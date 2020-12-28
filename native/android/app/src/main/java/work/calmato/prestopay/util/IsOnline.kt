package work.calmato.prestopay.util

import android.content.Context
import android.net.ConnectivityManager


fun isOnline(context: Context): Boolean {
  val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val activeNetwork = cm.activeNetworkInfo
    return activeNetwork?.isConnected == true
}
