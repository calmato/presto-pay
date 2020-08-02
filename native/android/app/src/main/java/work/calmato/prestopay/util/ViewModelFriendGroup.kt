package work.calmato.prestopay.util

import android.app.Activity
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.FriendsRepository
import work.calmato.prestopay.repository.GroupsRepository

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
  val nowLoading : LiveData<Boolean>
    get() = _nowLoading

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = SupervisorJob()

  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

  private val database = getAppDatabase(application)
  private val friendsRepository = FriendsRepository(database)
  private val groupsRepository = GroupsRepository(database)
  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private val id = sharedPreferences.getString("token", null)

  fun userListView() {
    viewModelScope.launch {
      try {
        friendsRepository.refreshFriends(id!!)
      } catch (e:java.lang.Exception){
        Log.i(TAG, "Trying to refresh id")
      }
    }
  }

  fun groupListView() {
    viewModelScope.launch {
      try {
        groupsRepository.refreshGroups(id!!)
      } catch (e:java.lang.Exception){
        Log.i(TAG, "Trying to refresh id")
      }
    }
  }

  val friendsList = friendsRepository.friends
  val groupsList = groupsRepository.groups

  fun getUserProperties(userName: String, activity: Activity) {
    _nowLoading.value = true
    coroutineScope.launch {
      try {
        _usersList.value =
          Api.retrofitService.getPropertiesAsync("Bearer $id", userName).await().asDomainModel()
        _nowLoading.value = false
      } catch (e: Exception) {
        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        _nowLoading.value = false
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun addFriendApi(userProperty: UserProperty, activity: Activity) {
    _nowLoading.value = true
    val userId = userProperty.id
    viewModelScope.launch {
      try {
        friendsRepository.addFriend(id, UserId(userId), userProperty)
        Toast.makeText(activity, "友だち追加しました", Toast.LENGTH_SHORT).show()
        _nowLoading.value = false
      } catch (e: java.lang.Exception) {
        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        _nowLoading.value = false
      }
    }
  }

  fun createGroupApi(groupProperty: CreateGroupProperty, activity: Activity) {
    _nowLoading.value = true
    Api.retrofitService.createGroup("Bearer ${id}", groupProperty)
      .enqueue(object : Callback<GroupPropertyResponse> {
        override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
          _nowLoading.value = false
        }

        override fun onResponse(
          call: Call<GroupPropertyResponse>,
          response: Response<GroupPropertyResponse>
        ) {
          if (response.isSuccessful) {
            Toast.makeText(activity, "新しいグループを作成しました", Toast.LENGTH_SHORT).show()
            _navigateToHome.value = true
          } else {
            Toast.makeText(activity, "グループ作成に失敗しました", Toast.LENGTH_LONG).show()
          }
          _nowLoading.value = false
        }
      })
  }

  fun deleteFriend(userId: String, activity: Activity) {
    _nowLoading.value = true
    Api.retrofitService.deleteFriend("Bearer ${id}", userId)
      .enqueue(object : Callback<AccountResponse> {
        override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
          _nowLoading.value = false
        }

        override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
          if (response.isSuccessful) {
            coroutineScope.launch {
              try {
                viewModelScope.launch {
                  friendsRepository.deleteFriend(userId)
                  Toast.makeText(activity, "友達リストから削除しました", Toast.LENGTH_LONG).show()
                }
              } catch (e: java.lang.Exception) {
                Toast.makeText(activity, "友達削除に失敗しました", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onResponse: ${e.message}")
              }
            }
          } else {
            Toast.makeText(activity, "友達削除に失敗しました", Toast.LENGTH_LONG).show()
          }
          _nowLoading.value = false
        }
      })
  }

  fun itemIsClicked(userProperty: UserProperty) {
    _itemClicked.value = userProperty
  }

  fun itemIsClickedGroup(group:GroupPropertyResponse) {
    _itemClickedGroup.value = group
  }

  fun itemIsClickedCompleted() {
    _itemClicked.value = null
  }

  fun navigationCompleted() {
    _navigateToHome.value = false
  }

  /**
   * Factory for constructing DevByteViewModel with parameter
   */
  class Factory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ViewModelFriendGroup::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return ViewModelFriendGroup(app) as T
      }
      throw IllegalArgumentException("Unable to construct viewmodel")
    }
  }

  companion object {
    internal const val TAG = "AddFriendViewModel"
  }

}
