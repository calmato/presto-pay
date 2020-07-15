package work.calmato.prestopay.ui.friendList

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentFriendListBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.util.AdapterCheck
import work.calmato.prestopay.util.ViewModelFriendGroup

class FriendListFragment : Fragment() {
  private val viewModel  : ViewModelFriendGroup by lazy {
    val activity = requireNotNull(this.activity){
      "You can only access the viewModel after onActivityCreated()"
    }
    ViewModelProviders.of(this,ViewModelFriendGroup.Factory(activity.application))
      .get(ViewModelFriendGroup::class.java)
  }

  private var recycleAdapter: AdapterCheck? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.friendsList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
      it?.apply {
          recycleAdapter?.friendList = it
      }
    })
  }

  private lateinit var clickListener: AdapterCheck.OnClickListener
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentFriendListBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_friend_list, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    clickListener = AdapterCheck.OnClickListener { viewModel.itemIsClicked(it) }
    recycleAdapter = AdapterCheck(clickListener)
    binding.root.findViewById<RecyclerView>(R.id.friendsRecycleView).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        it.checked = !it.checked
        viewModel.itemIsClickedCompleted()
      }
    })
    setHasOptionsMenu(true)
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackToHome()
        }
      }
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }

//  override fun onOptionsItemSelected(item: MenuItem): Boolean {
//    when (item.itemId) {
//      R.id.done -> this.findNavController().navigate(
//        FriendListFragmentDirections.actionFriendListFragmentToCreateGroupFragment(usersList!!)
//      )
//    }
//    return super.onOptionsItemSelected(item)
//  }

  private fun goBackToHome() {
    this.findNavController().navigate(
      FriendListFragmentDirections.actionFriendListFragmentToHomeFragment()
    )
  }

  companion object {
    internal const val TAG = "FriendListFragment"
  }
}
