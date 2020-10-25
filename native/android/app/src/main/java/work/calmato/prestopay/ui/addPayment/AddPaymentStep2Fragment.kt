package work.calmato.prestopay.ui.addPayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_add_payment_step2.*
import kotlinx.android.synthetic.main.fragment_add_payment_step2_view_pager.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep2Binding
import work.calmato.prestopay.util.AdapterAddPaymentCheck
import work.calmato.prestopay.util.ViewModelAddPayment

class AddPaymentStep2Fragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }
  private lateinit var recycleAdapterCheck: AdapterAddPaymentCheck
  private lateinit var demoCollectionAdapter: DemoCollectionAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddPaymentStep2Binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_payment_step2, container, false)
    binding.lifecycleOwner = this
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    demoCollectionAdapter = DemoCollectionAdapter(this)
    viewPager2.adapter = demoCollectionAdapter
    TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
      tab.text = resources.getStringArray(R.array.dividingMethod)[position]
    }.attach()
  }
}

class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

  override fun getItemCount(): Int = 3

  override fun createFragment(position: Int): Fragment {
    // Return a NEW fragment instance in createFragment(int)
    val fragment = EquallyDivideFragment()
    fragment.arguments = Bundle().apply {
      // Our object is just an integer :-P
      putInt(ARG_OBJECT, position + 1)
    }
    return fragment
  }
}
private const val ARG_OBJECT = "3"
// Instances of this class are fragments representing a single
// object in our collection.
class EquallyDivideFragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment().requireParentFragment()).get(ViewModelAddPayment::class.java)
  }
  private lateinit var recycleAdapterCheck: AdapterAddPaymentCheck
  private lateinit var clickListenerCheck: AdapterAddPaymentCheck.OnClickListener
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_add_payment_step2_view_pager, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    clickListenerCheck = AdapterAddPaymentCheck.OnClickListener {
      viewModel.itemIsClicked(it)
    }
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      it.isPaid = !it.isPaid
      changeDataSet()
      if (!it.isPaid) {
        it.amount = 0f
      }
      recycleAdapterCheck.notifyDataSetChanged()
    })
    arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
      recycleAdapterCheck = AdapterAddPaymentCheck(clickListenerCheck)
      recycleAdapterCheck.payers = viewModel.payersAddPayment.value!!
      recyckerViewPager.apply {
        layoutManager = LinearLayoutManager(context)
        adapter = recycleAdapterCheck
      }
    }
  }
  private fun changeDataSet() {
    val num = viewModel.payersAddPayment.value?.filter { it.isPaid }?.size
    viewModel.payersAddPayment.value?.filter { it.isPaid }
      ?.map { it.amount = (viewModel.total.value!! / num!!.toFloat()) }
  }
}
