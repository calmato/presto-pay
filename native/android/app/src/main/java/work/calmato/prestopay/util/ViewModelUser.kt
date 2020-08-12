package work.calmato.prestopay.util

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.network.*

class ViewModelUser(application: Application) : AndroidViewModel(application) {
  private val id = sharedPreferences.getString("token", null)

  fun registerDeviceId(registerDeviceIdProperty: RegisterDeviceIdProperty, activity: Activity) {
    Api.retrofitService
      .registerDeviceId("Bearer ${id}", registerDeviceIdProperty)
      .enqueue(object : callback<AccountResponse>) {
        override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
        }
      }
  }

  class Factory(val app: Application) : VIewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ViewModelUser::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return ViewModelUser(app) as T
      }

      throw IllegalArgumentException("Unable to construct viewmodel")
    }
  }
}
