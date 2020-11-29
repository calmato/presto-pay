package work.calmato.prestopay.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAd
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import work.calmato.prestopay.databinding.ListGroupItemPlaneBinding
import work.calmato.prestopay.network.GroupPropertyResponse

class AdapterGroupPlane(val onClickListener: OnClickListener, val context:Context) :
  RecyclerView.Adapter<AdapterGroupPlane.AddGroupViewHolder>() {
  var groupList: List<GroupPropertyResponse> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount(): Int = groupList.size

  override fun onBindViewHolder(holder: AddGroupViewHolder, position: Int) {
    if(position%7 == 0 && position!=0){
      holder.binding.nativeAd.visibility = ImageView.VISIBLE
      // Native ad
      val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/6300978111")
        .forUnifiedNativeAd { ad: UnifiedNativeAd ->
          // Show the ad.
          holder.binding.nativeAd.setNativeAd(ad)
//        native_ad.setNativeAd(ad)
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
    }else{
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
