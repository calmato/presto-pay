package work.calmato.prestopay.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import work.calmato.prestopay.network.NationalFlag
import work.calmato.prestopay.network.NetworkPayer

class ViewModelAddPayment(application: Application): AndroidViewModel(application) {
  private val _groupName = MutableLiveData<String>()
  val groupName: LiveData<String>
    get() = _groupName

  private val _paymentName = MutableLiveData<String>()
  val paymentName: LiveData<String>
    get() = _paymentName

  private val _currency = MutableLiveData<NationalFlag>()
  val currency : LiveData<NationalFlag>
    get() = _currency

  private val _total = MutableLiveData<Float>()
  val total: LiveData<Float>
    get() = _total

  private val _payers = MutableLiveData<List<NetworkPayer>>()
  val payers : LiveData<List<NetworkPayer>>
    get() = _payers

  private val _navigateToPaymentComfirmation = MutableLiveData<Boolean>()
  val navigateToPaymentComfirmation: LiveData<Boolean>
    get() = _navigateToPaymentComfirmation

  private var viewModelJob = SupervisorJob()
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

  fun setGroupName(name:String){
    _groupName.value = name
  }

  fun setPaymentName(name: String){
    _paymentName.value = name
  }
}
