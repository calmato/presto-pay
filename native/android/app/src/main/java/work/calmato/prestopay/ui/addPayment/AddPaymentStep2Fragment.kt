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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_add_payment_step1.*
import kotlinx.android.synthetic.main.fragment_add_payment_step2.*
import kotlinx.android.synthetic.main.fragment_add_payment_step2_view_pager.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddPaymentStep2Binding
import work.calmato.prestopay.network.PayerAddPayment
import work.calmato.prestopay.network.UserExpense
import work.calmato.prestopay.util.AdapterAddPaymentCheck
import work.calmato.prestopay.util.AdapterAddPaymentInputAmount
import work.calmato.prestopay.util.Constant.Companion.STEP2
import work.calmato.prestopay.util.Constant.Companion.STEP3
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

    demoCollectionAdapter = DemoCollectionAdapter(this,STEP2)
    viewPager2.adapter = demoCollectionAdapter
    // ViewPager2のタブを作成
    TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
      tab.text = resources.getStringArray(R.array.dividingMethod)[position]
    }.attach()
  }
}
//ViewPager2の入れ物
class DemoCollectionAdapter(fragment: Fragment, private val step: Int) : FragmentStateAdapter(fragment) {

  override fun getItemCount(): Int = 3

  override fun createFragment(position: Int): Fragment {
    // Return a NEW fragment instance in createFragment(int)
    val fragment = EquallyDivideFragment(position,step)
    fragment.arguments = Bundle().apply {
      putInt(ARG_OBJECT, position)
    }
    return fragment
  }
}

private const val ARG_OBJECT = "3"

// ViewPager2の中身
class EquallyDivideFragment(val position: Int,private val step:Int) : Fragment() {
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
    val payersList : List<PayerAddPayment> = viewModel.payersAddPayment.value!!.map {
      PayerAddPayment(it.id,it.name,it.thumbnail,it.amount,false)
    }
    arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
      when (position) {
        //割り勘画面
        0 -> {
          val amountList = payersList.map { UserExpense(it.id, 0f) }.toMutableList()
          if (step == STEP2) {
            viewModel.editPaymentPositivePayers.value?.let {
              it.map { viewModelList ->
                amountList.forEach { thisList ->
                  if (thisList.id == viewModelList.id) {
                    thisList.amount = viewModelList.amount
                  }
                }
              }
            }
          } else if (step == STEP3) {
            viewModel.editPaymentNegativePayers.value?.let {
              it.map { viewModelList ->
                amountList.forEach { thisList ->
                  if (thisList.id == viewModelList.id) {
                    thisList.amount = viewModelList.amount
                  }
                }
              }
            }
          }
          clickListenerCheck = AdapterAddPaymentCheck.OnClickListener {
            viewModel.itemIsClicked(it)
          }
          recycleAdapterCheck =
            AdapterAddPaymentCheck(clickListenerCheck, viewModel.currency.value!!)
          recycleAdapterCheck.payers = payersList
          recycleAdapterCheck.amounts = amountList.map { it.amount }
          //　割り勘計算
          viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
            it.isPaid = !it.isPaid
            val num = payersList.filter { it.isPaid }.size
            payersList.mapIndexed { index, payer ->
              if (payer.isPaid) {
                amountList[index].amount =
                  round(viewModel.total.value!! / num.toFloat() * 100) / 100
              } else {
                amountList[index].amount = 0f
              }
            }
            Log.i("AddPaymentStep", "onViewCreated: $amountList")
            recycleAdapterCheck.amounts = amountList.map { it.amount }
          })
          recyckerViewPager.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycleAdapterCheck
          }
          nextButton.setOnClickListener {
            // 1人はチェックされてなければならない確認
            if (recycleAdapterCheck.payers.any { it.isPaid }) {
              if (step == STEP2) {
                viewModel.setLendersList(recycleAdapterCheck.amounts)
                navigateToStep3()
                Log.i("AddPaymentStep", "onViewCreated: ${viewModel.payersAddPayment.value!!}")
              } else if (step == STEP3) {
                viewModel.setBorrowersList(recycleAdapterCheck.amounts)
                navigateToStep4()
              }
            } else {
              Toast.makeText(
                requireContext(),
                resources.getString(R.string.choose_involved_users),
                Toast.LENGTH_LONG
              ).show()
            }
          }
        }
        //金額入力の画面
        1 -> {
          val amountList = payersList.map { UserExpense(it.id, 0f) }.toMutableList()
          if (step == STEP2) {
            viewModel.editPaymentPositivePayers.value?.let {
              it.map { viewModelList ->
                amountList.forEach { thisList ->
                  if (thisList.id == viewModelList.id) {
                    thisList.amount = viewModelList.amount
                  }
                }
              }
            }
          } else if (step == STEP3) {
            viewModel.editPaymentNegativePayers.value?.let {
              it.map { viewModelList ->
                amountList.forEach { thisList ->
                  if (thisList.id == viewModelList.id) {
                    thisList.amount = viewModelList.amount
                  }
                }
              }
            }
          }
          recycleAdapterInputAmount2 = AdapterAddPaymentInputAmount(viewModel.currency.value!!)
          recycleAdapterInputAmount2.payers = payersList
          recycleAdapterInputAmount2.amounts = amountList.map { it.amount } as MutableList<Float>
          recyckerViewPager.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycleAdapterInputAmount2
          }
          nextButton.setOnClickListener {
            //合計金額があっているかの確認
            if(recycleAdapterInputAmount2.amounts.sum()==viewModel.total.value!!){
              if (step== STEP2){
                viewModel.setLendersList(recycleAdapterInputAmount2.amounts)
                navigateToStep3()
                Log.i("AddPaymentStep", "onViewCreated: ${viewModel.payersAddPayment.value!!}")
              }else if(step== STEP3){
                viewModel.setBorrowersList(recycleAdapterInputAmount2.amounts)
                navigateToStep4()
              }
            }else {
              Toast.makeText(requireContext(),resources.getString(R.string.amount_do_not_up_to_toal_cost),Toast.LENGTH_LONG).show()
            }
          }
        }
        //割合入力の画面
        2 -> {
          val amountList = payersList.map { UserExpense(it.id, 0f) }
          if (step == STEP2) {
            viewModel.editPaymentPositivePayers.value?.let {
              it.map { viewModelList ->
                amountList.forEach { thisList ->
                  if (thisList.id == viewModelList.id) {
                    thisList.amount = viewModelList.amount/viewModel.total.value!!
                  }
                }
              }
            }
          } else if (step == STEP3) {
            viewModel.editPaymentNegativePayers.value?.let {
              it.map { viewModelList ->
                amountList.forEach { thisList ->
                  if (thisList.id == viewModelList.id) {
                    thisList.amount = viewModelList.amount/viewModel.total.value!!
                  }
                }
              }
            }
          }
          recycleAdapterInputAmount3 = AdapterAddPaymentInputAmount("%")
          recycleAdapterInputAmount3.payers = payersList
          recycleAdapterInputAmount3.amounts = amountList.map { it.amount } as MutableList<Float>
          recyckerViewPager.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycleAdapterInputAmount3
          }
          nextButton.setOnClickListener {
            // 合計が100％かの確認
            if(recycleAdapterInputAmount3.amounts.sum() == 100f){
              if(step== STEP2){
                //ViewModel内の支払いデータに反映
                viewModel.setLendersList(recycleAdapterInputAmount3.amounts.map { it * viewModel.total.value!! / 100 })
                navigateToStep3()
                Log.i("AddPaymentStep", "onViewCreated: ${recycleAdapterInputAmount3.amounts}")
              }else if(step== STEP3){
                viewModel.setBorrowersList(recycleAdapterInputAmount3.amounts.map { it * viewModel.total.value!! / 100 })
                navigateToStep4()
              }
            } else{
             Toast.makeText(requireContext(),resources.getString(R.string.amount_do_not_up_to_100p),Toast.LENGTH_LONG).show()
            }
          }
        }
      }
    }
  }
  private fun navigateToStep3(){
    this.findNavController().navigate(
      AddPaymentStep2FragmentDirections.actionAddPaymentStep2ToAddPaymentStep3()
    )
  }
  private fun navigateToStep4(){
    this.findNavController().navigate(
      AddPaymentStep3FragmentDirections.actionAddPaymentStep3ToAddPaymentStep4()
    )
  }
}
