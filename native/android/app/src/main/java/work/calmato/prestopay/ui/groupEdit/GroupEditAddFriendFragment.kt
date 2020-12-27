package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_group_edit_add_friend.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.database.asDomainModel
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.databinding.FragmentGroupEditAddFriendBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GetGroupDetail
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.util.AdapterCheck
import work.calmato.prestopay.util.ViewModelFriend
import java.util.*

class GroupEditAddFriendFragment : Fragment() {
  private val viewModel: ViewModelFriend by lazy {
    ViewModelProvider(this).get(ViewModelFriend::class.java)
  }

  private var getGroupInfo: GetGroupDetail? = null
  private var recycleAdapter: AdapterCheck? = null
  private var responseGroup: GroupPropertyResponse? = null
  private lateinit var friendListArg: List<UserProperty>
  private lateinit var clickListener: AdapterCheck.OnClickListener
  private lateinit var id: String
  private lateinit var doneButton: MenuItem

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentGroupEditAddFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_edit_add_friend, container, false)
    clickListener = AdapterCheck.OnClickListener { viewModel.itemIsClicked(it) }
    recycleAdapter = AdapterCheck(clickListener)
    binding.root.findViewById<RecyclerView>(R.id.groupAddFriendRecycle).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    getGroupInfo = GroupEditAddFriendFragmentArgs.fromBundle(requireArguments()).groupEditList
    binding.lifecycleOwner = this
    id = MainActivity.firebaseId
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    var groupMembers: List<UserProperty> = mutableListOf()
    if (getGroupInfo != null) {
      groupMembers = getGroupInfo!!.users
    }
    val friends: LiveData<List<UserProperty>> =
      Transformations.map(getAppDatabase(requireContext()).friendDao.getFriends()) {
        it.asDomainModel()
      }
    friends.observe(viewLifecycleOwner, Observer { friendList ->
      friendListArg = friendList.filter {
        !groupMembers.contains(it)
      }
      recycleAdapter?.friendList = friendListArg
    })
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        it.checked = !it.checked
        viewModel.itemIsClickedCompleted()
      }
    })

    setHasOptionsMenu(true)
    searchFriend.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextChange(newText: String): Boolean {
        // text changed
        if (newText.isBlank()) {
          recycleAdapter?.friendList = friendListArg
        } else {
          recycleAdapter?.friendList = friendListArg.filter {
            !groupMembers.contains(it) && it.name.toLowerCase(Locale.ROOT).contains(newText)
          }
        }
        return false
      }

      override fun onQueryTextSubmit(query: String): Boolean {
        // submit button pressed
        return false
      }
    })
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackScreen()
        }
      }
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    doneButton = menu.getItem(0)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendRequest()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendRequest() {
    val friends: List<String> = friendListArg.filter { it.checked }.map { item -> item.id }
    if (friends.size + getGroupInfo!!.users.size <= 64) {
      doneButton.isEnabled = false
      progressBarGroupEditAddFriend.visibility = ProgressBar.VISIBLE
      frontViewgroupEditAddFriend.visibility = ImageView.VISIBLE
      Api.retrofitService.registerFriendToGroup(
        "Bearer $id",
        mapOf("userIds" to friends),
        getGroupInfo!!.id
      )
        .enqueue(object : Callback<GroupPropertyResponse> {
          override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
            Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
            doneButton.isEnabled = true
            progressBarGroupEditAddFriend.visibility = ProgressBar.GONE
            frontViewgroupEditAddFriend.visibility = ImageView.GONE
          }

          override fun onResponse(
            call: Call<GroupPropertyResponse>,
            response: Response<GroupPropertyResponse>
          ) {
            if (response.isSuccessful) {
              responseGroup = response.body()
            } else {
              Toast.makeText(
                activity,
                "グループに友達を追加できませんでした",
                Toast.LENGTH_SHORT
              ).show()
            }
            doneButton.isEnabled = true
            progressBarGroupEditAddFriend.visibility = ProgressBar.GONE
            frontViewgroupEditAddFriend.visibility = ImageView.GONE
            goBackScreen()
          }
        })
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.group_member_restriction),
        Toast.LENGTH_LONG
      ).show()
    }
  }

  private fun goBackScreen() {
    if (getGroupInfo != null) {
      if (responseGroup == null) {
        var idList: MutableList<String> = mutableListOf()
        for (i in 0..(getGroupInfo!!.users.size - 1)) {
          idList.add(getGroupInfo!!.users[i].id)
        }
        responseGroup = GroupPropertyResponse(
          getGroupInfo!!.id, getGroupInfo!!.name,
          getGroupInfo!!.thumbnailUrl, idList, getGroupInfo!!.createdAt, getGroupInfo!!.updatedAt
        )
      }
      this.findNavController().navigate(
        GroupEditAddFriendFragmentDirections.actionGroupEditAddFriendToGroupEditFragment(
          responseGroup
        )
      )
    } else {
      Toast.makeText(
        activity,
        resources.getString(R.string.bad_internet_connection),
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  companion object {
    internal const val TAG = "GroupEditAddFriend"
  }
}
