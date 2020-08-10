package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_group_item_checkbox.view.*
import work.calmato.prestopay.databinding.ListGroupItemCheckboxBinding
import work.calmato.prestopay.network.GroupPropertyResponse

class AdapterGroupCheck(val onClickListner: OnClickListener?) :
  RecyclerView.Adapter<AdapterGroupCheck.AddGroupViewHolder>() {
  var groupList: List<GroupPropertyResponse> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = groupList.size

  override fun onBindViewHolder(holder: AddGroupViewHolder, position: Int) {
     holder.binding.also { it ->
       it.groupPropertyResponse = groupList[position]
       it.addGroupCheckBox.setOnClickListener {
         groupList[position].checked = it.addGroupCheckBox.isChecked
       }
     }
    holder.itemView.setOnClickListener {
      onClickListner?.onClick(groupList[position])
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

  class AddGroupViewHolder(val binding: ListGroupItemCheckboxBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddGroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListGroupItemCheckboxBinding.inflate(layoutInflater, parent, false)
        return AddGroupViewHolder(
          binding
        )
      }
    }
  }
}
