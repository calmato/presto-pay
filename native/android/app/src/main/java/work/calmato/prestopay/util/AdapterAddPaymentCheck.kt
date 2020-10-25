package work.calmato.prestopay.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemAddPaymentCheckBinding
import work.calmato.prestopay.network.PayerAddPayment
import work.calmato.prestopay.util.Constant.Companion.NUMBER
import work.calmato.prestopay.util.Constant.Companion.RATIO

class AdapterAddPaymentCheck(private val onClickListener: OnClickListener?, val unit:String) :
  RecyclerView.Adapter<AdapterAddPaymentCheck.AddPaymentCheckViewHolder>() {
  var payers: List<PayerAddPayment> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  var amounts: MutableList<Float> = mutableListOf()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPaymentCheckViewHolder {
    return AddPaymentCheckViewHolder.from(
      parent
    )
  }

  override fun onBindViewHolder(holder: AddPaymentCheckViewHolder, position: Int) {

    holder.binding.also { binding ->
      binding.payer = payers[position]
      binding.amountVar = amounts[position]
      binding.unit.text = unit
    }
    holder.itemView.setOnClickListener {
      onClickListener?.onClick(payers[position])
    }
  }

  override fun getItemCount() = payers.size

  class OnClickListener(val clickListener: (payer: PayerAddPayment) -> Unit) {
    fun onClick(payer: PayerAddPayment) = clickListener(payer)
  }

  class AddPaymentCheckViewHolder(val binding: ListItemAddPaymentCheckBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddPaymentCheckViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemAddPaymentCheckBinding.inflate(layoutInflater, parent, false)
        return AddPaymentCheckViewHolder(
          binding
        )
      }
    }
  }
}
