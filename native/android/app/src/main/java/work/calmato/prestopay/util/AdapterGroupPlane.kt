package work.calmato.prestopay.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAd
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.ListGroupItemPlaneBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.repository.GroupsRepository

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
        val divideNum = 7
        for (i in 1..size / divideNum) {
          rawData.add(
            i * divideNum, GroupPropertyResponse(
              "", "", "", listOf(), "", "",
              selected = true,
              isHidden = false
            )
          )
        }
        if (size < divideNum) {
          rawData.add(
            GroupPropertyResponse(
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
      // Native ad
      val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
        .forUnifiedNativeAd { ad: UnifiedNativeAd ->
//          val style: NativeTemplateStyle =
//            NativeTemplateStyle.Builder().withCallToActionTextSize(5f).build()
          // Show the ad.
//          holder.binding.nativeAd.setStyles(style)
          holder.binding.nativeAd.setNativeAd(ad)
        }
        .withAdListener(object : AdListener() {
          // AdListener callbacks like OnAdFailedToLoad, OnAdOpened, OnAdClicked and
          // so on, can be overridden here.
          override fun onAdFailedToLoad(errorCode: Int) {
            Log.i("MainActivity", "onAdFailedToLoad: $errorCode")
          }
        })
        .withNativeAdOptions(
          NativeAdOptions.Builder()
            // Methods in the NativeAdOptions.Builder class can be
            // used here to specify individual options settings.
            .build()
        )
        .build()
      adLoader.loadAd(AdRequest.Builder().build())
    } else {
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
