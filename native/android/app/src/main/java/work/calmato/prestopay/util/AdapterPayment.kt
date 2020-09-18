package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemPaymentBinding
import work.calmato.prestopay.databinding.ListItemPlaneBinding
import work.calmato.prestopay.network.PaymentPropertyGet

class AdapterPayment() : RecyclerView.Adapter<AdapterPayment.PaymentViewHolder>() {
  var paymentList: List<PaymentPropertyGet> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = paymentList.size

  override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
    holder.binding.also {
      it.payment = paymentList[position]
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
