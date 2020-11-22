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

class ViewModelFriend(application: Application) : AndroidViewModel(application) {
  private val _itemClicked = MutableLiveData<UserProperty>()
  val itemClicked: LiveData<UserProperty>
    get() = _itemClicked

  private val _nowLoading = MutableLiveData<Boolean>()
  val nowLoading: LiveData<Boolean>
    get() = _nowLoading

  private val _refreshingFriend = MutableLiveData<Boolean>()
  val refreshingFriend: LiveData<Boolean>
    get() = _refreshingFriend

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = SupervisorJob()
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

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun itemIsClicked(userProperty: UserProperty) {
    _itemClicked.value = userProperty
  }

  fun itemIsClickedCompleted() {
    _itemClicked.value = null
  }

  private fun startRefreshingFriend() {
    _refreshingFriend.value = true
  }

  private fun endRefreshingFriend() {
    _refreshingFriend.value = false
  }
}
