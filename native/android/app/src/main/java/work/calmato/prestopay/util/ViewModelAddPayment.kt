package work.calmato.prestopay.util

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.NationalFlagsRepository
import work.calmato.prestopay.repository.TagRepository
import java.lang.Exception

class ViewModelAddPayment(application: Application) : AndroidViewModel(application) {
  private val _groupName = MutableLiveData<String>()
  val groupName: LiveData<String>
    get() = _groupName

  private val _paymentName = MutableLiveData<String>()
  val paymentName: LiveData<String>
    get() = _paymentName

  private val _currency = MutableLiveData<String>()
  val currency: LiveData<String>
    get() = _currency

  private val _total = MutableLiveData<Float>()
  val total: LiveData<Float>
    get() = _total

  private val _comment = MutableLiveData<String>()
  val comment: LiveData<String>
    get() = _comment

  private val _thumbnail = MutableLiveData<String>()
  val thumbnail: LiveData<String>
    get() = _thumbnail

  private val _paidAt = MutableLiveData<String>()
  val paidAt: LiveData<String>
    get() = _paidAt

  private val _payers = MutableLiveData<List<NetworkPayer>>()
  val payers: LiveData<List<NetworkPayer>>
    get() = _payers

  private val _payersAddPayment = MutableLiveData<List<PayerAddPayment>>()
  val payersAddPayment: LiveData<List<PayerAddPayment>>
    get() = _payersAddPayment

  private val _navigateToGroupDetail = MutableLiveData<GroupPropertyResponse>()
  val navigateToGroupDetail: LiveData<GroupPropertyResponse>
    get() = _navigateToGroupDetail

  private val _navigateToHome = MutableLiveData<Boolean>()
  val navigateToHome: LiveData<Boolean>
    get() = _navigateToHome

  private val _itemClicked = MutableLiveData<PayerAddPayment>()
  val itemClicked: LiveData<PayerAddPayment>
    get() = _itemClicked

  private val _lendersAddPayment = MutableLiveData<List<Float>>()
  val lendersAddPayment: LiveData<List<Float>>
    get() = _lendersAddPayment

  private val _borrowersAddPayment = MutableLiveData<List<Float>>()
  val borrowersAddPayment: LiveData<List<Float>>
    get() = _borrowersAddPayment

  private val _editPaymentPositivePayers = MutableLiveData<List<UserExpense>>()
  val editPaymentPositivePayers: LiveData<List<UserExpense>>
    get() = _editPaymentPositivePayers

  private val _editPaymentNegativePayers = MutableLiveData<List<UserExpense>>()
  val editPaymentNegativePayers: LiveData<List<UserExpense>>
    get() = _editPaymentNegativePayers

  lateinit var countryList: List<NationalFlag>
  lateinit var groupDetail: GetGroupDetail
  private lateinit var id: String
  lateinit var groupInfo: GroupPropertyResponse
  var paymentInfo: PaymentPropertyGet? = null
  lateinit var tags: List<Tag>

  fun setGroupName(name: String) {
    _groupName.value = name
  }

  @JvmName("setPaymentInfo1")
  fun setPaymentInfo(payment: PaymentPropertyGet) {
    paymentInfo = payment
  }

  fun setPaymentName(name: String) {
    _paymentName.value = name
  }

  fun setTotal(total: Float) {
    _total.value = total
  }

  fun setCurrency(currency: String) {
    _currency.value = currency
  }

  fun itemIsClicked(payerAddPayment: PayerAddPayment) {
    _itemClicked.value = payerAddPayment
  }

  fun setPayersAddPayment(payers: List<UserProperty>) {
    _payersAddPayment.value = payers.map {
      PayerAddPayment(
        it.id, it.name, it.thumbnailUrl!!, 0f, false
      )
    }
  }

  fun getCountryList() {
    CoroutineScope(Dispatchers.IO).launch {
      countryList = NationalFlagsRepository(getAppDatabase(getApplication())).nationalFlags
    }
  }

  fun getGroupDetail() {
    CoroutineScope(Dispatchers.IO).launch {
      Api.retrofitService.getGroupDetail("Bearer $id", groupInfo.id)
        .enqueue(object : Callback<GetGroupDetail> {
          override fun onResponse(call: Call<GetGroupDetail>, response: Response<GetGroupDetail>) {
            try {
              groupDetail = response.body()!!
              setPayersAddPayment(groupDetail.users)
            } catch (e: Exception) {
              Log.i("ViewModelAddPayment", "onResponse: ${e.message}")
              _navigateToHome.value = true
            }
          }

          override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
            Log.d("ViewModelAddPayment", t.message?:"No message")
            _navigateToHome.value = true
          }
        })
    }
  }

  fun setComment(comment: String) {
    _comment.value = comment
  }

  fun setThumbnail(thumbnail: String) {
    _thumbnail.value = thumbnail
  }

  fun setId(idInput: String) {
    id = idInput
  }

  @JvmName("setGroupInfo1")
  fun setGroupInfo(groupInfoInput: GroupPropertyResponse) {
    groupInfo = groupInfoInput
    setGroupName(groupInfo.name)
  }

  fun setLendersList(list: List<Float>) {
    _lendersAddPayment.value = list
  }

  fun setBorrowersList(list: List<Float>) {
    _borrowersAddPayment.value = list
  }

  fun getSumUppedAmountList(): List<Float> {
    return _lendersAddPayment.value!!.zip(_borrowersAddPayment.value!!) { x, y ->
      x - y
    }
  }

  fun setTag() {
    CoroutineScope(Dispatchers.IO).launch {
      tags = TagRepository(getAppDatabase(getApplication())).tags
    }
  }

  fun setPaidAt(date: String) {
    _paidAt.value = date
  }

  fun navigationCompleted() {
    _navigateToGroupDetail.value = null
  }

  fun setPositivePayers(payers: List<UserExpense>) {
    _editPaymentPositivePayers.value = payers
  }

  fun setNegativePayers(payers: List<UserExpense>) {
    _editPaymentNegativePayers.value = payers
  }

  fun sendRequest() {
    val positivePayers = _payersAddPayment.value!!.zip(lendersAddPayment.value!!) { x, y ->
      UserExpense(id = x.id, amount = y)
    }
    val negativePayers = _payersAddPayment.value!!.zip(borrowersAddPayment.value!!) { x, y ->
      UserExpense(id = x.id, amount = y * -1)
    }

    if (paymentInfo == null) {
      //　新規作成
      val expenseProperty = CreateExpenseProperty(
        name = paymentName.value!!,
        currency = currency.value!!,
        total = total.value!!,
        positivePayers = positivePayers.filter { it.amount != 0f },
        negativePayers = negativePayers.filter { it.amount != 0f },
        tags = tags.filter { it.isSelected }.map { it.name },
        comment = comment.value,
        images = listOf(thumbnail.value!!),
        paidAt = paidAt.value
      )
      Api.retrofitService.addExpense("Bearer $id", expenseProperty, groupInfo.id)
        .enqueue(object : Callback<Unit> {
          override fun onResponse(
            call: Call<Unit>,
            response: Response<Unit>
          ) {
            if (response.isSuccessful) {
              _navigateToGroupDetail.value = groupInfo
            } else {
              Toast.makeText(
                getApplication(),
                response.message(),
                Toast.LENGTH_LONG
              ).show()
            }
          }

          override fun onFailure(call: Call<Unit>, t: Throwable) {
            Toast.makeText(getApplication(), t.message, Toast.LENGTH_LONG).show()
            Log.i("ViewModelAddPayment", "onFailure: ${t.message}")
          }

        })
    } else {
      //　支払い編集
      val expenseProperty = EditExpenseProperty(
        name = paymentName.value!!,
        currency = currency.value!!,
        total = total.value!!,
        positivePayers = positivePayers.filter { it.amount != 0f },
        negativePayers = negativePayers.filter { it.amount != 0f },
        isCompleted = paymentInfo!!.isCompleted,
        tags = tags.filter { it.isSelected }.map { it.name },
        comment = comment.value,
        images = listOf(thumbnail.value!!),
        paidAt = paidAt.value
      )
      Api.retrofitService.updatePayment(
        "Bearer $id",
        expenseProperty,
        groupInfo.id,
        paymentInfo!!.id
      )
        .enqueue(object : Callback<Unit> {
          override fun onResponse(
            call: Call<Unit>,
            response: Response<Unit>
          ) {
            if (response.isSuccessful) {
              _navigateToGroupDetail.value = groupInfo
            } else {
              Toast.makeText(
                getApplication(),
                response.message(),
                Toast.LENGTH_LONG
              ).show()
            }
          }

          override fun onFailure(call: Call<Unit>, t: Throwable) {
            Toast.makeText(getApplication(), t.message, Toast.LENGTH_LONG).show()
            Log.i("ViewModelAddPayment", "onFailure: ${t.message}")
          }
        })
    }

  }

  fun settleUp(borrower: UserExpense, lender: UserExpense) {
    val expenseProperty = CreateExpenseProperty(
      name = getApplication<Application>().resources.getString(R.string.settle_up),
      currency = currency.value!!,
      total = total.value!!,
      positivePayers = listOf(lender),
      negativePayers = listOf(borrower),
      tags = listOf(getApplication<Application>().resources.getString(R.string.settle_up)),
      comment = "",
      images = listOf(thumbnail.value!!),
      paidAt = paidAt.value
    )
    Api.retrofitService.addExpense("Bearer $id", expenseProperty, groupInfo.id)
      .enqueue(object : Callback<Unit> {
        override fun onResponse(
          call: Call<Unit>,
          response: Response<Unit>
        ) {
          if (response.isSuccessful) {
            _navigateToGroupDetail.value = groupInfo
          } else {
            Toast.makeText(
              getApplication(),
              response.message(),
              Toast.LENGTH_LONG
            ).show()
          }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
          Toast.makeText(getApplication(), t.message, Toast.LENGTH_LONG).show()
          Log.i("ViewModelAddPayment", "onFailure: ${t.message}")
        }
      })
  }

}
