package work.calmato.prestopay.ui.paymentDetail

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_payment_detail.*
import retrofit2.Call
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentPaymentDetailBinding
import work.calmato.prestopay.util.PermissionBase
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.network.*
import work.calmato.prestopay.util.inflateGraph


class PaymentDetailFragment : PermissionBase() {
  private lateinit var paymentDetail: PaymentPropertyGet
  private lateinit var groupDetail: GroupPropertyResponse
  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var id: String
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentPaymentDetailBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_payment_detail, container, false)
    paymentDetail = PaymentDetailFragmentArgs.fromBundle(requireArguments()).paymentPropertyGet
    groupDetail = PaymentDetailFragmentArgs.fromBundle(requireArguments()).groupPropertyResponse
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setHasOptionsMenu(true)
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
    val nonzeroPayers = paymentDetail.payers.filter { it.amount != 0f }
    inflateGraph(
      chart,
      nonzeroPayers.map { it.name},
      nonzeroPayers.map { it.amount },
      requireContext()
    )
    chart.isDoubleTapToZoomEnabled = false
    completed.setOnClickListener {
      val builder: AlertDialog.Builder = requireActivity().let {
        AlertDialog.Builder(it)
      }
      builder.setTitle(resources.getString(R.string.paymentCompleteSetting))
        ?.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
          sendRequest()
        }
        ?.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        ?.setMessage(
          if (!paymentDetail.isCompleted) resources.getString(R.string.messagePaymentCompleted) else resources.getString(
            R.string.messagePaymentUncomplete
          )
        )
      val dialog: AlertDialog? = builder.create()
      dialog?.show()
    }
    setHasOptionsMenu(true)
  }

  private fun sendRequest() {
    progressBarPaymentDetail.visibility = ProgressBar.VISIBLE
    frontViewPaymentDetail.visibility = ImageView.VISIBLE
    Api.retrofitService.completePayment(id, groupDetail.id, paymentDetail.id)
      .enqueue(object : Callback<Unit> {
        override fun onResponse(
          call: Call<Unit>,
          response: Response<Unit>
        ) {
          if (!response.isSuccessful) {
            Toast.makeText(requireContext(), "精算登録に失敗しました", Toast.LENGTH_LONG).show()
          }
          progressBarPaymentDetail.visibility = ProgressBar.GONE
          frontViewPaymentDetail.visibility = ImageView.GONE
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
          progressBarPaymentDetail.visibility = ProgressBar.GONE
          frontViewPaymentDetail.visibility = ImageView.GONE
        }
      })
  }
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.editMenuButton -> {
        this.findNavController().navigate(
          PaymentDetailFragmentDirections.actionPaymentDetailToAddPayment(groupDetail,paymentDetail)
        )
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_edit, menu)
  }
}
