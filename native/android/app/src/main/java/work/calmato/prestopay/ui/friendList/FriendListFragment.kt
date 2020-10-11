package work.calmato.prestopay.ui.friendList

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_friend_list.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentFriendListBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.AdapterCheck
import work.calmato.prestopay.util.ViewModelFriend
import java.util.*

class FriendListFragment : Fragment() {
  private val viewModel: ViewModelFriend by lazy {
    ViewModelProvider(this).get(ViewModelFriend::class.java)
  }

  private var recycleAdapter: AdapterCheck? = null
  private lateinit var friendListArg: List<UserProperty>

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    friendListArg = FriendListFragmentArgs.fromBundle(requireArguments()).friendsList!!.users
    if (friendListArg.isEmpty()) {
      viewModel.friendsList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
        it?.apply {
          friendListArg = it
          recycleAdapter?.friendList = it
        }
      })
    } else {
      recycleAdapter?.friendList = friendListArg
    }
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
    viewModel.refreshingFriend.observe(viewLifecycleOwner, Observer<Boolean> {
      it?.apply {
        swipeContainer.isRefreshing = it
      }
    })
    swipeContainer.setOnRefreshListener {
      viewModel.userListView()
    }
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
    searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
      override fun onQueryTextSubmit(query: String?): Boolean {
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
          if (newText.isNotEmpty()) {
            recycleAdapter?.friendList = friendListArg.filter { it.name.startsWith(newText,true) }
          }else{
            recycleAdapter?.friendList = friendListArg
          }
        }
        return true
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> this.findNavController().navigate(
        FriendListFragmentDirections.actionFriendListFragmentToCreateGroupFragment(
          Users(
            friendListArg
          )
        )
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
