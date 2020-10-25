package work.calmato.prestopay.util

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.NationalFlagsRepository
import java.lang.Exception

class ViewModelAddPayment(application: Application): AndroidViewModel(application) {
  private val _groupName = MutableLiveData<String>()
  val groupName: LiveData<String>
    get() = _groupName

  private val _paymentName = MutableLiveData<String>()
  val paymentName: LiveData<String>
    get() = _paymentName

  private val _currency = MutableLiveData<String>()
  val currency : LiveData<String>
    get() = _currency

  private val _total = MutableLiveData<Float>()
  val total: LiveData<Float>
    get() = _total

  private val _payers = MutableLiveData<List<NetworkPayer>>()
  val payers : LiveData<List<NetworkPayer>>
    get() = _payers

  private val _payersAddPayment = MutableLiveData<List<PayerAddPayment>>()
  val payersAddPayment: LiveData<List<PayerAddPayment>>
    get() = _payersAddPayment

  private val _navigateToPaymentConfirmation = MutableLiveData<Boolean>()
  val navigateToPaymentConfirmation: LiveData<Boolean>
    get() = _navigateToPaymentConfirmation

  private val _itemClicked = MutableLiveData<PayerAddPayment>()
  val itemClicked: LiveData<PayerAddPayment>
    get() = _itemClicked

  lateinit var countryList: List<NationalFlag>
  lateinit var groupDetail: GetGroupDetail
  private lateinit var id:String
  lateinit var groupInfo: GroupPropertyResponse

  fun setGroupName(name:String){
    _groupName.value = name
  }

  fun setPaymentName(name: String){
    _paymentName.value = name
  }

  fun setTotal(total:Float){
    _total.value = total
  }

  fun setCurrency(currency: String){
    _currency.value = currency
  }

  fun itemIsClicked(payerAddPayment: PayerAddPayment) {
    _itemClicked.value = payerAddPayment
  }

  fun setPayersAddPayment(payers: List<UserProperty>){
    _payersAddPayment.value = payers.map {
      PayerAddPayment(
        it.id,it.name,it.thumbnailUrl!!,0f,false
      )
    }
  }

  fun getCountryList(){
    CoroutineScope(Dispatchers.IO).launch {
      countryList = NationalFlagsRepository(getAppDatabase(getApplication())).nationalFlags
    }
  }
  fun getGroupDetail(){
    CoroutineScope(Dispatchers.IO).launch {
      Api.retrofitService.getGroupDetail("Bearer $id", groupInfo.id)
        .enqueue(object : Callback<GetGroupDetail> {
          override fun onResponse(call: Call<GetGroupDetail>, response: Response<GetGroupDetail>) {
            try {
              groupDetail = response.body()!!
              setPayersAddPayment(groupDetail.users)
            } catch (e: Exception) {
              Log.i("ViewModelAddPayment", "onResponse: ${e.message}")
            }

          }

          override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
            Log.d("ViewModelAddPayment", t.message)
          }
        })
    }
  }
  fun setId(idInput:String){
    id = idInput
  }
  @JvmName("setGroupInfo1")
  fun setGroupInfo(groupInfoInput:GroupPropertyResponse){
    groupInfo = groupInfoInput
    setGroupName(groupInfo.name)
  }
}