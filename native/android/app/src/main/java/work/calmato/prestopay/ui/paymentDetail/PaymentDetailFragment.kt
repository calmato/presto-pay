package work.calmato.prestopay.ui.paymentDetail

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_payment_detail.*
import retrofit2.Call
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentPaymentDetailBinding
import work.calmato.prestopay.util.PermissionBase
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.network.*


class PaymentDetailFragment : PermissionBase() {
  private lateinit var paymentDetail: PaymentPropertyGet
  private lateinit var groupDetail: GroupPropertyResponse
  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var id: String
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentPaymentDetailBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_payment_detail, container, false)
    paymentDetail = PaymentDetailFragmentArgs.fromBundle(requireArguments()).paymentPropertyGet
    groupDetail = PaymentDetailFragmentArgs.fromBundle(requireArguments()).groupPropertyResponse
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
    date.text = paymentDetail.createdAt.split("T")[0]
    paymentDetail.imageUrls!![0].let {
      if (it.isNotEmpty()) {
        Picasso.with(requireContext()).load(it).into(thumbnail)
      }
    }
    expenseName.text = paymentDetail.name
    amount.text = paymentDetail.total.toString().plus(" ").plus(paymentDetail.currency)
    comment.text = paymentDetail.comment
    setGraph()
    completed.setOnClickListener {
      val builder: AlertDialog.Builder? = requireActivity().let {
        AlertDialog.Builder(it)
      }
      builder?.setTitle(resources.getString(R.string.paymentCompleted))
        ?.setPositiveButton(resources.getString(R.string.done)) { _, _ ->
          sendRequest()
        }
        ?.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        ?.setMessage(resources.getString(R.string.messagePaymentCompleted))
      val dialog: AlertDialog? = builder?.create()
      dialog?.show()
    }
  }

  private fun setGraph() {
    val entries: MutableList<BarEntry> = arrayListOf()
    val nonZeroPayers = paymentDetail.payers.filter { it.amount != 0f }
    val colors = nonZeroPayers.map {
      if (it.amount > 0) {
        ContextCompat.getColor(requireContext(), R.color.positiveNumberColor)
      } else {
        ContextCompat.getColor(requireContext(), R.color.negativeNumberColor)
      }
    }
    for ((idx, payer) in nonZeroPayers.withIndex()) {
      entries.add(BarEntry(idx.toFloat(), kotlin.math.abs(payer.amount)))
    }
    Log.i("PaymentDetailFragment", "onViewCreated: $entries")
    val set = BarDataSet(entries, "BarDataSet")
    set.axisDependency = YAxis.AxisDependency.LEFT
    set.colors = colors

    chart.setFitBars(true) // make the x-axis fit exactly all bars
    val xAxis = chart.xAxis
    xAxis.valueFormatter = XLabelFormatter(nonZeroPayers)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.textSize = 14f
    xAxis.granularity = 1f; // minimum axis-step (interval) is 1
    xAxis.labelRotationAngle = -45f
    xAxis.spaceMin = 0f
    xAxis.spaceMax = 0f
    val left = chart.axisLeft
    val right = chart.axisRight
    left.axisMinimum = 0f
    right.axisMinimum = 0f
    left.setDrawAxisLine(false); // no axis line
    left.setDrawGridLines(false); // no grid lines
    right.isEnabled = false
    right.setDrawGridLines(false); // no grid line
    left.textSize = 14f
    chart.legend.isEnabled = false
    chart.description.isEnabled = false
    val data = BarData(set)
    data.barWidth = 0.9f // set custom bar width
    data.setValueTextSize(14f)
    chart.data = data
    chart.invalidate() // refresh
  }

  private fun sendRequest() {
    val paymentPropertyPost = PaymentPropertyPost(
      name = paymentDetail.name,
      currency = paymentDetail.currency,
      total = paymentDetail.total,
      payers = paymentDetail.payers,
      isCompleted = true,
      tags = paymentDetail.tags,
      comment = paymentDetail.comment,
      images = paymentDetail.imageUrls,
      paidAt = paymentDetail.paidAt
    )
    Api.retrofitService.completePayment(id, groupDetail.id, paymentDetail.id)
      .enqueue(object : Callback<PaymentCompleteResponse> {
        override fun onResponse(
          call: Call<PaymentCompleteResponse>,
          response: Response<PaymentCompleteResponse>
        ) {
          if (response.isSuccessful) {
            Toast.makeText(requireContext(), "精算登録しました", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
          } else {
            Toast.makeText(requireContext(), "精算登録に失敗しました", Toast.LENGTH_LONG).show()
          }
        }

        override fun onFailure(call: Call<PaymentCompleteResponse>, t: Throwable) {
          Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
        }
      })
  }

}

class XLabelFormatter(payers: List<NetworkPayer>) : ValueFormatter() {
  private val names = payers.map { it.name }
  override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    return names.getOrNull(value.toInt()) ?: value.toString()
  }
}

