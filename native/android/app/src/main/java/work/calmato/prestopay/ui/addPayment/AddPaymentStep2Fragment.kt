package work.calmato.prestopay.ui.addPayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_add_payment_step2.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep2Binding
import work.calmato.prestopay.network.PayerAddPayment
import work.calmato.prestopay.util.AdapterAddPaymentCheck
import work.calmato.prestopay.util.ViewModelAddPayment

class AddPaymentStep2Fragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }

  private lateinit var clickListenerCheck: AdapterAddPaymentCheck.OnClickListener
  private lateinit var recycleAdapterCheck: AdapterAddPaymentCheck

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddPaymentStep2Binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment_step2,container,false)
    binding.lifecycleOwner = this
    clickListenerCheck = AdapterAddPaymentCheck.OnClickListener{
      viewModel.itemIsClicked(it)
    }
    recycleAdapterCheck = AdapterAddPaymentCheck(clickListenerCheck)

    recycleAdapterCheck.payers = viewModel.payersAddPayment.value!!
    binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapterCheck
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      it.isPaid = !it.isPaid
      if(!it.isPaid){
        it.amount = 0f
      }
      changeDataSet()
    })
  }

  private fun changeDataSet(){
    val num = viewModel.payersAddPayment.value?.filter { it.isPaid }?.size
    viewModel.payersAddPayment.value?.filter { it.isPaid }
      ?.map { it.amount = (viewModel.total.value!! / num!!.toFloat()) }
    recycleAdapterCheck.notifyDataSetChanged()
  }
}
