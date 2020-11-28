package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemSettleUpChildViewBinding
import work.calmato.prestopay.network.NetworkPayer

class AdapterSettleUpChild(private val networkList: List<NetworkPayer>) :
  RecyclerView.Adapter<AdapterSettleUpChild.SettleUpChildViewHolder>() {
  class SettleUpChildViewHolder(val binding: ListItemSettleUpChildViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): SettleUpChildViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemSettleUpChildViewBinding.inflate(layoutInflater, parent, false)
        return SettleUpChildViewHolder(
          binding
        )
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettleUpChildViewHolder {
    return SettleUpChildViewHolder.from(
      parent
    )
  }

  override fun onBindViewHolder(holder: SettleUpChildViewHolder, position: Int) {
    holder.binding.also {
      it.user = networkList[position]
    }
  }

  override fun getItemCount() = networkList.size
}
