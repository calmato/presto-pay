package work.calmato.prestopay

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception


class MainActivity : AppCompatActivity() {
  private lateinit var sharedPreferences: SharedPreferences
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize Mobile Ads SDK
    MobileAds.initialize(this)
    refreshAd()
    refreshAdFlag.value = false
    nativeAdCountDownTimer()
    firebaseIdCountDownTimer()
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
  }
  companion object {
    var nativeAd : UnifiedNativeAd? = null
    var refreshAdFlag : MutableLiveData<Boolean> = MutableLiveData(true)
    var firebaseId = ""
  }

  fun refreshAd() {
    Log.i("MainActivityRefresh", "refreshAd: ")
    nativeAd?.destroy()
    // Native ad
    val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
      .forUnifiedNativeAd { ad: UnifiedNativeAd ->
        nativeAd = ad
        // Timer
        refreshAdFlag.value = false
        nativeAdCountDownTimer()
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

  fun nativeAdCountDownTimer(){
    object : CountDownTimer(61000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
      }
      override fun onFinish() {
        refreshAdFlag.value = true
      }
    }.start()
  }

  fun firebaseIdCountDownTimer(){
    object : CountDownTimer(3000000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
      }
      override fun onFinish() {
        try {
          Log.i("MainActivity", "Token refreshing")
          FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
            firebaseId = it.result.token!!
            val editor = sharedPreferences.edit()
            editor.putString("token", it.result.token)
            editor.apply()
          }
          firebaseIdCountDownTimer()
        } catch(e:Exception){
          Log.i("MainActivity", "onFinish: ${e.message}")
        }
      }
    }.start()
  }


}
