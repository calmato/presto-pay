package work.calmato.prestopay.ui.addPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_payment_step4.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep4Binding
import work.calmato.prestopay.util.ViewModelAddPayment
import work.calmato.prestopay.util.inflateGraph

class AddPaymentStep4Fragment:Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }

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
  }
}
