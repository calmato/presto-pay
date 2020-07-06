package work.calmato.prestopay.ui.addFriend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.Users

class AddFriendViewModel:ViewModel(){
  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = Job()

  /**
   * Gets filtered Mars real estate property information from the API Retrofit service and
   * updates the [userProperties] [List] and [ApiStatus] [LiveData]. The Retrofit service
   * returns a coroutine Deferred, which we await to get the result of the transaction.
   */
  fun getUserProperties(userName:String,idToken:String): Users? {
    var users : Users? = null
      val getProperties = Api.retrofitService.getProperties("Bearer $idToken",userName)
      val thread = Thread(Runnable {
        try {
        users = getProperties.execute().body()
        Log.i(TAG, "getUserProperties: ${users!!}")
        } catch (e: Exception) {
          Log.i(TAG, "getUserProperties: Fail")
        }
      })
      thread.start()
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
