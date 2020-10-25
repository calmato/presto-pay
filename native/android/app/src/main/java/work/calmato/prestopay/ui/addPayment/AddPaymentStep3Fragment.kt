package work.calmato.prestopay.ui.addPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_add_payment_step2.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep2Binding

class AddPaymentStep3Fragment : Fragment()  {
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
    step.text = "Step3"
    sentence.text = "対象者は？"
    demoCollectionAdapter = DemoCollectionAdapter(this)
    viewPager2.adapter = demoCollectionAdapter
    // ViewPager2のタブを作成
    TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
      tab.text = resources.getStringArray(R.array.dividingMethod)[position]
    }.attach()
  }
}
