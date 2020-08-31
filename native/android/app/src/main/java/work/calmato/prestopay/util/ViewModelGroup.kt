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
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GetGroupDetail
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.repository.GroupsRepository

class ViewModelGroup(application: Application) : AndroidViewModel(application) {
  private val _itemClickedGroup = MutableLiveData<GroupPropertyResponse>()
  val itemClickedGroup: LiveData<GroupPropertyResponse>
    get() = _itemClickedGroup

  private val _groupList = MutableLiveData<List<GroupPropertyResponse>>()
  val groupList: LiveData<List<GroupPropertyResponse>>
    get() = _groupList

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelGroupJob = SupervisorJob()

  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelGroupJob + Dispatchers.Main)

  private val database = getAppDatabase(application)
  private val groupsRepository = GroupsRepository(database)
  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private val id = sharedPreferences.getString("token", null)

  fun groupListView() {
    viewModelScope.launch {
      try {
        groupsRepository.refreshGroups(id!!)
      } catch (e: java.lang.Exception) {
        Log.i(TAG, "Trying to refresh id")
      }
    }
  }

  val groupsList = groupsRepository.groups

  override fun onCleared() {
    super.onCleared()
    viewModelGroupJob.cancel()
  }

  fun getGroupDetail(groupId: String, activity: Activity) : GetGroupDetail? {
    var groupDetail: GetGroupDetail? = null
    Api.retrofitService.getGroupDetail("Bearer ${id}", groupId)
      .enqueue(object: Callback<GetGroupDetail> {
        override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
        }

        override fun onResponse(call: Call<GetGroupDetail>, response: Response<GetGroupDetail>) {
          Log.d(TAG, response.body()!!.name)
          if (response.isSuccessful) {
            groupDetail = response.body()!!
          }
        }
      })
    return groupDetail
  }

  fun itemIsClickedGroup(group: GroupPropertyResponse) {
    _itemClickedGroup.value = group
  }

  class Factory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ViewModelGroup::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return ViewModelGroup(app) as T
      }
      throw IllegalArgumentException("Unable to construct viewmodel")
    }
  }

  companion object {
    internal const val TAG = "AddGroupViewModel"
  }

}
