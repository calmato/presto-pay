package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemTagBinding
import work.calmato.prestopay.network.Tag


class AdapterTag():RecyclerView.Adapter<AdapterTag.TagViewHolder>() {
  var tagList:List<Tag> = emptyList()
    set(value){
      field = value
      notifyDataSetChanged()
    }
  override fun getItemCount() = tagList.size

  class TagViewHolder(val binding:ListItemTagBinding):
    RecyclerView.ViewHolder(binding.root){
    companion object{
      fun from(parent: ViewGroup): TagViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTagBinding.inflate(layoutInflater, parent, false)
        return TagViewHolder(
          binding
        )
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
    return TagViewHolder.from(
      parent
    )
  }

  override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
    holder.binding.also {
      it.tag = tagList[position]
    }
    holder.binding.parent.setOnClickListener {
      holder.binding.tag?.isSelected = !holder.binding.tag?.isSelected!!
      holder.binding.parent.setTagCheck(holder.binding.tag)
    }
  }
}
