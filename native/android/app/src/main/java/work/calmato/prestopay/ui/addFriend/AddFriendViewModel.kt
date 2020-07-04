package work.calmato.prestopay.ui.addFriend

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.userProperty
import java.lang.Exception

enum class ApiStatus { LOADING, ERROR, DONE }
class AddFriendViewModel:ViewModel(){
  // The internal MutableLiveData that stores the status of the most recent request
  private val _status = MutableLiveData<ApiStatus>()
  // The external immutable LiveData for the request status
  val status: LiveData<ApiStatus>
    get() = _status

  // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
  // with new values
  private val _userProperties = MutableLiveData<List<userProperty>>()
  // The external LiveData interface to the property is immutable, so only this class can modify
  val userProperties:LiveData<List<userProperty>>
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
  fun getUserProperties(userName:String,idToken:String){
    coroutineScope.launch{
      var getPropertiesDeferred = Api.retrofitService.getProperties(userName)
      try{
        _status.value = ApiStatus.LOADING
        val listResult = getPropertiesDeferred.await()
        _status.value = ApiStatus.DONE
        _userProperties.value = listResult
      } catch (e:Exception){
        _status.value = ApiStatus.ERROR
        _userProperties.value = ArrayList()
      }
    }
  }
  /**
   * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
   * Retrofit service to stop.
   */
  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

}
