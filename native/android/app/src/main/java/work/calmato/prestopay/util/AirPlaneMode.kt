package work.calmato.prestopay.util

import android.content.Context
import android.provider.Settings

fun isAirPlaneModeOn(context: Context): Boolean {
  return Settings.System.getInt(
    context.contentResolver,
    Settings.Global.AIRPLANE_MODE_ON, 0
  ) != 0
}
