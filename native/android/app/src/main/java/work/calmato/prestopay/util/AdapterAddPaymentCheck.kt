package work.calmato.prestopay.util

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemAddPaymentCheckBinding
import work.calmato.prestopay.network.PayerAddPayment
import work.calmato.prestopay.network.UserProperty

class AdapterAddPaymentCheck(val onClickListener: OnClickListener?):
RecyclerView.Adapter<AdapterAddPaymentCheck.AddPaymentCheckViewHolder>(){
  var payers: List<PayerAddPayment> = emptyList()
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
    holder.binding.also {
      it.payer = payers[position]
    }
    holder.itemView.setOnClickListener {
      onClickListener?.onClick(payers[position])
    }
  }

  override fun getItemCount() = payers.size

  class OnClickListener(val clickListener: (payer: PayerAddPayment) -> Unit) {
    fun onClick(payer: PayerAddPayment) = clickListener(payer)
  }
  class AddPaymentCheckViewHolder(val binding:ListItemAddPaymentCheckBinding) :
    RecyclerView.ViewHolder(binding.root){
    companion object{
      fun from(parent:ViewGroup): AddPaymentCheckViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemAddPaymentCheckBinding.inflate(layoutInflater,parent,false)
        return AddPaymentCheckViewHolder(
          binding
        )
      }
    }
  }
}
