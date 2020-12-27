package work.calmato.prestopay.ui.addPayment

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_add_payment.*
import kotlinx.android.synthetic.main.fragment_add_payment.currency
import kotlinx.android.synthetic.main.fragment_add_payment.groupName
import kotlinx.android.synthetic.main.fragment_add_payment.paymentName
import kotlinx.android.synthetic.main.fragment_add_payment_step1.*
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentBindingImpl
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.NetworkPayer
import work.calmato.prestopay.network.UserExpense
import work.calmato.prestopay.util.ViewModelAddPayment
import java.lang.StringBuilder

class AddPaymentFragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(this).get(ViewModelAddPayment::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentAddPaymentBindingImpl =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.setGroupInfo(AddPaymentFragmentArgs.fromBundle(requireArguments()).group)
    AddPaymentFragmentArgs.fromBundle(requireArguments()).payment?.let {payments ->
      viewModel.setPaymentInfo(payments)
      viewModel.setPositivePayers(payments.positivePayers.map { UserExpense(it.id,it.amount) })
      viewModel.setNegativePayers(payments.negativePayers.map { UserExpense(it.id,it.amount) })
    }
    viewModel.setId(MainActivity.firebaseId)
    viewModel.getGroupDetail()
    viewModel.groupName.observe(viewLifecycleOwner, Observer<String> {
      groupName.text = it
    })
    viewModel.total.observe(viewLifecycleOwner, Observer<Float> {
      total.text = it.toString()
    })
    viewModel.currency.observe(viewLifecycleOwner, Observer<String> {
      currency.text = it
    })
    viewModel.paymentName.observe(viewLifecycleOwner, Observer<String> {
      paymentName.text = it
    })
    viewModel.payers.observe(viewLifecycleOwner, Observer<List<NetworkPayer>> { it ->
      paymentName.text =
        it.filter { it.amount > 0 }.joinTo(StringBuilder(), separator = ", ") { it.name }
    })
    viewModel.navigateToGroupDetail.observe(viewLifecycleOwner, Observer<GroupPropertyResponse> {
      it?.let {
        findNavController().navigate(
          AddPaymentFragmentDirections.actionAddPaymentToGroupDetail(viewModel.groupInfo)
        )
        viewModel.navigationCompleted()
      }
    })
    viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
      it?.let {
        findNavController().navigate(
          AddPaymentFragmentDirections.actionAddPaymentToHomeFragment()
        )
        viewModel.navigationCompleted()
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.bad_internet_connection),
          Toast.LENGTH_LONG
        ).show()
      }
    })

    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBack(viewModel.groupInfo)
        }
      }
    )
    viewModel.nowLoading.observe(viewLifecycleOwner, Observer {
      if(it){
        progressBarAddPayment.visibility = ProgressBar.VISIBLE
        frontViewAddPayment.visibility = ImageView.VISIBLE
      }else{
        progressBarAddPayment.visibility = ProgressBar.GONE
        frontViewAddPayment.visibility = ImageView.GONE

      }
    })
  }

  private fun goBack(group: GroupPropertyResponse) {
    this.viewModelStore.clear()
    this.findNavController().navigate(
      AddPaymentFragmentDirections.actionAddPaymentToGroupDetail(group)
    )
  }


}
