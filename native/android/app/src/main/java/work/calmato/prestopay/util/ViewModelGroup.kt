package work.calmato.prestopay.util

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.repository.GroupsRepository

class ViewModelGroup(application: Application) : AndroidViewModel(application) {
  private val _itemClickedGroup = MutableLiveData<GroupPropertyResponse>()
  val itemClickedGroup: LiveData<GroupPropertyResponse>
    get() = _itemClickedGroup

  private val _groupList = MutableLiveData<List<GroupPropertyResponse>>()
  val groupList: LiveData<List<GroupPropertyResponse>>
    get() = _groupList

  private val _nowLoading = MutableLiveData<Boolean>()
  val nowLoading: LiveData<Boolean>
    get() = _nowLoading

  private val _refreshing = MutableLiveData<Boolean>()
  val refreshing: LiveData<Boolean>
    get() = _refreshing

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelGroupJob = SupervisorJob()

  private val database = getAppDatabase(application)
  private val groupsRepository = GroupsRepository(database)
  private var id = MainActivity.firebaseId//sharedPreferences.getString("token", null)

  fun groupListView() {
    id = MainActivity.firebaseId
    viewModelScope.launch {
      try {
        startRefreshing()
        groupsRepository.refreshGroups(id)
      } catch (e: java.lang.Exception) {
        endRefreshing()
      }
    }
  }

  val groupsList = groupsRepository.groups

  override fun onCleared() {
    super.onCleared()
    viewModelGroupJob.cancel()
  }

  fun itemIsClickedGroup(group: GroupPropertyResponse) {
    _itemClickedGroup.value = group
  }

  fun navigationCompleted() {
    _itemClickedGroup.value = null
  }

  private fun startRefreshing() {
    _refreshing.value = true
  }

  fun endRefreshing() {
    _refreshing.value = false
  }

  companion object {
    internal const val TAG = "ViewModelGroup"
  }

}
