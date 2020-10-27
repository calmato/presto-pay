package work.calmato.prestopay.util

import android.app.Activity
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.HiddenGroups
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

  fun navigationCompleted() {
    _itemClickedGroup.value = null
  }

  fun itemIsClickedCompleted() {
    _itemClickedGroup.value = null
  }

  private fun startRefreshing() {
    _refreshing.value = true
  }

  fun endRefreshing() {
    _refreshing.value = false
  }

  fun deleteHiddenGroup(groupId: String, activity: Activity) {
    var hiddenGroups: HiddenGroups? = null
    _nowLoading.value = true
    Api.retrofitService.deleteHiddenGroup("Bearer ${id}", groupId)
      .enqueue(object : Callback<HiddenGroups> {
        override fun onResponse(call: Call<HiddenGroups>, response: Response<HiddenGroups>) {
          Log.d(TAG, response.body().toString())
          hiddenGroups = response.body()
          Log.d(TAG, hiddenGroups.toString())
          _nowLoading.value = false
        }

        override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
          Log.d(TAG, t.message)
          _nowLoading.value = false
        }
      })
  }

  companion object {
    internal const val TAG = "ViewModelGroup"
  }

}
