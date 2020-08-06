package work.calmato.prestopay.ui.addExpense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import work.calmato.prestopay.R

class AdvancedExpenseFragment:DialogFragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
        return inflater.inflate(R.layout.dialog_advanced_expense,container,false)
  }

  override fun onStart() {
    super.onStart()
    val dialog = dialog
    dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

  }
}
