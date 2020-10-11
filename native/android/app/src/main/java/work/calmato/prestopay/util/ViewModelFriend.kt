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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.FriendsRepository

class ViewModelFriend(application:Application): AndroidViewModel(application) {
  private val _itemClicked = MutableLiveData<UserProperty>()
  val itemClicked: LiveData<UserProperty>
    get() = _itemClicked

  private val _navigateToHome = MutableLiveData<Boolean>()
  val navigateToHome: LiveData<Boolean>
    get() = _navigateToHome

  private val _usersList = MutableLiveData<List<UserProperty>>()
  val usersList: LiveData<List<UserProperty>>
    get() = _usersList

  private val _nowLoading = MutableLiveData<Boolean>()
  val nowLoading: LiveData<Boolean>
    get() = _nowLoading

  private val _refreshingFriend = MutableLiveData<Boolean>()
  val refreshingFriend: LiveData<Boolean>
    get() = _refreshingFriend

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = SupervisorJob()

  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

  private val database = getAppDatabase(application)
  private val friendsRepository = FriendsRepository(database)

  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private val id = sharedPreferences.getString("token", null)
  val friendsList = friendsRepository.friends
  fun userListView() {
    startRefreshingFriend()
    viewModelScope.launch {
      try {
        friendsRepository.refreshFriends(id!!)
        endRefreshingFriend()
      } catch (e: java.lang.Exception) {
        Log.i("ViewModelFriend", "Trying to refresh id")
        endRefreshingFriend()
      }
    }
  }

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
        Toast.makeText(activity, getApplication<Application>().resources.getString(R.string.add_friend_succeeded), Toast.LENGTH_SHORT).show()
        _nowLoading.value = false
      } catch (e: java.lang.Exception) {
        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        _nowLoading.value = false
      }
    }
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
                  Toast.makeText(activity, getApplication<Application>().resources.getString(R.string.delete_friend_succeeded), Toast.LENGTH_LONG).show()
                }
              } catch (e: java.lang.Exception) {
                Toast.makeText(activity, getApplication<Application>().resources.getString(R.string.delete_friend_failed), Toast.LENGTH_LONG).show()
                Log.i(ViewModelFriendGroup.TAG, "onResponse: ${e.message}")
              }
            }
          } else {
            Toast.makeText(activity, getApplication<Application>().resources.getString(R.string.delete_friend_failed), Toast.LENGTH_LONG).show()
          }
          _nowLoading.value = false
        }
      })
  }

  fun itemIsClicked(userProperty: UserProperty) {
    _itemClicked.value = userProperty
  }

  fun itemIsClickedCompleted() {
    _itemClicked.value = null
  }

  fun navigationCompleted() {
    _navigateToHome.value = false
  }

  private fun startRefreshingFriend(){
    _refreshingFriend.value = true
  }
  private fun endRefreshingFriend(){
    _refreshingFriend.value = false
  }

  /**
   * Factory for constructing DevByteViewModel with parameter
   */
  class Factory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ViewModelFriend::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return ViewModelFriend(app) as T
      }
      throw IllegalArgumentException("Unable to construct viewmodel")
    }
  }
}
