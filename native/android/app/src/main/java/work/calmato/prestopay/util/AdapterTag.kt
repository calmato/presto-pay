package work.calmato.prestopay.util

import android.content.res.Resources
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemTagBinding


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
      fun from(parent: ViewGroup): AdapterTag.TagViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTagBinding.inflate(layoutInflater, parent, false)
        return AdapterTag.TagViewHolder(
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
    holder.binding.tagCheckBox.setOnCheckedChangeListener { _, isChecked ->
      holder.binding.tag?.isSelected = isChecked
    }
  }
}

class Tag(val name:String,val imageId:Int,var isSelected:Boolean)

/**
 * Returns resource ids from the array resource id.
 */
fun Resources.getIdList(arrayResourceId: Int): List<Int> {
  with(obtainTypedArray(arrayResourceId)) {
    try {
      return (0 until length()).map { getResourceId(it) }
    } finally {
      recycle()
    }
  }
}

/**
 * Returns the resource id.
 * @throws IllegalStateException when the resource id does not exist.
 */
fun TypedArray.getResourceId(index: Int): Int {
  return getResourceId(index, 0).apply { if (this == 0) throw IllegalStateException("resourceId not found.") }
}

