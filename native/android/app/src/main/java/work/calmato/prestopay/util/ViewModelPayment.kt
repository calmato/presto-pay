package work.calmato.prestopay.util

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.repository.PaymentRepository

class ViewModelPayment(application: Application) : AndroidViewModel(application) {
  private val _itemClicked = MutableLiveData<PaymentPropertyGet>()
  val itemClicked: LiveData<PaymentPropertyGet>
    get() = _itemClicked

  private var viewModelJob = SupervisorJob()
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
  private val database = getAppDatabase(application)
  private var paymentRepository:PaymentRepository? = null
  private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
  private val id = sharedPreferences.getString("token", null)
  var paymentsList:LiveData<List<PaymentPropertyGet>>? = null
  private val _refreshing = MutableLiveData<Boolean>()
  val refreshing: LiveData<Boolean>
    get() = _refreshing

  fun setInitPaymentList(groupId: String){
    paymentRepository = PaymentRepository(database,groupId)
    paymentsList = paymentRepository!!.payments
  }

  fun getPayments(groupId: String) {
    startRefreshing()
    viewModelScope.launch {
      try {
      paymentRepository!!.refreshPayments(id!!, groupId)
      endRefreshing()
      } catch (e:java.lang.Exception){
        endRefreshing()
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  private fun startRefreshing() {
    _refreshing.value = true
  }

  private fun endRefreshing() {
    _refreshing.value = false
  }

  fun itemIsClicked(payment : PaymentPropertyGet){
    _itemClicked.value = payment
  }

  fun navigationCompleted(){
    _itemClicked.value = null
  }
}
