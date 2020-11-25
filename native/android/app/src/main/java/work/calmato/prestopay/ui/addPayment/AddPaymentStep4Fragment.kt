package work.calmato.prestopay.ui.addPayment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_add_payment.*
import kotlinx.android.synthetic.main.fragment_add_payment_step4.*
import kotlinx.android.synthetic.main.fragment_add_payment_step4.calendar
import kotlinx.android.synthetic.main.fragment_add_payment_step4.calendarDate
import kotlinx.android.synthetic.main.fragment_add_payment_step4.calendarYear
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep4Binding
import work.calmato.prestopay.util.*
import java.text.SimpleDateFormat
import java.util.*

class AddPaymentStep4Fragment : PermissionBase() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }
  private val CODE = 11

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentAddPaymentStep4Binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment_step4, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    inflateGraph(
      chart,
      viewModel.payersAddPayment.value!!.map { it.name },
      viewModel.getSumUppedAmountList(),
      requireContext()
    )
    constraintDate2.setOnClickListener {
      val datePickerFragment = DatePickerFragment(requireContext())
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
      viewModel.setThumbnail(encodeImage2Base64(camera2))
      viewModel.sendRequest()
    }
    viewModel.paymentInfo?.also { payment ->
      // 支払い編集時はここに来る
      // 日付
      inflateDate(payment.paidAt)
      viewModel.setPaidAt(payment.paidAt)
      calendar.visibility = ImageView.INVISIBLE
      calendarYear.visibility = TextView.VISIBLE
      calendarDate.visibility = TextView.VISIBLE
      //　タグ
      viewModel.tags.forEach { tag ->
        if (payment.tags!!.contains(tag.name)) {
          tag.isSelected = true
        }
      }
      if (viewModel.tags.map { it.isSelected }.contains(true)) {
        tagImage3.setImageResource(viewModel.tags.filter { tag ->
          tag.isSelected
        }[0].imageId)
      }
      // 写真
      val images = payment.imageUrls!![0].split(',')
      Picasso.with(requireContext()).load(images[images.lastIndex].trim()).into(camera2)

      // コメント
      payment.comment?.let { comment -> viewModel.setComment(comment) }

    } ?: run {
      // 新規作成時はここに来る
      val c = Calendar.getInstance()
      val dateTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(c.time)
      inflateDate(dateTime)
      viewModel.setPaidAt(dateTime)
    }
    chart.isDoubleTapToZoomEnabled = false
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == CODE && resultCode == Activity.RESULT_OK) {
      inflateDate(data?.getStringExtra("selectedDate"))
      calendar.visibility = ImageView.INVISIBLE
      calendarYear.visibility = TextView.VISIBLE
      calendarDate.visibility = TextView.VISIBLE
      val monthDate = calendarDate.text.split("-", " ")
      viewModel.setPaidAt("${calendarYear.text}-${monthDate[0]}-${monthDate[1]} 00:00:00")
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
    val dateList = yearDate?.split("-", " ", "T")
    calendarYear.text = dateList!![0]
    val concatDate = dateList[1] + "-" + dateList[2]
    calendarDate.text = concatDate
  }

  private fun showCommentDialog() {
    val builder: AlertDialog.Builder = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val input = EditText(requireContext())
    input.filters = arrayOf(InputFilter.LengthFilter(256))
    input.setBackgroundResource(android.R.color.transparent)
    viewModel.comment.value?.let {
      input.setText(it)
    }

    builder.setTitle(resources.getString(R.string.add_comment))
      ?.setPositiveButton(
        resources.getString(R.string.add)
      ) { _, _ ->
        viewModel.setComment(input.text.toString())
      }
      ?.setNegativeButton(resources.getString(R.string.cancel), null)
      ?.setView(input)
    val dialog: AlertDialog? = builder.create()
    dialog?.show()
  }

  private fun showTagDialog() {
    val builder: AlertDialog.Builder = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val tagRecycleAdapter = AdapterTag()
    tagRecycleAdapter.tagList = viewModel.tags
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = GridLayoutManager(requireContext(), 3)
      adapter = tagRecycleAdapter
    }
    builder.setView(recycleView)
      ?.setPositiveButton(resources.getString(R.string.done), null)
    val dialog: AlertDialog? = builder.create()
    dialog?.show()
  }
}
