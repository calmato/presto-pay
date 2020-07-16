package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemPlaneBinding
import work.calmato.prestopay.network.UserProperty

class AdapterRecyclePlane(val onClickListener: OnClickListener) :
  RecyclerView.Adapter<AdapterRecyclePlane.AddFriendViewHolder>() {
  var friendList: List<UserProperty> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = friendList.size

  override fun onBindViewHolder(holder: AddFriendViewHolder, position: Int) {
    holder.binding.also {
      it.userProperty = friendList[position]
    }
    holder.itemView.setOnClickListener {
      onClickListener.onClick(friendList[position])
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

  class AddFriendViewHolder(val binding: ListItemPlaneBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): AddFriendViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemPlaneBinding.inflate(layoutInflater, parent, false)
        return AddFriendViewHolder(
          binding
        )
      }
    }
  }
}

