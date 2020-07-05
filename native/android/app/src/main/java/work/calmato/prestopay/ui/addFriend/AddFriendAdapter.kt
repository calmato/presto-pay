package work.calmato.prestopay.ui.addFriend

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.databinding.ListItemNameThumbnailBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users

class AddFriendAdapter(private val mUsersProperties: Users) : RecyclerView.Adapter<AddFriendAdapter.ViewHolder>(){
  class ViewHolder private constructor(val binding:ListItemNameThumbnailBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(item:UserProperty){
      binding.userProperty = item
      binding.executePendingBindings()
    }
    companion object{
      fun from(parent:ViewGroup):ViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemNameThumbnailBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder.from(parent)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = mUsersProperties.users[position]
    if (item != null) {
      holder.bind(item)
    }
  }

  override fun getItemCount()=mUsersProperties.users.size
}
