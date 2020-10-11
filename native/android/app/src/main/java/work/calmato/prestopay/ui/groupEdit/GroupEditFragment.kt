package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_friend_list.*
import kotlinx.android.synthetic.main.fragment_group_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditBinding
import work.calmato.prestopay.network.*
import work.calmato.prestopay.util.AdapterFriendPlane
import work.calmato.prestopay.util.ViewModelFriend
import work.calmato.prestopay.util.ViewModelGroup
import work.calmato.prestopay.util.encodeImage2Base64
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
    viewModel.refreshingFriend.observe(viewLifecycleOwner, Observer<Boolean> {
      it?.apply {
        swipeContainer.isRefreshing = it
      }
    })

    groupEditAddFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupEditFragmentDirections.actionGroupEditFragmentToGroupEditAddFriend(groupDetail)
      )
    }

    // 戻るbuttonを押した時の処理
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackHome()
        }
      }
    )

    // TODO: Group上での友達の削除として対応できていないため実装する場合ばここに記述する
/*    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setView(R.layout.dialog_add_friend)
          ?.setPositiveButton(
            resources.getString(R.string.delete_friend),
            DialogInterface.OnClickListener { _, _ ->
              val builder2: AlertDialog.Builder? = requireActivity().let {
                AlertDialog.Builder(it)
              }
              builder2?.setMessage(resources.getString(R.string.delete_question))
                ?.setPositiveButton(
                  resources.getString(R.string.delete),
                  DialogInterface.OnClickListener { _, _ ->
                    viewModel.deleteFriend(it.id, requireActivity())
                  })
                ?.setNegativeButton(resources.getString(R.string.cancel), null)
              val dialog2: AlertDialog? = builder2?.create()
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
    })*/
    setHasOptionsMenu(true)
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
    val thumbnails = encodeImage2Base64(groupThumnail)
    val groupName: String = groupEditName.text.toString()
    try {
      val editGroup = EditGroup(groupName, thumbnails, groupDetail!!.users.map { it.id })
      execute(editGroup, getGroupInfo!!.id)
    } catch (e: Exception) {
      Log.d(TAG, "debug $e")
    }
  }

  private fun execute(editGroupProperty: EditGroup, groupId: String) {
    Api.retrofitService.editGroup("Bearer $id", editGroupProperty, groupId)
      .enqueue(object : Callback<EditGroup> {
        override fun onFailure(call: Call<EditGroup>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
        }

        override fun onResponse(
          call: Call<EditGroup>,
          response: Response<EditGroup>
        ) {
          if (response.isSuccessful) {
            Toast.makeText(
              activity,
              "グループ情報を変更しました",
              Toast.LENGTH_SHORT
            ).show()
          } else {
            Toast.makeText(
              activity,
              "グループ情報を変更できませんでした",
              Toast.LENGTH_SHORT
            )
          }
        }
      })
    this.findNavController().navigate(
      GroupEditFragmentDirections.actionGroupEditFragmentToHomeFragment()
    )
  }

  private fun goBackHome() {
    this.findNavController().navigate(
      GroupEditFragmentDirections.actionGroupEditFragmentToGroupDetail(getGroupInfo)
    )
  }

  companion object {
    const val TAG = "GroupEditFragment"
  }
}
