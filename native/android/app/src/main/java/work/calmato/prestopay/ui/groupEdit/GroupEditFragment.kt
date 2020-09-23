package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_group_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GetGroupDetail
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.util.AdapterFriendPlane
import work.calmato.prestopay.util.ViewModelFriend
import work.calmato.prestopay.util.ViewModelGroup
import kotlin.concurrent.thread

class GroupEditFragment : Fragment() {
  private val viewModel: ViewModelFriend by lazy {
    ViewModelProvider(this).get(ViewModelFriend::class.java)
  }

  private var getGroupInfo: GroupPropertyResponse? = null
  private var groupDetail: GetGroupDetail? = null
  private var groupMembers: MutableList<UserProperty> = mutableListOf()
  private lateinit var id: String

  private var recycleAdapter: AdapterFriendPlane? = null
  private lateinit var friendListArg: List<UserProperty>

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    thread {
      try {
        Api.retrofitService.getGroupDetail("Bearer $id", getGroupInfo!!.id)
          .enqueue(object : Callback<GetGroupDetail> {
            override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
              Log.d(ViewModelGroup.TAG, t.message)
            }

            override fun onResponse(
              call: Call<GetGroupDetail>,
              response: Response<GetGroupDetail>
            ) {
              Log.d(ViewModelGroup.TAG, response.body().toString())
              groupDetail = response.body()
              groupEditName.setText(groupDetail?.name)
              groupMembers.addAll(groupDetail!!.users)
              viewModel.friendsList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
                recycleAdapter?.friendList = groupMembers
              })
            }
          })
      } catch (e: Exception) {
        Log.d(TAG, "debug $e")
      }
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!

  }


  private lateinit var clickListener: AdapterFriendPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupEditBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_edit, container, false)
    getGroupInfo = GroupEditFragmentArgs.fromBundle(requireArguments()).groupEditList

    clickListener = AdapterFriendPlane.OnClickListener { viewModel.itemIsClicked(it) }
    recycleAdapter = AdapterFriendPlane(clickListener)

    binding.root.findViewById<RecyclerView>(R.id.friendsRecycleView).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    return binding.root
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

  }


  companion object {
    const val TAG = "GroupEditFragment"
  }
}
