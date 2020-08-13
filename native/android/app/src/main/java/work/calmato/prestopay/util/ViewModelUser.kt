package work.calmato.prestopay.util

import android.app.Activity
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.network.*

class ViewModelUser(application: Application) : AndroidViewModel(application) {
  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private val id = sharedPreferences.getString("token", null)
  private val token = "Bearer $id"

  fun registerDeviceId(registerDeviceIdProperty: RegisterDeviceIdProperty, activity: Activity) {
    Api.retrofitService
      .registerDeviceId(token, registerDeviceIdProperty)
      .enqueue(object : Callback<AccountResponse> {
        override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
        }

        override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
          if (!response.isSuccessful) {
            Log.i(ViewModelUser.TAG, "There is some error on onResponse")
          }
        }
      })
  }

  class Factory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ViewModelUser::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return ViewModelUser(app) as T
      }

      throw IllegalArgumentException("Unable to construct ViewModel")
    }
  }

  companion object {
    internal const val TAG = "ViewModelUser"
  }
}
