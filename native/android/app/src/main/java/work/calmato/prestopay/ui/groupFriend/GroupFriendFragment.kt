package work.calmato.prestopay.ui.groupFriend

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_group_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupFriendBinding
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.AdapterRecyclePlane
import work.calmato.prestopay.util.ViewModelFriendGroup

class GroupFriendFragment : Fragment() {
  private val viewModel = ViewModelFriendGroup()
  private var usersList: Users? = null
  private lateinit var clickListener: AdapterRecyclePlane.OnClickListener
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var recycleAdapter: AdapterRecyclePlane

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_friend, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    viewModel.getIdToken()
    clickListener = AdapterRecyclePlane.OnClickListener { viewModel.itemIsClicked(it) }
    recycleAdapter = AdapterRecyclePlane(usersList, clickListener)
    viewManager = LinearLayoutManager(requireContext())
    binding.friendsRecycleView.apply {
      setHasFixedSize(true)
    }
    binding.friendsRecycleView.adapter = recycleAdapter
    binding.friendsRecycleView.layoutManager = viewManager
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupFriendFragmentDirections.actionGroupFriendFragmentToAddFriendFragment()
      )
    }
    viewModel.idToken.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        viewModel.getFriends(requireActivity())
      }
    })
    viewModel.usersList.observe(viewLifecycleOwner, Observer {
      friendsRecycleView.swapAdapter(AdapterRecyclePlane(it, clickListener), false)
    })

    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setView(R.layout.dialog_add_friend)
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
        val name = dialog?.findViewById<TextView>(R.id.username_dialog)
        val thumbnail = dialog?.findViewById<ImageView>(R.id.thumbnail_dialog)
        name!!.text = it.name
        if (it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty()) {
          Picasso.with(context).load(it.thumbnailUrl).into(thumbnail)
        }
        viewModel.itemIsClickedCompleted()
      }
    })
  }

  companion object {
    internal const val TAG = "GroupFriendFragment"
  }
}
