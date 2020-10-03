package work.calmato.prestopay.ui.paymentDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_payment_detail.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentPaymentDetailBinding
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.util.PermissionBase

class PaymentDetailFragment : PermissionBase()  {
  private lateinit var paymentDetail: PaymentPropertyGet
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentPaymentDetailBinding =
      DataBindingUtil.inflate(inflater,R.layout.fragment_payment_detail,container,false)
    paymentDetail = PaymentDetailFragmentArgs.fromBundle(requireArguments()).paymentPropertyGet
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    date.text = paymentDetail.createdAt.split("T")[0]
    paymentDetail.imageUrls!![0].let {
      if(it.isNotEmpty()){
        Picasso.with(requireContext()).load(it).into(thumbnail)
      }
    }
    expenseName.text = paymentDetail.name
    amount.text = paymentDetail.total.toString()
    comment.text = paymentDetail.comment
  }
}
