package work.calmato.prestopay

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize Mobile Ads SDK
    MobileAds.initialize(this)
    refreshAd()
  }
  companion object {
    var nativeAd : UnifiedNativeAd? = null
  }

  fun refreshAd() {
    // Native ad
    val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
      .forUnifiedNativeAd { ad: UnifiedNativeAd ->
        nativeAd = ad
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
  }
}
