package work.calmato.prestopay.ui.friendList

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_friend_list.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentFriendListBinding
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.AdapterRecycleCheck
import work.calmato.prestopay.util.ViewModelFriendGroup

class FriendListFragment : Fragment() {
  private val viewModel = ViewModelFriendGroup()
  private var usersList: Users? = null
  private lateinit var recycleAdapter: AdapterRecycleCheck
  private lateinit var clickListener: AdapterRecycleCheck.OnClickListener
  private lateinit var viewManager: RecyclerView.LayoutManager

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentFriendListBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_friend_list, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    clickListener = AdapterRecycleCheck.OnClickListener { viewModel.itemIsClicked(it) }
    usersList = FriendListFragmentArgs.fromBundle(requireArguments()).friendsList
    if (usersList == null) {
      viewModel.getIdToken()
    }
    recycleAdapter = AdapterRecycleCheck(usersList, clickListener)
    binding.friendsRecycleView.adapter = recycleAdapter
    viewManager = LinearLayoutManager(requireContext())
    binding.friendsRecycleView.apply {
      setHasFixedSize(true)
      layoutManager = viewManager
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.idToken.observe(viewLifecycleOwner, Observer { it ->
      if (null != it) {
        viewModel.getFriends()?.let { obtainedUsersList ->
          usersList = obtainedUsersList
          friendsRecycleView.swapAdapter(
            AdapterRecycleCheck(
              usersList,
              clickListener
            ), false
          )
        }
      }
    })
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

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> this.findNavController().navigate(
        FriendListFragmentDirections.actionFriendListFragmentToCreateGroupFragment(usersList!!)
      )
    }
    return super.onOptionsItemSelected(item)
  }

  private fun goBackToHome() {
    this.findNavController().navigate(
      FriendListFragmentDirections.actionFriendListFragmentToHomeFragment()
    )
  }

  companion object {
    internal const val TAG = "FriendListFragment"
  }
}
