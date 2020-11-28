package work.calmato.prestopay.ui.settleUpGroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_settle_up_group.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentSettleUpGroupBinding
import work.calmato.prestopay.util.AdapterSettleUpGroup
import work.calmato.prestopay.util.ViewModelSettleUpGroup


class SettleUpGroupFragment : Fragment() {
  private val viewModel: ViewModelSettleUpGroup by lazy {
    ViewModelProvider(this).get(ViewModelSettleUpGroup::class.java)
  }
  private var recycleAdapter: AdapterSettleUpGroup? = null
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentSettleUpGroupBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_settle_up_group, container, false)
    binding.lifecycleOwner = this
    recycleAdapter = AdapterSettleUpGroup()
    binding.parentRecyclerView.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
      addItemDecoration(DividerItemDecoration(
        binding.parentRecyclerView.context,
        DividerItemDecoration.VERTICAL
      ))
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.setUsersPaymentInfo(SettleUpGroupFragmentArgs.fromBundle(requireArguments()).listNetworkPayer.payers)
    val groupDetail = SettleUpGroupFragmentArgs.fromBundle(requireArguments()).groupDetail
    val settleUpList = viewModel.calcForSettleUp()
    recycleAdapter?.settleUpList = settleUpList
    settleUpButton.setOnClickListener {
      viewModel.settleUpApi(groupDetail.id)
    }
    viewModel.nowLoading.observe(viewLifecycleOwner,Observer<Boolean>{
      if(it){
        settleUpButton.isEnabled = false
        progressBarSettleUpGroup.visibility = ProgressBar.VISIBLE
        frontViewSettleUpGroup.visibility = ImageView.VISIBLE
      } else{
        settleUpButton.isEnabled = true
        progressBarSettleUpGroup.visibility = ProgressBar.GONE
        frontViewSettleUpGroup.visibility = ImageView.GONE
      }
    })
    viewModel.navigateBack.observe(viewLifecycleOwner,Observer<Boolean>{
      if(it){
        this.findNavController().navigate(
          SettleUpGroupFragmentDirections.actionSettleUpGroupToGroupDetail(groupDetail)
        )
        viewModel.doneNavigating()
      }
    })
  }
}
