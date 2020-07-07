package work.calmato.prestopay.ui.addFriend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.UserId
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users

class AddFriendViewModel : ViewModel() {
  private val _itemClicked = MutableLiveData<UserProperty>()
  val itemClicked: LiveData<UserProperty>
    get() = _itemClicked

  fun getUserProperties(userName: String, idToken: String): Users? {
    var users: Users? = null
    val getProperties = Api.retrofitService.getProperties("Bearer $idToken", userName)
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

  fun addFriendApi(userProperty: UserProperty, idToken: String):Boolean{
    val userId = UserId(userProperty.id)
    val sendFriendRequest = Api.retrofitService.addFriend("Bearer $idToken", userId)
    var returnBool = false
    val thread = Thread(Runnable {
      try {
        val user = sendFriendRequest.execute().body()
        returnBool = true
        Log.i(TAG, "getUserProperties:kore ${user!!}")
      } catch (e: Exception) {
        Log.i(TAG, "getUserProperties: Fail")
      }
    })
    thread.start()
    thread.join()
    return returnBool
  }

  fun displayDialog(userProperty: UserProperty) {
    _itemClicked.value = userProperty
  }

  fun displayDialogCompleted() {
    _itemClicked.value = null
  }

  companion object {
    internal const val TAG = "AddFriendViewModel"
  }

}
