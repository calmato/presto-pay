package work.calmato.prestopay.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemAddPaymentInputAmountBinding
import work.calmato.prestopay.network.PayerAddPayment
import kotlin.math.round

class AdapterAddPaymentInputAmount(val unit:String) :
  RecyclerView.Adapter<AdapterAddPaymentInputAmount.AddPaymentInputAmountViewHolder>() {
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

  class AddPaymentInputAmountViewHolder(val binding: ListItemAddPaymentInputAmountBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddPaymentInputAmountViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemAddPaymentInputAmountBinding.inflate(layoutInflater, parent, false)
        return AddPaymentInputAmountViewHolder(
          binding
        )
      }
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): AddPaymentInputAmountViewHolder {
    return AddPaymentInputAmountViewHolder.from(parent)
  }

  override fun onBindViewHolder(holder: AddPaymentInputAmountViewHolder, position: Int) {
    var watcher: TextWatcher? = null
    holder.binding.also { binding ->
      binding.payer = payers[position]
      binding.amount = amounts[position]
      binding.unit.text = unit
    }
    //小数点2桁まで許容。　ブランクとかNullは0にする。
    watcher = holder.binding.inputAmount.doAfterTextChanged {s ->
      if (s.isNullOrEmpty() || s.isNullOrBlank()) {
        amounts[position] = 0f
        notifyDataSetChanged()
      } else {
        if (round(s.toString().toFloat() * 100) / 100 != s.toString().toFloat()) {
          holder.binding.inputAmount.removeTextChangedListener(watcher)
          val newNum = (round(s.toString().toFloat() * 100) / 100).toString()
          holder.binding.inputAmount.setText(newNum)
          amounts[position] = newNum.toFloat()
          holder.binding.inputAmount.addTextChangedListener(watcher)
        } else{
          amounts[position] = s.toString().toFloat()
        }
      }
    }
  }

  override fun getItemCount() = payers.size
}
