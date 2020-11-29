package work.calmato.prestopay

import android.os.Bundle
import android.util.Log
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

    // Banner
//    val adView = adView
//    val adRequest = AdRequest.Builder().build()
//    adView.loadAd(adRequest)
//    adView.adListener = object : AdListener() {
//      override fun onAdLoaded() {
//        // Code to be executed when an ad finishes loading.
//        adView.visibility = AdView.VISIBLE
//      }
//
//      override fun onAdFailedToLoad(adError: LoadAdError) {
//        // Code to be executed when an ad request fails.
//        adView.visibility = AdView.GONE
//      }
//
//      override fun onAdOpened() {
//        // Code to be executed when an ad opens an overlay that
//        // covers the screen.
//      }
//
//      override fun onAdClicked() {
//        // Code to be executed when the user clicks on an ad.
//      }
//
//      override fun onAdLeftApplication() {
//        // Code to be executed when the user has left the app.
//      }
//
//      override fun onAdClosed() {
//        // Code to be executed when the user is about to return
//        // to the app after tapping on an ad.
//      }
//    }

    // Native ad
    val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/6300978111")
      .forUnifiedNativeAd { ad: UnifiedNativeAd ->
        // Show the ad.
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


  }

}
