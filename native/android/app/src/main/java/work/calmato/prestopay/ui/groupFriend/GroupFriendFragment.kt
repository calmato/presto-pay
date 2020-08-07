package work.calmato.prestopay.ui.groupFriend

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_group_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupFriendBinding
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.AdapterFriendPlane
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelFriendGroup

class GroupFriendFragment : Fragment() {
  private val viewModel: ViewModelFriendGroup by lazy {
    val activity = requireNotNull(this.activity) {
      "You can only access the viewModel after onActivityCreated()"
    }
    ViewModelProviders.of(this, ViewModelFriendGroup.Factory(activity.application))
      .get(ViewModelFriendGroup::class.java)
  }
  private var recycleAdapter: AdapterFriendPlane? = null
  private var recycleGroupAdapter: AdapterGroupPlane? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.friendsList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
      it?.apply {
        recycleAdapter?.friendList = it
      }
    })

    viewModel.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
      it?.apply {
        recycleGroupAdapter?.groupList = it
      }
    })
  }

  private lateinit var clickListener: AdapterFriendPlane.OnClickListener
  private lateinit var clickListenerGroup:AdapterGroupPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_friend, container, false)

    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    clickListener = AdapterFriendPlane.OnClickListener { viewModel.itemIsClicked(it) }
    clickListenerGroup = AdapterGroupPlane.OnClickListener{viewModel.itemIsClickedGroup(it)}
    recycleAdapter = AdapterFriendPlane(clickListener)
    recycleGroupAdapter = AdapterGroupPlane(clickListenerGroup)
    binding.friendsRecycleView.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }

    viewModel.userListView()
    viewModel.groupListView()
    binding.groupRecycleView.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupAdapter
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupFriendFragmentDirections.actionGroupFriendFragmentToAddFriendFragment()
      )
    }
    addGroup.setOnClickListener {
      this.findNavController().navigate(
        GroupFriendFragmentDirections.actionGroupFriendFragmentToFriendListFragment(
          Users(emptyList<UserProperty>()))
      )
    }
    val animBlink = AnimationUtils.loadAnimation(requireContext(),R.anim.blink)
    viewModel.nowLoading.observe(viewLifecycleOwner, Observer {
      if(it){
        nowLoading.visibility = View.VISIBLE
        nowLoading.startAnimation(animBlink)
      }else{
        nowLoading.clearAnimation()
        nowLoading.visibility = View.INVISIBLE
      }
    })

    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setView(R.layout.dialog_add_friend)
          ?.setPositiveButton(resources.getString(R.string.delete_friend)
          ,DialogInterface.OnClickListener{_,_->
              val builder2:AlertDialog.Builder? = requireActivity().let {
                AlertDialog.Builder(it)
              }
              builder2?.setMessage(resources.getString(R.string.delete_question))
                ?.setPositiveButton(resources.getString(R.string.delete)
                ,DialogInterface.OnClickListener{_,_->
                    viewModel.deleteFriend(it.id,requireActivity())
                  })
                ?.setNegativeButton(resources.getString(R.string.cancel),null)
              val dialog2:AlertDialog? = builder2?.create()
              dialog2?.show()
            })
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
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    userNameText.text = sharedPreferences.getString("name", "")
    val thumbnailUrl = sharedPreferences.getString("thumbnailUrl", "")
    if (thumbnailUrl.isNotEmpty()) {
      Picasso.with(context).load(thumbnailUrl).into(thumbnail)
    }
    groupSwitcher.setOnClickListener {
      if(groupRecycleView.visibility==RecyclerView.VISIBLE && friendsRecycleView.visibility==RecyclerView.VISIBLE){
        groupRecycleView.visibility = RecyclerView.GONE
      }else{
        groupRecycleView.visibility = RecyclerView.VISIBLE
      }
    }
    friendSwitcher.setOnClickListener {
      if(friendsRecycleView.visibility==RecyclerView.VISIBLE && groupRecycleView.visibility==RecyclerView.VISIBLE){
        friendsRecycleView.visibility = RecyclerView.GONE
      }else{
        friendsRecycleView.visibility = RecyclerView.VISIBLE
      }
    }
  }

  companion object {
    internal const val TAG = "GroupFriendFragment"
  }
}
