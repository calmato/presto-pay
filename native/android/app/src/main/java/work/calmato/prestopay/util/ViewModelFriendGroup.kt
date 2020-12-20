package work.calmato.prestopay.util

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.database.DatabaseGroup
import work.calmato.prestopay.database.DatabaseFriend
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.FriendsRepository
import work.calmato.prestopay.repository.GroupsRepository
import kotlin.concurrent.thread

class ViewModelFriendGroup(application: Application) : AndroidViewModel(application) {
  private val _itemClicked = MutableLiveData<UserProperty>()
  val itemClicked: LiveData<UserProperty>
    get() = _itemClicked

  private val _navigateToHome = MutableLiveData<Boolean>()
  val navigateToHome: LiveData<Boolean>
    get() = _navigateToHome

  private val _usersList = MutableLiveData<List<UserProperty>>()
  val usersList: LiveData<List<UserProperty>>
    get() = _usersList

  private val _itemClickedGroup = MutableLiveData<GroupPropertyResponse>()
  val itemClickedGroup: LiveData<GroupPropertyResponse>
    get() = _itemClickedGroup

  private val _nowLoading = MutableLiveData<Boolean>()
  val nowLoading: LiveData<Boolean>
    get() = _nowLoading

  private val _refreshingGroup = MutableLiveData<Boolean>()
  val refreshingGroup: LiveData<Boolean>
    get() = _refreshingGroup

  private val _refreshingFriend = MutableLiveData<Boolean>()
  val refreshingFriend: LiveData<Boolean>
    get() = _refreshingFriend

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = SupervisorJob()

  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

  private val database = getAppDatabase(application)
  private val friendsRepository = FriendsRepository(database)
  private val groupsRepository = GroupsRepository(database)
  private val id = MainActivity.firebaseId
  val friendsList = friendsRepository.friends
  val groupsList = groupsRepository.groups
  private var databaseFriends:List<DatabaseFriend>? = null

  fun userListView() {
    startRefreshingFriend()
    viewModelScope.launch {
      try {
        friendsRepository.refreshFriends(id)
        endRefreshingFriend()
      } catch (e: java.lang.Exception) {
        Log.i(TAG, "Trying to refresh id")
        endRefreshingFriend()
      }
    }
  }

  fun groupListView() {
    startRefreshingGroup()
    viewModelScope.launch {
      try {
        groupsRepository.refreshGroups(id)
        endRefreshingGroup()
      } catch (e: java.lang.Exception) {
        Log.i(TAG, "Trying to refresh id")
        endRefreshingGroup()
      }
    }
  }

  fun getDatabaseFriendList(){
    CoroutineScope(Dispatchers.IO).launch {
      databaseFriends = database.friendDao.getFriendsList()
    }
  }

  fun getUserProperties(userName: String) {
    _nowLoading.value = true
    Log.i(TAG, "getUserProperties: $friendsList")
    coroutineScope.launch {
        try {
          val networkUsers =
            Api.retrofitService.getPropertiesAsync("Bearer $id", userName).await().asDomainModel()
          databaseFriends?.also {friends ->
            _usersList.value = networkUsers.filter { user ->
              !friends.map { it.id }.contains(user.id)
            }
          } ?: run{
            _usersList.value = networkUsers
          }
          _nowLoading.value = false
        } catch (e: Exception) {
          _nowLoading.value = false
        }
    }
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun addFriendApi(userProperty: UserProperty) {
    _nowLoading.value = true
    val userId = userProperty.id
    viewModelScope.launch {
      try {
        friendsRepository.addFriend(id, UserId(userId), userProperty)
        _nowLoading.value = false
      } catch (e: java.lang.Exception) {
        Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
        _nowLoading.value = false
      }
    }
  }

  fun createGroupApi(groupProperty: CreateGroupProperty) {
    _nowLoading.value = true
    Api.retrofitService.createGroup("Bearer $id", groupProperty)
      .enqueue(object : Callback<GroupPropertyResponse> {
        override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
          Toast.makeText(getApplication(), t.message, Toast.LENGTH_LONG).show()
          _nowLoading.value = false
        }

        override fun onResponse(
          call: Call<GroupPropertyResponse>,
          response: Response<GroupPropertyResponse>
        ) {
          if (response.isSuccessful) {
            val responseGroup = response.body()!!
            thread {
              database.groupDao.insertGroup(DatabaseGroup(
                id = responseGroup.id,
                name = responseGroup.name,
                createdAt = responseGroup.createdAt,
                thumbnailUrl = responseGroup.thumbnailUrl,
                updateAt = responseGroup.updatedAt,
                userIds = responseGroup.userIds
              ))
            }
            _navigateToHome.value = true
          } else {
            Toast.makeText(
              getApplication(),
              getApplication<Application>().resources.getString(R.string.create_group_failed),
              Toast.LENGTH_LONG
            ).show()
          }
          _nowLoading.value = false
        }
      })
  }

  fun deleteFriendSwipe(friendId: String) {
    _nowLoading.value = true

    coroutineScope.launch {
      try {
        viewModelScope.launch {
          friendsRepository.deleteFriend(friendId)
        }
      } catch (e: java.lang.Exception) {
        Toast.makeText(
          getApplication(),
          getApplication<Application>().resources.getString(R.string.delete_friend_failed),
          Toast.LENGTH_LONG
        ).show()
        Log.i(TAG, "onResponse: ${e.message}")
      }
    }
    _nowLoading.value = false
  }

  fun deleteGroup(groupId: String) {
    _nowLoading.value = true

    coroutineScope.launch {
      try {
        viewModelScope.launch {
          groupsRepository.deleteGroup(groupId)
        }
      } catch (e: java.lang.Exception) {
        Toast.makeText(
          getApplication(),
          getApplication<Application>().resources.getString(R.string.delete_group_failed),
          Toast.LENGTH_LONG
        ).show()
        Log.i(TAG, "onResponse: ${e.message}")
      }
    }
    _nowLoading.value = false
  }

  fun itemIsClicked(userProperty: UserProperty) {
    _itemClicked.value = userProperty
  }

  fun itemIsClickedGroup(group: GroupPropertyResponse) {
    _itemClickedGroup.value = group
  }

  fun itemIsClickedCompleted() {
    _itemClicked.value = null
  }

  fun navigationCompleted() {
    _navigateToHome.value = false
  }

  fun startRefreshingGroup() {
    _refreshingGroup.value = true
  }

  fun endRefreshingGroup() {
    _refreshingGroup.value = false
  }

  private fun startRefreshingFriend() {
    _refreshingFriend.value = true
  }

  private fun endRefreshingFriend() {
    _refreshingFriend.value = false
  }

  companion object {
    internal const val TAG = "AddFriendViewModel"
  }

}
