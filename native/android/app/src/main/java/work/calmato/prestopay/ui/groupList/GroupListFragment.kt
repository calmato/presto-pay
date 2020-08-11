package work.calmato.prestopay.ui.groupList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupListBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelGroup

class GroupListFragment : Fragment() {
  private val groupListModel: ViewModelGroup by lazy {
    val activity = requireNotNull(this.activity) {
      "You can only access the viewModel after onActivityCreated()"
    }
    ViewModelProviders.of(this, ViewModelGroup.Factory(activity.application))
      .get(ViewModelGroup::class.java)
  }

  private var recycleGroupAdapter: AdapterGroupPlane? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    groupListModel.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
      it?.apply {
        recycleGroupAdapter?.groupList = it
      }
    })
  }

  private lateinit var clickListenerListGroup: AdapterGroupPlane.OnClickListener
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupListBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false)
    binding.lifecycleOwner = this
    binding.viewModelGrouplist = groupListModel

    clickListenerListGroup = AdapterGroupPlane.OnClickListener {
      groupListModel.itemIsClickedGroup(it)
    }
    recycleGroupAdapter = AdapterGroupPlane(clickListenerListGroup)
    binding.root.findViewById<RecyclerView>(R.id.groupsListView).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    groupListModel.itemClickedGroup.observe(viewLifecycleOwner, Observer {
      if (it != null) {
        it.checked = !it.checked
        groupListModel.itemIsClickedCompleted()
      }
    })
    setHasOptionsMenu(true)
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
      }
    )

  }

  companion object {
    internal const val TAG = "GroupListFragment"
  }

}
