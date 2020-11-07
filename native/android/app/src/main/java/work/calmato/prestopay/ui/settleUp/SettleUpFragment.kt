package work.calmato.prestopay.ui.settleUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentSettleUpBindingImpl
import work.calmato.prestopay.util.ViewModelAddPayment

class SettleUpFragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(this).get(ViewModelAddPayment::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding:FragmentSettleUpBindingImpl =
      DataBindingUtil.inflate(inflater, R.layout.fragment_settle_up,container,false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }
}
