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
import kotlin.math.absoluteValue

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

  private lateinit var listToBeCalculated: MutableList<NetworkPayer>
  private lateinit var listToBeSent: MutableList<PaymentInfoSettleUp>

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

  /**
   * 支払い登録
   * @param i 過払いの人の[listToBeCalculated]でのインデックス
   * @param j 未払いの人の[listToBeCalculated]でのインデックス
   * @param amount やりとりされる金額の絶対値
   */
  private fun registerSettleUp(i: Int, j: Int, absAmount: Float) {
    if(absAmount >= 0.05) {
      var amount = absAmount
      for (k in 0..1) {
        val user1 = listOf(listToBeCalculated[i], listToBeCalculated[j])[k]
        val user2 = listOf(listToBeCalculated[i], listToBeCalculated[j])[1 - k]
        //    既にlistToBeSentに支払い情報がある場合
        if (listToBeSent.map { it.payerAddPayment.id }.contains(user1.id)) {
          val index1: Int = listToBeSent.map { it.payerAddPayment.id }.indexOf(user1.id)
          listToBeSent[index1].payerAddPayment.amount += amount
          //   既にlistToBeSent内のlistPayerに支払い情報がある場合は単純に足す
          if (listToBeSent[index1].listPayer.map { it.id }.contains(user2.id)) {
            val index2 = listToBeSent[index1].listPayer.map { it.id }.indexOf(user2.id)
            listToBeSent[index1].listPayer[index2].amount += amount * -1
          } else {
            //   listToBeSent内のlistPayerに支払い情報がない場合は追加する
            listToBeSent[index1].listPayer.add(
              NetworkPayer(
                user2.id,
                user2.name,
                amount * -1
              )
            )
          }
        } else {
          //    既にlistToBeSentに支払い情報がない場合
          listToBeSent.add(
            PaymentInfoSettleUp(
              NetworkPayer(user1.id, user1.name, amount),
              mutableListOf(NetworkPayer(user2.id, user2.name, amount * -1))
            )
          )
        }
        amount *= -1
      }
    }
  }

  fun calcForSettleUp(): List<PaymentInfoSettleUp> {
    listToBeCalculated =
      (usersPaymentInfo.value!! as MutableList<NetworkPayer>).toMutableList()
    listToBeSent = mutableListOf()

    whileLoop@while (listToBeCalculated.size > 0) {
      //    支払う金額と受け取る金額が一致しているユーザー2人がいれば、その2人の間で精算する
      forLoop@ for (i in listToBeCalculated.indices - 1) {
        for (j in i + 1 until listToBeCalculated.size) {
          if (listToBeCalculated[i].amount == -1 * listToBeCalculated[j].amount) {
            if (listToBeCalculated[i].amount > 0) {
              registerSettleUp(i, j, listToBeCalculated[i].amount)
            } else {
              registerSettleUp(j, i, listToBeCalculated[j].amount)
            }
            listToBeCalculated.removeAt(j)
            listToBeCalculated.removeAt(i)
            break@forLoop
          }
        }
      }
      if(listToBeCalculated.size==0) {break@whileLoop}
      //    受け取る金額が最も多い人が支払いが最も多い人から受け取る
      val maxUserIdx =
        listToBeCalculated.indexOf( listToBeCalculated.maxBy { it.amount } )
      val minUserIdx =
        listToBeCalculated.indexOf( listToBeCalculated.minBy { it.amount } )
      // 受け取る金額のほうが支払いの絶対値より大きい時
      if (listToBeCalculated[maxUserIdx].amount >= listToBeCalculated[minUserIdx].amount.absoluteValue) {
        registerSettleUp(maxUserIdx,minUserIdx,listToBeCalculated[minUserIdx].amount.absoluteValue)
        listToBeCalculated[maxUserIdx].amount += listToBeCalculated[minUserIdx].amount
        listToBeCalculated.removeAt(minUserIdx)
      } else {
        // 支払い金額の絶対値ほうが受け取り金額より大きい時
        registerSettleUp(maxUserIdx,minUserIdx,listToBeCalculated[maxUserIdx].amount)
        listToBeCalculated[minUserIdx].amount += listToBeCalculated[maxUserIdx].amount
        listToBeCalculated.removeAt(maxUserIdx)
      }
    }
    return listToBeSent
  }

  data class PaymentInfoSettleUp(
    val payerAddPayment: NetworkPayer,
    val listPayer: MutableList<NetworkPayer>
  )
}
