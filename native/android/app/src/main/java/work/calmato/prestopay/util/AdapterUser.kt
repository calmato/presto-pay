package work.calmato.prestopay.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemNameThumbnailBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users

class AdapterUser(private val mUserProperties: Users?, val onClickListener: OnClickListener) :
  RecyclerView.Adapter<AdapterUser.AddFriendViewHolder>() {
  class AddFriendViewHolder(private val binding: ListItemNameThumbnailBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: UserProperty) {
      binding.userProperty = item
      binding.executePendingBindings()
    }

    companion object {
      fun from(parent: ViewGroup): AddFriendViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemNameThumbnailBinding.inflate(layoutInflater, parent, false)
        return AddFriendViewHolder(
          binding
        )
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendViewHolder {
    return AddFriendViewHolder.from(
      parent
    )
  }

  override fun onBindViewHolder(holder: AddFriendViewHolder, position: Int) {
    mUserProperties?.let {
      val userProperty = it.users[position]!!
      holder.itemView.setOnClickListener {
        onClickListener.onClick(userProperty)
      }
      holder.bind(userProperty)
    }
  }

  override fun getItemCount(): Int {
    var returnInt: Int = 0
    mUserProperties?.let {
      returnInt = it.users.size
    }
    return returnInt
  }

  class OnClickListener(val clickListener: (userProperty: UserProperty) -> Unit) {
    fun onClick(userProperty: UserProperty) = clickListener(userProperty)
  }
}

