package work.calmato.prestopay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentHomeBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelGroup


class HomeFragment : Fragment() {
  private val viewModelGroup: ViewModelGroup by lazy {
    val activity = requireNotNull(this.activity) {
      "You can only access the viewModel after onActivityCreated()"
    }
    ViewModelProviders.of(this, ViewModelGroup.Factory(activity.application))
      .get(ViewModelGroup::class.java)
  }

  private var recycleGroupAdapter: AdapterGroupPlane? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModelGroup.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
      it?.apply {
        recycleGroupAdapter?.groupList = it
      }
    })
  }

  private lateinit var clickListenerHomeGroup: AdapterGroupPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentHomeBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

    binding.lifecycleOwner = this
    binding.viewModelHome = viewModelGroup

    clickListenerHomeGroup = AdapterGroupPlane.OnClickListener {
      viewModelGroup.itemIsClickedGroup(it)
    }
    recycleGroupAdapter = AdapterGroupPlane(clickListenerHomeGroup)
    viewModelGroup.groupListView()
    binding.groupHomeRecycle.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupAdapter
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    floatingActionButton.setOnClickListener {
      this.findNavController().navigate(
        HomeFragmentDirections.actionHomeFragmentToAddExpenseFragment()
      )
    }
    bottom_navigation.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_person -> {
          this.findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToAccountHome()
          )
          true
        }
        R.id.action_people -> {
          this.findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToGroupFriendFragment()
          )
          true
        }
        else -> false
      }
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
      })
  }
}
