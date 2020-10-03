package work.calmato.prestopay.ui.paymentDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentPaymentDetailBinding
import work.calmato.prestopay.util.PermissionBase

class PaymentDetailFragment : PermissionBase()  {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentPaymentDetailBinding =
      DataBindingUtil.inflate(inflater,R.layout.fragment_payment_detail,container,false)
    return binding.root
  }
}
