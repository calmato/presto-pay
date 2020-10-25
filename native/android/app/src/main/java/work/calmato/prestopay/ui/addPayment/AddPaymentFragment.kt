package work.calmato.prestopay.ui.addPayment

import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_expense.*
import kotlinx.android.synthetic.main.fragment_add_payment.*
import kotlinx.android.synthetic.main.fragment_add_payment.groupName
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentBindingImpl
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.NationalFlag
import work.calmato.prestopay.network.NetworkPayer
import work.calmato.prestopay.ui.addExpense.AddExpenseFragment
import work.calmato.prestopay.util.AdapterCurrency
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
  ): View? {
    val binding: FragmentAddPaymentBindingImpl =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.setGroupInfo(AddPaymentFragmentArgs.fromBundle(requireArguments()).group)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    viewModel.setId(sharedPreferences.getString("token", "")!!)
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


    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBack(viewModel.groupInfo)
        }
      }
    )
  }

  private fun goBack(group : GroupPropertyResponse){
    this.viewModelStore.clear()
    this.findNavController().navigate(
      AddPaymentFragmentDirections.actionAddPaymentToGroupDetail(group)
    )
  }


}