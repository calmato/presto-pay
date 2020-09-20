package work.calmato.prestopay.util

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemPaymentBinding
import work.calmato.prestopay.databinding.ListItemPlaneBinding
import work.calmato.prestopay.network.PaymentPropertyGet
import java.lang.Math.round
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class AdapterPayment(val context:Context) : RecyclerView.Adapter<AdapterPayment.PaymentViewHolder>() {
  var paymentList: List<PaymentPropertyGet> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = paymentList.size

  override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val name = sharedPreferences.getString("name", "")
    holder.binding.also { it ->
      val thisPayment = paymentList[position]
      it.payment = thisPayment
      it.currency = thisPayment.currency
      it.amount =  (thisPayment.payers.filter { it.name == name }[0].amount * 100).roundToInt().toFloat() / 100
      val maxAmount = thisPayment.payers.map { it.amount }.max()
      val maxPayer = thisPayment.payers.filter { it.amount == maxAmount }[0].name
      if (maxAmount != null) {
        it.whoPaid = maxPayer + "が" + thisPayment.currency + (maxAmount * 100).roundToInt().toFloat() / 100 + "多く払った"
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
    return PaymentViewHolder.from(
      parent
    )
  }

  class PaymentViewHolder(val binding: ListItemPaymentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): PaymentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemPaymentBinding.inflate(layoutInflater, parent, false)
        return PaymentViewHolder(
          binding
        )
      }
    }
  }
}
