package work.calmato.prestopay.ui.paymentDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentPaymentDetailBinding
import work.calmato.prestopay.network.NetworkPayer
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.util.PermissionBase


class PaymentDetailFragment : PermissionBase() {
  private lateinit var paymentDetail: PaymentPropertyGet
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentPaymentDetailBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_payment_detail, container, false)
    paymentDetail = PaymentDetailFragmentArgs.fromBundle(requireArguments()).paymentPropertyGet
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
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

}

class XLabelFormatter(payers: List<NetworkPayer>) : ValueFormatter() {
  private val names = payers.map { it.name }
  override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    return names.getOrNull(value.toInt()) ?: value.toString()
  }
}

