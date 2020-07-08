package work.calmato.prestopay.ui.friendList

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
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
import work.calmato.prestopay.util.AdapterUser
import work.calmato.prestopay.util.ViewModelFriendGroup

class FriendListFragment: Fragment() {
  private val viewModel = ViewModelFriendGroup()
  private var usersList:Users? = null
  private lateinit var adapter : AdapterUser
  private lateinit var clickListener: AdapterUser.OnClickListener
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
    clickListener = AdapterUser.OnClickListener { viewModel.itemIsClicked(it) }
    adapter = AdapterUser(usersList, clickListener, CheckBox.VISIBLE)
    binding.friendsRecycleView.adapter = adapter
    viewManager = LinearLayoutManager(requireContext())
    binding.friendsRecycleView.apply {
      setHasFixedSize(true)
      layoutManager = viewManager
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.idToken.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        usersList = viewModel.getFriends()
        usersList?.let {
          friendsRecycleView.swapAdapter(
            AdapterUser(
              usersList,
              clickListener,
              CheckBox.VISIBLE
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
  }
  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> this.findNavController().navigate(
        FriendListFragmentDirections.actionFriendListFragmentToCreateGroupFragment()
      )
    }
    return super.onOptionsItemSelected(item)
  }
  companion object {
    internal const val TAG = "FriendListFragment"
  }
}
