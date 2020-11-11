package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
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

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
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
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    var groupMembers = getGroupInfo!!.users
    val friends: LiveData<List<UserProperty>> =
      Transformations.map(getAppDatabase(requireContext()).friendDao.getFriends()) {
        it.asDomainModel()
      }
    friends.observe(viewLifecycleOwner, Observer {
      friendListArg = it.filter {
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
        if (newText.isNullOrBlank()) {
          recycleAdapter?.friendList = friendListArg
        } else {
          recycleAdapter?.friendList = friendListArg.filter {
            !groupMembers.contains(it) && it.name.toLowerCase().contains(newText)
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
          sendRequest()
        }
      }
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendRequest()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendRequest() {
    var friendids: List<String> = friendListArg.filter { it.checked }.map { item -> item.id }
    Api.retrofitService.registerFriendToGroup(
      "Bearer $id",
      mapOf("userIds" to friendids),
      getGroupInfo!!.id
    )
      .enqueue(object : Callback<GroupPropertyResponse> {
        override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
        }

        override fun onResponse(
          call: Call<GroupPropertyResponse>,
          response: Response<GroupPropertyResponse>
        ) {
          if (response.isSuccessful) {
            responseGroup = response.body()
            applyBackHome(responseGroup)
          } else {
            Toast.makeText(
              activity,
              "グループに友達を追加できませんでした",
              Toast.LENGTH_SHORT
            )
          }
        }
      })
  }

  private fun applyBackHome(response: GroupPropertyResponse?) {
    this.findNavController().navigate(
      GroupEditAddFriendFragmentDirections.actionGroupEditAddFriendToGroupEditFragment(response)
    )
  }

  companion object {
    internal const val TAG = "GroupEditAddFeiend"
  }
}
