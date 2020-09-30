package work.calmato.prestopay.ui.groupDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_group_detail.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupDetailBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.util.AdapterPayment
import work.calmato.prestopay.util.ViewModelPayment

class GroupDetailFragment : Fragment() {
  private val viewModel: ViewModelPayment by lazy {
    ViewModelProvider(this).get(ViewModelPayment::class.java)
  }
  private var recycleAdapter: AdapterPayment? = null
  private var groupDetail: GroupPropertyResponse? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupDetailBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_detail, container, false)
    binding.lifecycleOwner = this
    binding.viewModelGroupDetail = viewModel
    recycleAdapter = AdapterPayment(requireContext())
    binding.recyclerViewPayment.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    groupDetail = GroupDetailFragmentArgs.fromBundle(requireArguments()).groupDetail
    viewModel.setInitPaymentList(groupDetail!!.id)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    groupDetail?.let {
      viewModel.getPayments(it.id)
    }
    setting.setOnClickListener {
      this.findNavController().navigate(
      GroupDetailFragmentDirections.actionGroupDetailToGroupEditFragment(groupDetail)
      )
    }
    viewModel.paymentsList!!.observe(viewLifecycleOwner, Observer<List<PaymentPropertyGet>> {
      it?.apply {
        recycleAdapter?.paymentList = it
      }
    })
    viewModel.refreshing.observe(viewLifecycleOwner, Observer {
      it?.apply {
        swipeContainer.isRefreshing = it
        floatingActionButton.isClickable = !it
        groupIcon.isClickable = !it
        settleUp.isClickable = !it
        setting.isClickable = !it
      }
    })
    swipeContainer.setOnRefreshListener {
      groupDetail?.let {
        viewModel.getPayments(it.id)
      }
    }
    groupDetail?.thumbnailUrl?.let {
      Picasso.with(requireContext()).load(it).into(groupIcon)
    }
    bottom_navigation.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_person -> {
          this.findNavController().navigate(
            GroupDetailFragmentDirections.actionGroupDetailToAccountHome()
          )
          true
        }
        R.id.action_people -> {
          this.findNavController().navigate(
            GroupDetailFragmentDirections.actionGroupDetailToGroupFriendFragment()
          )
          true
        }
        else -> true
      }
    }
    floatingActionButton.setOnClickListener {
      this.findNavController().navigate(
        GroupDetailFragmentDirections.actionGroupDetailToAddExpenseFragment(groupDetail!!)
      )
    }
  }
}
