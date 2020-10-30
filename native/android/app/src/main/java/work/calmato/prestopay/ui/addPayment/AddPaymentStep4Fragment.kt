package work.calmato.prestopay.ui.addPayment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_add_payment_step4.*
import kotlinx.android.synthetic.main.fragment_add_payment_step4.calendar
import kotlinx.android.synthetic.main.fragment_add_payment_step4.calendarDate
import kotlinx.android.synthetic.main.fragment_add_payment_step4.calendarYear
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep4Binding
import work.calmato.prestopay.util.*
import java.text.SimpleDateFormat
import java.util.*

class AddPaymentStep4Fragment : PermissionBase(){
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }
  private val CODE = 11

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding:FragmentAddPaymentStep4Binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment_step4,container,false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    inflateGraph(chart,viewModel.payersAddPayment.value!!.map { it.name },viewModel.getSumUppedAmountList(),requireContext())
    constraintDate2.setOnClickListener {
      val datePickerFragment = DatePickerFragment()
      datePickerFragment.setTargetFragment(this, CODE)
      datePickerFragment.show(parentFragmentManager, "datePicker")
    }
    camera2.setOnClickListener {
      requestPermission()
    }
    comment2.setOnClickListener {
      showCommentDialog()
    }
    tagImage3.setOnClickListener {
      showTagDialog()
    }
    buttonStep4.setOnClickListener {
      startHttpConnection(buttonStep4,nowLoadingStep4,requireContext())
      viewModel.sendRequest(encodeImage2Base64(camera2))
      finishHttpConnection(buttonStep4,nowLoadingStep4)
    }
    viewModel.setThumbnail(encodeImage2Base64(camera2))
    val c = Calendar.getInstance()
    inflateDate(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(c.time))
    val monthDate = calendarDate.text.split("/")
    viewModel.setPaidAt("${calendarYear.text}-${monthDate[0]}-${monthDate[1]}T00:00:00.000Z")
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == CODE && resultCode == Activity.RESULT_OK) {
      inflateDate(data?.getStringExtra("selectedDate"))
      calendar.visibility = ImageView.INVISIBLE
      calendarYear.visibility = TextView.VISIBLE
      calendarDate.visibility = TextView.VISIBLE
      val monthDate = calendarDate.text.split("/")
      viewModel.setPaidAt("${calendarYear.text}-${monthDate[0]}-${monthDate[1]}T00:00:00.000Z")
    }
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      cropImage(data?.data!!)
    }
    if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
      val resultUri = UCrop.getOutput(data!!)
      camera2.setImageURI(resultUri)
      camera2.visibility = ImageView.VISIBLE
      viewModel.setThumbnail(encodeImage2Base64(camera2))
    }
  }

  private fun inflateDate(yearDate: String?) {
    val dateList = yearDate?.split("/")
    calendarYear.text = dateList!![0]
    val concatDate = dateList[1] + "/" + dateList[2]
    calendarDate.text = concatDate
  }

  private fun showCommentDialog() {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val input = EditText(requireContext())
    input.setBackgroundResource(android.R.color.transparent)

    builder?.setTitle(resources.getString(R.string.add_comment))
      ?.setPositiveButton(
        resources.getString(R.string.add)
      ) { _, _ ->
        viewModel.setComment(input.text.toString())
      }
      ?.setNegativeButton(resources.getString(R.string.cancel), null)
      ?.setView(input)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
  }

  private fun showTagDialog() {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val tagRecycleAdapter = AdapterTag()
    tagRecycleAdapter.tagList = viewModel.tags
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = GridLayoutManager(requireContext(), 3)
      adapter = tagRecycleAdapter
    }
    builder?.setView(recycleView)
      ?.setPositiveButton(resources.getString(R.string.done), null)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
  }
}