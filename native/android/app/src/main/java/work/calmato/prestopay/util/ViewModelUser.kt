package work.calmato.prestopay.util

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.network.*

class ViewModelUser(application: Application) : AndroidViewModel(application) {
  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())


  fun registerDeviceId(registerDeviceIdProperty: RegisterDeviceIdProperty, activity: Activity) {
    val id = sharedPreferences.getString("token", null)
    val token = "Bearer $id"
    Api.retrofitService
      .registerDeviceId(token, registerDeviceIdProperty)
      .enqueue(object : Callback<AccountResponse> {
        override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
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
