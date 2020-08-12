package work.calmato.prestopay.ui.groupList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupListBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelGroup

class groupListFragment : Fragment() {
  private val viewModelGroup: ViewModelGroup by lazy {
    val activity = requireNotNull(this.activity) {
      "You can only access the viewModel after onActivityCreated()"
    }
    ViewModelProviders.of(this, ViewModelGroup.Factory(activity.application))
      .get(ViewModelGroup::class.java)
  }

  private var recycleGroupListAdapter: AdapterGroupPlane? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModelGroup.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
      it?.apply {
        recycleGroupListAdapter?.groupList = it
      }
    })
  }

  private lateinit var clickListenerHomeGroup: AdapterGroupPlane.OnClickListener


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupListBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false)

    binding.lifecycleOwner = this
    binding.viewModelGroupList = viewModelGroup

    clickListenerHomeGroup = AdapterGroupPlane.OnClickListener {
      viewModelGroup.itemIsClickedGroup(it)
    }
    recycleGroupListAdapter = AdapterGroupPlane(clickListenerHomeGroup)
    viewModelGroup.groupListView()
    binding.groupChoiceRecycle.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupListAdapter
    }

    return binding.root
  }

}
