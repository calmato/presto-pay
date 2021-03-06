package work.calmato.prestopay.util

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.database.DatabaseGroup
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.NationalFlag
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.repository.GroupsRepository
import work.calmato.prestopay.repository.NationalFlagsRepository
import work.calmato.prestopay.repository.PaymentRepository

class ViewModelPayment(application: Application) : AndroidViewModel(application) {
  private val _itemClicked = MutableLiveData<PaymentPropertyGet>()
  val itemClicked: LiveData<PaymentPropertyGet>
    get() = _itemClicked

  private var viewModelJob = SupervisorJob()
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
  private val database = getAppDatabase(application)
  private var paymentRepository: PaymentRepository? = null
  private var groupRepository: GroupsRepository? = null
  private val id = MainActivity.firebaseId
  var paymentsList: LiveData<List<PaymentPropertyGet>>? = null
  var groupStatus: LiveData<DatabaseGroup>? = null
  private val _refreshing = MutableLiveData<Boolean>()
  val refreshing: LiveData<Boolean>
    get() = _refreshing

  private val _nowLoading = MutableLiveData<Boolean>()
  val nowLoading: LiveData<Boolean>
    get() = _nowLoading

  private val _countryList = MutableLiveData<List<NationalFlag>>()
  val countryList: LiveData<List<NationalFlag>>
    get() = _countryList

  fun getCountryList() {
    CoroutineScope(Dispatchers.IO).launch {
      _countryList.postValue(NationalFlagsRepository(getAppDatabase(getApplication())).nationalFlags)
    }
  }
  fun setInitPaymentList(groupId: String) {
    paymentRepository = PaymentRepository(database, groupId)
    groupRepository = GroupsRepository(database)
    paymentsList = paymentRepository!!.payments
    groupStatus = paymentRepository!!.status
  }

  fun getPayments(groupId: String) {
    startRefreshing()
    viewModelScope.launch {
      try {
        paymentRepository!!.refreshPayments(id, groupId)
        endRefreshing()
      } catch (e: java.lang.Exception) {
        endRefreshing()
      }
    }
  }

  fun deletePayment(paymentId: String) {
    _nowLoading.value = true

    coroutineScope.launch {
      try {
        viewModelScope.launch {
          paymentRepository!!.deletePayment(paymentId)
        }
      } catch (e: java.lang.Exception) {
        Toast.makeText(
          getApplication(),
          getApplication<Application>().resources.getString(R.string.delete_friend_failed),
          Toast.LENGTH_LONG
        ).show()
      }
    }
    _nowLoading.value = false
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

  fun itemIsClicked(payment: PaymentPropertyGet) {
    _itemClicked.value = payment
  }

  fun navigationCompleted() {
    _itemClicked.value = null
  }
}
