package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_checkbox.view.*
import work.calmato.prestopay.databinding.ListItemCheckboxBinding
import work.calmato.prestopay.network.UserProperty

class AdapterCheck(val onClickListener: OnClickListener?) :
  RecyclerView.Adapter<AdapterCheck.AddFriendViewHolder>() {
  var friendList: List<UserProperty> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = friendList.size

  override fun onBindViewHolder(holder: AddFriendViewHolder, position: Int) {
    holder.binding.also { it ->
      it.userProperty = friendList[position]
      it.addFriendCheckBox.setOnClickListener {
        friendList[position].checked = it.addFriendCheckBox.isChecked
      }
    }
    holder.itemView.setOnClickListener {
      onClickListener?.onClick(friendList[position])
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendViewHolder {
    return AddFriendViewHolder.from(
      parent
    )
  }

  class OnClickListener(val clickListener: (userProperty: UserProperty) -> Unit) {
    fun onClick(userProperty: UserProperty) = clickListener(userProperty)
  }

  class AddFriendViewHolder(val binding: ListItemCheckboxBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddFriendViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemCheckboxBinding.inflate(layoutInflater, parent, false)
        return AddFriendViewHolder(
          binding
        )
      }
    }
  }
}
