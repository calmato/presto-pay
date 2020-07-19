package work.calmato.prestopay.ui.addExpense

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.fragment_add_expense.*
import kotlinx.android.synthetic.main.fragment_new_account.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddExpenseBindingImpl
import work.calmato.prestopay.util.Constant
import work.calmato.prestopay.util.DatePickerFragment
import work.calmato.prestopay.util.PermissionBase
import work.calmato.prestopay.util.encodeImage2Base64

class AddExpenseFragment : PermissionBase() {
  val REQUEST_CODE = 11
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddExpenseBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_add_expense, container, false
    )
    return binding.root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
      val selectedDate = data?.getStringExtra("selectedDate")
      val dateList = selectedDate?.split("/")
      calendarYear.text = dateList!![0]
      val concatDate = dateList[1] + "/" + dateList[2]
      calendarDate.text = concatDate
      calendar.visibility = ImageView.INVISIBLE
      calendarYear.visibility = TextView.VISIBLE
      calendarDate.visibility = TextView.VISIBLE
    }
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      camera.setImageURI(data?.data)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    constraintDate.setOnClickListener {
      val datePickerFragment = DatePickerFragment()
      datePickerFragment.setTargetFragment(this,REQUEST_CODE)
      datePickerFragment.show(parentFragmentManager,"datePicker")
    }
    camera.setOnClickListener {
      requestPermission()
    }
    comment.setOnClickListener {
      val builder: AlertDialog.Builder? = requireActivity().let{
        AlertDialog.Builder(it)
      }
      val input = EditText(requireContext())
      input.setText(commentEditText.text)
      builder?.setTitle("コメント追加")
        ?.setPositiveButton("追加",
        DialogInterface.OnClickListener{_,_ ->
          commentEditText.text = input.text.toString()
          commentEditText.visibility = EditText.VISIBLE
          commentText.visibility = TextView.VISIBLE
        })
        ?.setNegativeButton("キャンセル", null)
        ?.setView(input)
      val dialog:AlertDialog? = builder?.create()
      dialog?.show()

    }
    setHasOptionsMenu(true)
  }
  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.header_done, menu)
  }
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendRequest()
    }
    return super.onOptionsItemSelected(item)
  }
  private fun sendRequest(){
    val name = expenseName.text.toString()
    val amount = (amountEdit.text.toString()).toDouble()
    val date = calendarYear.text.toString() + "/" + calendarDate.text.toString()
    val thumbnail = encodeImage2Base64(camera)
    val comment = commentEditText.text.toString()
  }
}
