package work.calmato.prestopay.util

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemNameThumbnailBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users

class AdapterUser(private val mUserProperties: Users?, val onClickListener: OnClickListener?,val isCheckBoxVisible:Int) :
  RecyclerView.Adapter<AdapterUser.AddFriendViewHolder>() {
  class AddFriendViewHolder(private val binding: ListItemNameThumbnailBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: UserProperty, isCheckBoxVisible:Int) {
      binding.userProperty = item
      binding.addFriendCheckBox.visibility = isCheckBoxVisible
      binding.addFriendCheckBox.setOnClickListener {
        item.checked = binding.addFriendCheckBox.isChecked
      }
      if(isCheckBoxVisible == CheckBox.GONE){
        val newLayout = binding.thumbnail.layoutParams as ConstraintLayout.LayoutParams
        newLayout.marginStart = dpToPx(8)
      }
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
    private fun dpToPx(dp: Int): Int {
      return (dp * Resources.getSystem().displayMetrics.density).toInt()
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
        onClickListener?.onClick(userProperty)
      }
      holder.bind(userProperty,isCheckBoxVisible)

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

