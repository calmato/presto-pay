package work.calmato.prestopay.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

  fun addFriendApi(userProperty: UserProperty,activity:Activity): Boolean {
    val userId = UserId(userProperty.id)
    Api.retrofitService.addFriend("Bearer ${idToken.value}", userId).enqueue(object:Callback<AddFriendResponse>{
      override fun onFailure(call: Call<AddFriendResponse>, t: Throwable) {
        Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
      }
      override fun onResponse(
        call: Call<AddFriendResponse>,
        response: Response<AddFriendResponse>
      ) {
        if(response.isSuccessful){
          Toast.makeText(activity, "友だち追加しました", Toast.LENGTH_SHORT).show()
        }else{
          try {
            val jObjError = JSONObject(response.errorBody()?.string()).getJSONArray("errors")
            for (i in 0 until jObjError.length()) {
              val errorMessage =
                jObjError.getJSONObject(i).getString("field") + " " + jObjError.getJSONObject(
                  i
                )
                  .getString("message")
              Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
          }catch (e:java.lang.Exception){
            Toast.makeText(activity, "友だち追加失敗しました", Toast.LENGTH_SHORT).show()
          }
        }
      }
    })


    val sendFriendRequest = Api.retrofitService.addFriend("Bearer ${idToken.value}", userId)
    var returnBool = false
    val thread = Thread(Runnable {
      try {
        val result = sendFriendRequest.execute()
        returnBool = result.isSuccessful
        Log.i(TAG, "addFriendApi: ${result.isSuccessful}")
      } catch (e: Exception) {
        Log.i(TAG, "getUserProperties: Fail")
      }
    })
    thread.start()
    thread.join()
    return returnBool
  }

  fun createGroupApi(groupProperty: CreateGroupProperty): Boolean {
    val createGroupRequest =
      Api.retrofitService.createGroup("Bearer ${idToken.value}", groupProperty)
    var returnBool = false
    val thread = Thread(Runnable {
      try {
        val result = createGroupRequest.execute()
        returnBool = result.isSuccessful
      } catch (e: Exception) {
        Log.i(TAG, "CreateGroupProperties: Fail. ログのOkhttpをチェック")
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
