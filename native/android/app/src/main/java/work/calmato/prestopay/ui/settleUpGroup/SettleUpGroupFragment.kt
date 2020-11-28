package work.calmato.prestopay.ui.settleUpGroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentSettleUpGroupBinding
import work.calmato.prestopay.util.ViewModelSettleUpGroup

class SettleUpGroupFragment : Fragment() {
  private val viewModel: ViewModelSettleUpGroup by lazy {
    ViewModelProvider(this).get(ViewModelSettleUpGroup::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentSettleUpGroupBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_settle_up_group, container, false)
    binding.lifecycleOwner = this
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.setUsersPaymentInfo(SettleUpGroupFragmentArgs.fromBundle(requireArguments()).listNetworkPayer.payers)
    Log.i("SettleUpGroupFragment", "onViewCreated: ${viewModel.calcForSettleUp()}")
  }
}
