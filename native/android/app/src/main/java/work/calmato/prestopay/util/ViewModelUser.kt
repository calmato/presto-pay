package work.calmato.prestopay.util

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.network.*

class ViewModelUser(application: Application) : AndroidViewModel(application) {

  fun registerDeviceId(registerDeviceIdProperty: RegisterDeviceIdProperty) {
    val id = MainActivity.firebaseId
    val token = "Bearer $id"
    Api.retrofitService
      .registerDeviceId(token, registerDeviceIdProperty)
      .enqueue(object : Callback<AccountResponse> {
        override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
          Log.i("TAG", t.message ?: "")
        }

        override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
          if (!response.isSuccessful) {
            Log.i(TAG, "There is some error on onResponse")
          }
        }
      })
  }

  companion object {
    internal const val TAG = "ViewModelUser"
  }
}
