package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListGroupItemPlaneBinding
import work.calmato.prestopay.network.GroupPropertyResponse

class AdapterGroupPlane(val onClickListener: OnClickListener) :
  RecyclerView.Adapter<AdapterGroupPlane.AddGroupViewHolder>() {
  var groupList: List<GroupPropertyResponse> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount(): Int = groupList.size

  override fun onBindViewHolder(holder: AddGroupViewHolder, position: Int) {
    holder.binding.also {
      it.groupPropertyResponse = groupList[position]
    }
    holder.itemView.setOnClickListener {
      onClickListener.onClick(groupList[position])
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGroupViewHolder {
    return AddGroupViewHolder.from(
      parent
    )
  }

  class OnClickListener(val clickListener: (groupPropertyResponse: GroupPropertyResponse) -> Unit) {
    fun onClick(groupPropertyResponse: GroupPropertyResponse) = clickListener(groupPropertyResponse)
  }

  class AddGroupViewHolder(val binding: ListGroupItemPlaneBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddGroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListGroupItemPlaneBinding.inflate(layoutInflater, parent, false)
        return AddGroupViewHolder(
          binding
        )
      }
    }
  }

}
