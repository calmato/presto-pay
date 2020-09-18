package work.calmato.prestopay.util

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

  private val _refreshing = MutableLiveData<Boolean>()
  val refreshing: LiveData<Boolean>
    get() = _refreshing

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelGroupJob = SupervisorJob()

  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelGroupJob + Dispatchers.Main)

  private val database = getAppDatabase(application)
  private val groupsRepository = GroupsRepository(database)
  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private var id = sharedPreferences.getString("token", null)

  fun groupListView() {
    id = sharedPreferences.getString("token", null)
    viewModelScope.launch {
      try {
        startRefreshing()
        groupsRepository.refreshGroups(id!!)
      } catch (e: java.lang.Exception) {
        Log.i(TAG, "${e.message}")
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

  fun navigationCompleted(){
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
