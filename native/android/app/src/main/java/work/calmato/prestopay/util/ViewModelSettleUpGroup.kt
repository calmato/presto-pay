package work.calmato.prestopay.util

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.NetworkPayer
import retrofit2.Call

class ViewModelSettleUpGroup(application: Application) : AndroidViewModel(application) {

  private val _usersPaymentInfo = MutableLiveData<List<NetworkPayer>>()
  val usersPaymentInfo: LiveData<List<NetworkPayer>>
    get() = _usersPaymentInfo

  private val _navigateBack = MutableLiveData<Boolean>()
  val navigateBack: LiveData<Boolean>
    get() = _navigateBack

  private val _nowLoading = MutableLiveData<Boolean>()
  val nowLoading: LiveData<Boolean>
    get() = _nowLoading

  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private val id = sharedPreferences.getString("token", null)


  fun setUsersPaymentInfo(list: List<NetworkPayer>) {
    _usersPaymentInfo.value = list
  }

  fun settleUpApi(groupId: String) {
    _nowLoading.value = true
    Api.retrofitService.settleUp("Bearer $id", groupId)
      .enqueue(object : Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
          if (response.isSuccessful) {
            _navigateBack.value = true
          } else {
            Toast.makeText(getApplication(), "精算登録に失敗しました", Toast.LENGTH_LONG).show()
          }
          _nowLoading.value = false
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
          Toast.makeText(getApplication(), "精算登録に失敗しました", Toast.LENGTH_LONG).show()
          _nowLoading.value = false
        }
      })
  }
}
