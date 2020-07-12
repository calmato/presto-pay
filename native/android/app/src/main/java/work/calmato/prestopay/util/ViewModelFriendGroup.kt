package work.calmato.prestopay.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import work.calmato.prestopay.network.*

class ViewModelFriendGroup : ViewModel() {
  private val _itemClicked = MutableLiveData<UserProperty>()
  val itemClicked: LiveData<UserProperty>
    get() = _itemClicked

  private val _idToken = MutableLiveData<String>()
  val idToken: LiveData<String>
    get() = _idToken

  fun getUserProperties(userName: String): Users? {
    var users: Users? = null
    val getProperties = Api.retrofitService.getProperties("Bearer ${idToken.value}", userName)
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

  fun addFriendApi(userProperty: UserProperty): Boolean {
    val userId = UserId(userProperty.id)
    val sendFriendRequest = Api.retrofitService.addFriend("Bearer ${idToken.value}", userId)
    var returnBool = false
    val thread = Thread(Runnable {
      try {
        val result = sendFriendRequest.execute()
        returnBool = result.isSuccessful
      } catch (e: Exception) {
        Log.i(TAG, "getUserProperties: Fail")
      }
    })
    thread.start()
    thread.join()
    return returnBool
  }

  fun createGroupApi(groupProperty:CreateGroupProperty):Boolean{
    val createGroupRequest = Api.retrofitService.createGroup("Bearer ${idToken.value}", groupProperty)
    var returnBool = false
    val thread = Thread(Runnable {
      try {
          val result = createGroupRequest.execute()
        returnBool = result.isSuccessful
      } catch (e:Exception){
        Log.i(TAG, "CreateGroupProperties: Fail")
      }
    })
    thread.start()
    thread.join()
    return returnBool
  }

  fun getFriends(): Users? {
    var users: Users? = null
    val getFriends = Api.retrofitService.getFriends("Bearer ${idToken.value}")
    val thread = Thread(Runnable {
      try {
        users = getFriends.execute().body()
      } catch (e: java.lang.Exception) {
        Log.i(TAG, "getUserProperties: Fail")
      }
    })
    thread.start()
    thread.join()
    return users
  }

  fun itemIsClicked(userProperty: UserProperty) {
    _itemClicked.value = userProperty
  }

  fun itemIsClickedCompleted() {
    _itemClicked.value = null
  }

  fun getIdToken() {
    FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
      _idToken.value = it.result?.token
    }
  }

  companion object {
    internal const val TAG = "AddFriendViewModel"
  }

}
