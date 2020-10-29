package work.calmato.prestopay.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager


fun hideSoftKeyboard(activity: Activity) {
  val inputMethodManager: InputMethodManager = activity.getSystemService(
    Activity.INPUT_METHOD_SERVICE
  ) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(
    activity.currentFocus.windowToken, 0
  )
}
