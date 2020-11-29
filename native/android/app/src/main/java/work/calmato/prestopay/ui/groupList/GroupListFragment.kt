package work.calmato.prestopay.ui.groupList

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_home.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupListBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelGroup

class GroupListFragment : Fragment() {
  private val viewModelGroup: ViewModelGroup by lazy {
    ViewModelProvider(this).get(ViewModelGroup::class.java)
  }

  private var recycleGroupListAdapter: AdapterGroupPlane? = null
  private lateinit var groupListArg: List<GroupPropertyResponse>

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    groupListArg = GroupListFragmentArgs.fromBundle(requireArguments()).groupsList!!.groups
    if (groupListArg.isEmpty()) {
      viewModelGroup.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
        it?.apply {
          groupListArg = it
          recycleGroupListAdapter?.groupList = it
        }
      })
    } else {
      recycleGroupListAdapter?.groupList = groupListArg
    }
  }

  private lateinit var clickListenerHomeGroup: AdapterGroupPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentGroupListBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false)

    binding.lifecycleOwner = this
    binding.viewModelGroupList = viewModelGroup

    clickListenerHomeGroup =
      AdapterGroupPlane.OnClickListener { viewModelGroup.itemIsClickedGroup(it) }
    recycleGroupListAdapter = AdapterGroupPlane(clickListenerHomeGroup,requireActivity(),false)
    binding.groupChoiceRecycle.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupListAdapter
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModelGroup.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
      it?.apply {
        recycleGroupListAdapter?.groupList = it
        viewModelGroup.endRefreshing()
      }
    })
    viewModelGroup.refreshing.observe(viewLifecycleOwner, Observer<Boolean> {
      it?.apply {
        swipeContainer.isRefreshing = it
      }
    })
    swipeContainer.setOnRefreshListener {
      viewModelGroup.groupListView()
    }
    viewModelGroup.itemClickedGroup.observe(viewLifecycleOwner, Observer {
      it?.apply {
        navigateToAddExpense(it)
      }
    })
    setHasOptionsMenu(true)
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackHome()
        }
      }
    )
  }

  private fun goBackHome() {
    this.findNavController().navigate(
      GroupListFragmentDirections.actionGroupListFragmentToHomeFragment()
    )
  }

  private fun navigateToAddExpense(group:GroupPropertyResponse){
    this.findNavController().navigate(
      GroupListFragmentDirections.actionGroupListFragmentToAddPayment(group,null)
    )
  }

}
