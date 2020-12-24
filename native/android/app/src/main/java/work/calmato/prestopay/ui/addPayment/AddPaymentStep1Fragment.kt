package work.calmato.prestopay.ui.addPayment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_payment.*
import kotlinx.android.synthetic.main.fragment_add_payment_step1.*
import kotlinx.android.synthetic.main.fragment_add_payment_step1.currency
import kotlinx.android.synthetic.main.fragment_add_payment_step1.paymentName
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep1Binding
import work.calmato.prestopay.util.AdapterCurrency
import work.calmato.prestopay.util.ViewModelAddPayment
import java.util.*

class AddPaymentStep1Fragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentAddPaymentStep1Binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment_step1, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.getCountryList()
    viewModel.setTag()
    currency.text = MainActivity.currency.toUpperCase(Locale.ROOT)
    buttonStep4.setOnClickListener {
      when {
          paymentName.text.isNullOrEmpty() -> {
            Toast.makeText(
              requireContext(),
              resources.getString(R.string.fill_expense_name),
              Toast.LENGTH_LONG
            ).show()
          }
          amount.text.isNullOrEmpty() -> {
            Toast.makeText(requireContext(), R.string.fill_total_amount, Toast.LENGTH_LONG).show()
          }
          amount.text.toString().toFloat() >= 1000000 -> {
            Toast.makeText(requireContext(), R.string.amount_too_high, Toast.LENGTH_LONG).show()
          }
          else -> {
            viewModel.setPaymentName(paymentName.text.toString())
            viewModel.setTotal(amount.text.toString().toFloat())
            viewModel.setCurrency(currency.text.toString())
            this.findNavController().navigate(
              AddPaymentStep1FragmentDirections.actionAddPaymentStep1ToAddPaymentStep2()
            )
          }
      }
    }
    currency.setOnClickListener {
      showCurrencyDialog()
    }
    viewModel.paymentInfo?.let {
      paymentName.setText(it.name)
      amount.setText(it.total.toString())
      currency.text = it.currency
    }
    paymentName.setOnFocusChangeListener { view, b ->
      if(b){
        requireParentFragment().requireParentFragment().groupName.visibility = TextView.INVISIBLE
      }else{
        requireParentFragment().requireParentFragment().groupName.visibility = TextView.VISIBLE
      }
    }
  }

  private fun showCurrencyDialog() {
    val builder: AlertDialog.Builder = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val currencyRecycleAdapter = AdapterCurrency(AdapterCurrency.OnClickListener {
      currency.text = it.name.toUpperCase(Locale.ROOT)
    })
    currencyRecycleAdapter.countryList = viewModel.countryList
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = currencyRecycleAdapter
    }
    builder.setView(recycleView)
    val dialog: AlertDialog? = builder.create()
    dialog?.show()
    currency.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dialog?.dismiss()
      }

      override fun afterTextChanged(s: Editable?) {
      }

    })
  }
}
