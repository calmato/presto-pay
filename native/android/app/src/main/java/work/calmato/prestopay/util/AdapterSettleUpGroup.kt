package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemSettleUpGroupBinding
import work.calmato.prestopay.network.NetworkPayerContainer

class AdapterSettleUpGroup : RecyclerView.Adapter<AdapterSettleUpGroup.SettleUpViewHolder>(){
  var settleUpList : List<ViewModelSettleUpGroup.PaymentInfoSettleUp> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  class SettleUpViewHolder(val binding:ListItemSettleUpGroupBinding) :
    RecyclerView.ViewHolder(binding.root){
      companion object{
        fun from(parent:ViewGroup): SettleUpViewHolder{
          val layoutInflater = LayoutInflater.from(parent.context)
          val binding = ListItemSettleUpGroupBinding.inflate(layoutInflater,parent,false)
          return SettleUpViewHolder(
            binding
          )
        }
      }
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettleUpViewHolder {
    return SettleUpViewHolder.from(
      parent
    )
  }

  override fun onBindViewHolder(holder: SettleUpViewHolder, position: Int) {
    holder.binding.also {
      it.user = settleUpList[position].payerAddPayment
      it.users = NetworkPayerContainer(settleUpList[position].listPayer)
    }
  }

  override fun getItemCount() = settleUpList.size
}
