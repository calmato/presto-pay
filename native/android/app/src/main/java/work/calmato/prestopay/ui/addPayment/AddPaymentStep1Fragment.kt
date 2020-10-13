package work.calmato.prestopay.ui.addPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_payment_step1.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep1Binding
import work.calmato.prestopay.util.ViewModelAddPayment

class AddPaymentStep1Fragment: Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddPaymentStep1Binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment_step1, container,false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    buttonStep1.setOnClickListener {
      viewModel.setPaymentName(paymentName.text.toString())
    }
  }
}
