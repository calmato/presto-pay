package work.calmato.prestopay.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import work.calmato.prestopay.BuildConfig
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.databinding.ListGroupItemPlaneBinding
import work.calmato.prestopay.network.GroupPropertyResponse

class AdapterGroupPlane(
  val onClickListener: OnClickListener,
  val context: Context,
  val isHidden: Boolean
) :
  RecyclerView.Adapter<AdapterGroupPlane.AddGroupViewHolder>() {
  var groupList: List<GroupPropertyResponse> = emptyList()
    set(value) {
      val rawData = value.toMutableList()
      if (!isHidden) {
        val size = value.size
        val adIdx = 4
        if (size < adIdx) {
          rawData.add(
            GroupPropertyResponse(
              "", "", "", listOf(), "", "",
              selected = true,
              isHidden = false
            )
          )
        } else {
          rawData.add(
            adIdx, GroupPropertyResponse(
              "", "", "", listOf(), "", "",
              selected = true,
              isHidden = false
            )
          )
        }
      }
      field = rawData
      notifyDataSetChanged()
    }

  override fun getItemCount(): Int = groupList.size

  override fun onBindViewHolder(holder: AddGroupViewHolder, position: Int) {
    if (groupList[position].selected) {
      holder.binding.nativeAd.visibility = ImageView.VISIBLE
      holder.binding.normalLayout.visibility = ConstraintLayout.INVISIBLE
      if(MainActivity.nativeAd != null){
        holder.binding.nativeAd.setNativeAd(MainActivity.nativeAd)
      }
    } else {
      holder.binding.normalLayout.visibility = ConstraintLayout.VISIBLE
      holder.binding.nativeAd.visibility = ImageView.GONE
      holder.binding.also {
        it.groupPropertyResponse = groupList[position]
      }
      holder.itemView.setOnClickListener {
        onClickListener.onClick(groupList[position])
      }
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
