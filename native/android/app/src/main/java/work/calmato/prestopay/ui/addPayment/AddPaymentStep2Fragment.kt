package work.calmato.prestopay.ui.addPayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_add_payment_step1.*
import kotlinx.android.synthetic.main.fragment_add_payment_step2.*
import kotlinx.android.synthetic.main.fragment_add_payment_step2_view_pager.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep2Binding
import work.calmato.prestopay.util.AdapterAddPaymentCheck
import work.calmato.prestopay.util.AdapterAddPaymentInputAmount
import work.calmato.prestopay.util.ViewModelAddPayment
import kotlin.math.round

class AddPaymentStep2Fragment : Fragment() {
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
    // ViewPager2のタブを作成
    TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
      tab.text = resources.getStringArray(R.array.dividingMethod)[position]
    }.attach()
  }
}
//ViewPager2の入れ物
class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

  override fun getItemCount(): Int = 3

  override fun createFragment(position: Int): Fragment {
    // Return a NEW fragment instance in createFragment(int)
    val fragment = EquallyDivideFragment(position)
    fragment.arguments = Bundle().apply {
      putInt(ARG_OBJECT, position)
    }
    return fragment
  }
}

private const val ARG_OBJECT = "3"

// ViewPager2の中身
class EquallyDivideFragment(val position: Int) : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(requireParentFragment().requireParentFragment().requireParentFragment()).get(
      ViewModelAddPayment::class.java
    )
  }
  //3つのアダプターを用意することで3種類の割り勘方法を実装
  private lateinit var recycleAdapterCheck: AdapterAddPaymentCheck
  private lateinit var recycleAdapterInputAmount2: AdapterAddPaymentInputAmount
  private lateinit var recycleAdapterInputAmount3: AdapterAddPaymentInputAmount
  private lateinit var clickListenerCheck: AdapterAddPaymentCheck.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_add_payment_step2_view_pager, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val payersList = viewModel.payersAddPayment.value!!
    arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
      when (position) {
        //割り勘画面
        0 -> {
          val amountList = MutableList(payersList.size) { 0f }
          clickListenerCheck = AdapterAddPaymentCheck.OnClickListener {
            viewModel.itemIsClicked(it)
          }
          recycleAdapterCheck =
            AdapterAddPaymentCheck(clickListenerCheck, viewModel.currency.value!!)
          recycleAdapterCheck.payers = payersList
          recycleAdapterCheck.amounts = amountList
          //　割り勘計算
          viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
            it.isPaid = !it.isPaid
            val num = payersList.filter { it.isPaid }.size
            payersList.mapIndexed { index, payer ->
              if (payer.isPaid) {
                amountList[index] = round(viewModel.total.value!! / num.toFloat() * 100) / 100
              } else {
                amountList[index] = 0f
              }
            }
            recycleAdapterCheck.notifyDataSetChanged()
          })
          recyckerViewPager.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycleAdapterCheck
          }
          nextButton.setOnClickListener {
            viewModel.payersAddPayment.value!!.mapIndexed { index, payerAddPayment ->
              payerAddPayment.amount = recycleAdapterCheck.amounts[index]
            }
            Log.i("AddPaymentStep", "onViewCreated: ${viewModel.payersAddPayment.value!!}")
          }
        }
        //金額入力の画面
        1 -> {
          val amountList = MutableList(payersList.size) { 0f }
          recycleAdapterInputAmount2 = AdapterAddPaymentInputAmount(viewModel.currency.value!!)
          recycleAdapterInputAmount2.payers = payersList
          recycleAdapterInputAmount2.amounts = amountList
          recyckerViewPager.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycleAdapterInputAmount2
          }
          nextButton.setOnClickListener {
            //合計金額があっているかの確認
            if(recycleAdapterInputAmount2.amounts.sum()==viewModel.total.value!!){
              viewModel.payersAddPayment.value!!.mapIndexed { index, payerAddPayment ->
                payerAddPayment.amount = recycleAdapterInputAmount2.amounts[index]
              }
              Log.i("AddPaymentStep", "onViewCreated: ${viewModel.payersAddPayment.value!!}")
            }else {
              Toast.makeText(requireContext(),"支払い合計が総額と一致しません",Toast.LENGTH_LONG).show()
            }
          }
        }
        //割合入力の画面
        2 -> {
          val amountList = MutableList(payersList.size) { 0f }
          recycleAdapterInputAmount3 = AdapterAddPaymentInputAmount("%")
          recycleAdapterInputAmount3.payers = payersList
          recycleAdapterInputAmount3.amounts = amountList
          recyckerViewPager.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycleAdapterInputAmount3
          }
          nextButton.setOnClickListener {
            // 合計が100％かの確認
            if(recycleAdapterInputAmount3.amounts.sum() == 100f){
              //ViewModel内の支払いデータに反映
              viewModel.payersAddPayment.value!!.mapIndexed { index, payerAddPayment ->
                payerAddPayment.amount = recycleAdapterInputAmount3.amounts[index]
              }
              Log.i("AddPaymentStep", "onViewCreated: ${recycleAdapterInputAmount3.amounts}")
            } else{
             Toast.makeText(requireContext(),"合計を100％にしてください",Toast.LENGTH_LONG).show()
            }
          }
        }
      }
    }
  }
}
