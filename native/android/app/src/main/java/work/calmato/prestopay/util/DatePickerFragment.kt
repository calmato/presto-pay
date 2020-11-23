package work.calmato.prestopay.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment(val cont: Context):DialogFragment(),DatePickerDialog.OnDateSetListener {
  var calendar = Calendar.getInstance()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    // Use the current date as the default date in the picker
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    // Create a new instance of DatePickerDialog and return it
    return DatePickerDialog(cont, this, year, month, day)
  }

  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    calendar.set(year,month,dayOfMonth)
    val selectedDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.getDefault()).format(calendar.time)
    targetFragment?.onActivityResult(
      targetRequestCode,
      Activity.RESULT_OK,
      Intent().putExtra("selectedDate",selectedDate)
    )
  }
}
