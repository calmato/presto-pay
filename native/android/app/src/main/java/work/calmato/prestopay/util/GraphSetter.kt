package work.calmato.prestopay.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import work.calmato.prestopay.R
import kotlin.math.abs

class XLabelFormatter(private val names: List<String>) : ValueFormatter() {
  override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    return names.getOrNull(value.toInt()) ?: value.toString()
  }
}

fun inflateGraph(chart: BarChart, names:List<String>, amounts:List<Float>, context: Context){
  val entries: MutableList<BarEntry> = arrayListOf()
  val colors = amounts.map {
    if (it > 0) {
      ContextCompat.getColor(context, R.color.positiveNumberColor)
    } else {
      ContextCompat.getColor(context, R.color.negativeNumberColor)
    }
  }
  for ((idx, amount) in amounts.withIndex()) {
    entries.add(BarEntry(idx.toFloat(), abs(amount)))
  }
  val set = BarDataSet(entries, "BarDataSet")
  set.axisDependency = YAxis.AxisDependency.LEFT
  set.colors = colors
  chart.setFitBars(true) // make the x-axis fit exactly all bars
  val xAxis = chart.xAxis
  xAxis.valueFormatter = XLabelFormatter(names.map { it })
  xAxis.position = XAxis.XAxisPosition.BOTTOM
  xAxis.setDrawGridLines(false)
  xAxis.textSize = 14f
  xAxis.granularity = 1f // minimum axis-step (interval) is 1
  xAxis.labelRotationAngle = -45f
  xAxis.spaceMin = 0f
  xAxis.spaceMax = 0f
  val left = chart.axisLeft
  val right = chart.axisRight
  left.axisMinimum = 0f
  right.axisMinimum = 0f
  left.setDrawAxisLine(false) // no axis line
  left.setDrawGridLines(false) // no grid lines
  right.isEnabled = false
  right.setDrawGridLines(false) // no grid line
  left.textSize = 14f
  chart.legend.isEnabled = false
  chart.description.isEnabled = false
  val data = BarData(set)
  data.barWidth = 0.9f // set custom bar width
  data.setValueTextSize(14f)
  chart.data = data
  chart.invalidate() // refresh
}
