package work.calmato.prestopay.ui.groupFriend

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_group_friend.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupFriendBinding
import work.calmato.prestopay.network.*
import work.calmato.prestopay.util.AdapterFriendPlane
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelFriendGroup
import work.calmato.prestopay.util.ViewModelGroup

class GroupFriendFragment : Fragment() {
  private val viewModel: ViewModelFriendGroup by lazy {
    ViewModelProvider(this).get(ViewModelFriendGroup::class.java)
  }
  private var groups: List<GroupPropertyResponse>? = null
  private var friends: List<UserProperty>? = null

  private var recycleAdapter: AdapterFriendPlane? = null
  private var recycleGroupAdapter: AdapterGroupPlane? = null

  private lateinit var id: String

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.friendsList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
      it?.apply {
        recycleAdapter?.friendList = it
        friends = it
      }
    })

    progressBar.visibility = android.widget.ProgressBar.INVISIBLE

    viewModel.groupsList.observe(viewLifecycleOwner, Observer<List<GroupPropertyResponse>> {
      it?.apply {
        recycleGroupAdapter?.groupList = it
        groups = it
      }
    })
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!

    val swipeToGroupDismissTouchHelper = getGroupSwipeToDismissTouchHelper(recycleGroupAdapter!!)
    swipeToGroupDismissTouchHelper.attachToRecyclerView(groupRecycleView)

    val swipeToFriendDismissTouchHelper = getFriendSwipeToDismissTouchHelper(recycleAdapter!!)
    swipeToFriendDismissTouchHelper.attachToRecyclerView(friendsRecycleView)
  }

  private lateinit var clickListener: AdapterFriendPlane.OnClickListener
  private lateinit var clickListenerGroup: AdapterGroupPlane.OnClickListener

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
    clickListenerGroup = AdapterGroupPlane.OnClickListener { viewModel.itemIsClickedGroup(it) }
    recycleAdapter = AdapterFriendPlane(clickListener)
    recycleGroupAdapter = AdapterGroupPlane(clickListenerGroup)
    binding.friendsRecycleView.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    binding.groupRecycleView.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupAdapter
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
    viewModel.refreshingGroup.observe(viewLifecycleOwner, Observer<Boolean> {
      it?.apply {
        swipeContainerGroup.isRefreshing = it
      }
    })
    swipeContainer.setOnRefreshListener {
      viewModel.userListView()
    }
    swipeContainerGroup.setOnRefreshListener {
      viewModel.groupListView()
    }

    addFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupFriendFragmentDirections.actionGroupFriendFragmentToAddFriendFragment()
      )
    }
    groupEditAddFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupFriendFragmentDirections.actionGroupFriendFragmentToFriendListFragment(
          Users(emptyList<UserProperty>())
        )
      )
    }
    val animBlink = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
    viewModel.nowLoading.observe(viewLifecycleOwner, Observer {
      if (it) {
        nowLoading.visibility = View.VISIBLE
        nowLoading.startAnimation(animBlink)
      } else {
        nowLoading.clearAnimation()
        nowLoading.visibility = View.INVISIBLE
      }
    })

    frontView.visibility = ImageView.GONE
    progressBar.visibility = android.widget.ProgressBar.INVISIBLE

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    userNameText.text = sharedPreferences.getString("username", "")
    val thumbnailUrl = sharedPreferences.getString("thumbnailUrl", "")
    if (thumbnailUrl.isNotEmpty()) {
      Picasso.with(context).load(thumbnailUrl).into(thumbnail)
    }
    groupSwitcher.setOnClickListener {
      if (swipeContainerGroup.visibility == RecyclerView.VISIBLE && swipeContainer.visibility == RecyclerView.VISIBLE) {
        swipeContainerGroup.visibility = RecyclerView.GONE
      } else {
        swipeContainerGroup.visibility = RecyclerView.VISIBLE
      }
    }
    friendSwitcher.setOnClickListener {
      if (swipeContainer.visibility == RecyclerView.VISIBLE && swipeContainerGroup.visibility == RecyclerView.VISIBLE) {
        swipeContainer.visibility = RecyclerView.GONE
      } else {
        swipeContainer.visibility = RecyclerView.VISIBLE
      }
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          findNavController().navigate(
            GroupFriendFragmentDirections.actionGroupFriendFragmentToHomeFragment()
          )
        }
      }
    )
  }

  private fun getGroupSwipeToDismissTouchHelper(adapter: RecyclerView.Adapter<AdapterGroupPlane.AddGroupViewHolder>) =
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.LEFT,
      ItemTouchHelper.LEFT
    ) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        return false
      }

      //スワイプ時に実行
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setMessage(resources.getString(R.string.delete_question))
          ?.setPositiveButton(
            resources.getString(R.string.delete),
            DialogInterface.OnClickListener { _, _ ->
              frontView.visibility = ImageView.VISIBLE
              progressBar.visibility = android.widget.ProgressBar.VISIBLE

              Api.retrofitService.deleteGroup(
                "Bearer ${id}",
                groups!![viewHolder.adapterPosition].id
              )
                .enqueue(object : Callback<Unit> {
                  override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                  ) {
                    Log.d(ViewModelGroup.TAG, response.body().toString())
                    viewModel.deleteGroup(
                      groups!![viewHolder.adapterPosition].id,
                      requireActivity()
                    )
                    frontView.visibility = ImageView.GONE
                    progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                  }

                  override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                    Log.d(ViewModelGroup.TAG, t.message)
                    frontView.visibility = ImageView.GONE
                  }
                })
            })
          ?.setNegativeButton(resources.getString(R.string.cancel), null)
        recycleGroupAdapter?.notifyDataSetChanged()
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
      }

      //スワイプした時の背景を設定
      override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
      ) {
        super.onChildDraw(
          c,
          recyclerView,
          viewHolder,
          dX,
          dY,
          actionState,
          isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val background = ColorDrawable(Color.RED)
        val deleteIcon = AppCompatResources.getDrawable(
          requireContext(),
          R.drawable.ic_baseline_delete_sweep_24
        )
        val iconMarginVertical =
          (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2
        deleteIcon.setBounds(
          itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
          itemView.top + iconMarginVertical,
          itemView.right - iconMarginVertical,
          itemView.bottom - iconMarginVertical
        )
        background.setBounds(
          itemView.right,
          itemView.top,
          itemView.left + dX.toInt(),
          itemView.bottom
        )
        background.draw(c)
        deleteIcon.draw(c)
      }

    })

  private fun getFriendSwipeToDismissTouchHelper(adapter: RecyclerView.Adapter<AdapterFriendPlane.AddFriendViewHolder>) =
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.LEFT,
      ItemTouchHelper.LEFT
    ) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        return false
      }

      //スワイプ時に実行
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setMessage(resources.getString(R.string.delete_question))
          ?.setPositiveButton(
            resources.getString(R.string.delete),
            DialogInterface.OnClickListener { _, _ ->
              frontView.visibility = ImageView.VISIBLE
              progressBar.visibility = android.widget.ProgressBar.VISIBLE
              Api.retrofitService.deleteFriend(
                "Bearer ${id}",
                friends!![viewHolder.adapterPosition].id
              )
                .enqueue(object : Callback<AccountResponse> {
                  override fun onResponse(
                    call: Call<AccountResponse>,
                    response: Response<AccountResponse>
                  ) {
                    Log.d(ViewModelGroup.TAG, response.body().toString())
                    viewModel.deleteFriendSwipe(
                      friends!![viewHolder.adapterPosition].id,
                      requireActivity()
                    )
                    frontView.visibility = ImageView.GONE
                    progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                  }

                  override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                    Log.d(ViewModelGroup.TAG, t.message)
                    frontView.visibility = ImageView.GONE
                  }
                })
            })
          ?.setNegativeButton(resources.getString(R.string.cancel), null)
        recycleAdapter?.notifyDataSetChanged()
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
      }

      //スワイプした時の背景を設定
      override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
      ) {
        super.onChildDraw(
          c,
          recyclerView,
          viewHolder,
          dX,
          dY,
          actionState,
          isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val background = ColorDrawable(Color.RED)
        val deleteIcon = AppCompatResources.getDrawable(
          requireContext(),
          R.drawable.ic_baseline_delete_sweep_24
        )
        val iconMarginVertical =
          (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2

        deleteIcon.setBounds(
          itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
          itemView.top + iconMarginVertical,
          itemView.right - iconMarginVertical,
          itemView.bottom - iconMarginVertical
        )
        background.setBounds(
          itemView.right,
          itemView.top,
          itemView.left + dX.toInt(),
          itemView.bottom
        )
        background.draw(c)
        deleteIcon.draw(c)
      }

    })

  companion object {
    internal const val TAG = "GroupFriendFragment"
  }
}
