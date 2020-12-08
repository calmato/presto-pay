package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemUsernameBinding
import work.calmato.prestopay.network.UserProperty

class AdapterUserNamePlane(val onClickListener: OnClickListener) :
  RecyclerView.Adapter<AdapterUserNamePlane.AddUserNameViewHolder>() {
  var friendList: List<UserProperty> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = friendList.size

  override fun onBindViewHolder(holder: AddUserNameViewHolder, position: Int) {
    holder.binding.also {
      it.userProperty = friendList[position]
    }
    holder.itemView.setOnClickListener {
      onClickListener.onClick(friendList[position])
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUserNameViewHolder {
    return AddUserNameViewHolder.from(
      parent
    )
  }

  class OnClickListener(val clickListener: (userProperty: UserProperty) -> Unit) {
    fun onClick(userProperty: UserProperty) = clickListener(userProperty)
  }

  class AddUserNameViewHolder(val binding: ListItemUsernameBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddUserNameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemUsernameBinding.inflate(layoutInflater, parent, false)
        return AddUserNameViewHolder(
          binding
        )
      }
    }
  }
}
