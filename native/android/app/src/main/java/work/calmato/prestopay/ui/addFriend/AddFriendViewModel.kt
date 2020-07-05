package work.calmato.prestopay.ui.addFriend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users
import java.lang.Exception
import kotlin.concurrent.thread

enum class ApiStatus { LOADING, ERROR, DONE }
class AddFriendViewModel:ViewModel(){
  // The internal MutableLiveData that stores the status of the most recent request
  private val _status = MutableLiveData<ApiStatus>()
  // The external immutable LiveData for the request status
  val status: LiveData<ApiStatus>
    get() = _status

  // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
  // with new values
  private val _userProperties = MutableLiveData<UserProperty>()
  // The external LiveData interface to the property is immutable, so only this class can modify
  val userProperties:LiveData<UserProperty>
    get() = _userProperties

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = Job()
  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

  /**
   * Gets filtered Mars real estate property information from the API Retrofit service and
   * updates the [userProperties] [List] and [ApiStatus] [LiveData]. The Retrofit service
   * returns a coroutine Deferred, which we await to get the result of the transaction.
   */
  fun getUserProperties(userName:String,idToken:String): Users? {
    var users : Users? = null
//    coroutineScope.launch{
      val getPropertries = Api.retrofitService.getProperties("Bearer $idToken",userName)
      val thread = Thread(Runnable {
//        try {
//          _status.value = ApiStatus.LOADING
        users = getPropertries.execute().body()
//          _status.value = ApiStatus.DONE
//          _userProperties.value = listResult.body()
        Log.i(TAG, "getUserProperties: ${users!!}")
//        } catch (e: Exception) {
//          _status.value = ApiStatus.ERROR
//          _userProperties.value = null
//          Log.i(TAG, "getUserProperties: Fail")
//        }
      })
      thread.start()
//    }
    thread.join()
    return users
  }
  /**
   * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
   * Retrofit service to stop.
   */
  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }
  companion object {
    internal const val TAG = "AddFriendViewModel"
  }
}
