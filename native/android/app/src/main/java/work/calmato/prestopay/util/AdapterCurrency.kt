package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemCountryBinding
import work.calmato.prestopay.network.NationalFlag

class AdapterCurrency(private val onClickListener: OnClickListener):RecyclerView.Adapter<AdapterCurrency.CurrencyViewHolder>() {
  var countryList:List<NationalFlag> = emptyList()
    set(value){
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = countryList.size

  class CurrencyViewHolder(val binding:ListItemCountryBinding)
    :RecyclerView.ViewHolder(binding.root){
    companion object{
      fun from(parent:ViewGroup): CurrencyViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemCountryBinding.inflate(layoutInflater,parent,false)
        return CurrencyViewHolder(
          binding
        )
      }
    }
  }

  class OnClickListener(val clickListener: (nationalFlag:NationalFlag) -> Unit){
    fun onClick(nationalFlag: NationalFlag) = clickListener(nationalFlag)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
    return CurrencyViewHolder.from(
      parent
    )
  }

  override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
    holder.binding.also {
      it.nationalFlag = countryList[position]
    }
    holder.itemView.setOnClickListener {
      onClickListener.onClick(countryList[position])
    }
  }
}
