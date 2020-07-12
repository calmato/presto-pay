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

  private val _navigateToHome = MutableLiveData<Boolean>()
  val navigateToHome:LiveData<Boolean>
    get() = _navigateToHome

  private val _usersList = MutableLiveData<Users>()
  val usersList:LiveData<Users>
    get() = _usersList

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = Job()

  // the Coroutine runs using the Main (UI) dispatcher
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

  fun getUserProperties(userName: String,activity: Activity){
    coroutineScope.launch {
      try {
      _usersList.value   = Api.retrofitService.getPropertiesAsync("Bearer ${idToken.value}", userName).await()
      } catch (e:Exception){
        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
      }
    }
  }
  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun addFriendApi(userProperty: UserProperty,activity:Activity){
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
  }

  fun createGroupApi(groupProperty: CreateGroupProperty,activity: Activity) {
    Api.retrofitService.createGroup("Bearer ${idToken.value}", groupProperty).enqueue(object:Callback<CreateGroupPropertyResponse>{
      override fun onFailure(call: Call<CreateGroupPropertyResponse>, t: Throwable) {
        Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
      }
      override fun onResponse(
        call: Call<CreateGroupPropertyResponse>,
        response: Response<CreateGroupPropertyResponse>
      ) {
        if(response.isSuccessful){
          Toast.makeText(activity, "新しいグループを作成しました", Toast.LENGTH_SHORT).show()
          _navigateToHome.value = true
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
          }catch (e: java.lang.Exception){
            Toast.makeText(activity, "グループ作成に失敗しました", Toast.LENGTH_LONG).show()
          }
        }
      }
    })
  }

  fun getFriends(activity: Activity){
    coroutineScope.launch {
      try {
          _usersList.value = Api.retrofitService.getFriendsAsync("Bearer ${idToken.value}").await()
        Log.i(TAG, "getFriends: getUser")
      }catch (e:java.lang.Exception){
        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
      }
    }
  }

  fun deleteFriend(userId:String, activity: Activity){
    Api.retrofitService.deleteFriend("Bearer ${idToken.value}", userId).enqueue(object:Callback<AccountResponse>{
      override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
        Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
      }

      override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
        if(response.isSuccessful){
          Toast.makeText(activity,"友達リストから削除しました",Toast.LENGTH_LONG).show()
          getFriends(activity)
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
          }catch (e: java.lang.Exception){
            Toast.makeText(activity, "友達削除に失敗しました", Toast.LENGTH_LONG).show()
          }
        }
      }
    })
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

  fun navigationCompleted(){
    _navigateToHome.value = false
  }

  companion object {
    internal const val TAG = "AddFriendViewModel"
  }

}
