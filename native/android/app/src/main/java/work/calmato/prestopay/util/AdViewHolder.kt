package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemAdsBinding

class AdViewHolder(val binding: ListItemAdsBinding) :
  RecyclerView.ViewHolder(binding.root) {
  companion object{
    fun from(parent:ViewGroup): AdViewHolder{
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding = ListItemAdsBinding.inflate(layoutInflater,parent,false)
      return AdViewHolder(
        binding
      )
    }
  }
}
