package work.calmato.prestopay.ui.groupEdit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_group_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditBinding
import work.calmato.prestopay.network.*
import work.calmato.prestopay.util.*
import java.io.IOException
import kotlin.concurrent.thread

class GroupEditFragment : PermissionBase() {
  private val viewModel: ViewModelFriend by lazy {
    ViewModelProvider(this).get(ViewModelFriend::class.java)
  }

  private var getGroupInfo: GroupPropertyResponse? = null
  private var groupDetail: GetGroupDetail? = null
  private var groupMembers: MutableList<UserProperty> = mutableListOf()
  private lateinit var id: String
  private var recycleAdapter: AdapterFriendPlane? = null
  private lateinit var doneButton: MenuItem

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    thread {
      try {
        progressBarGroupEdit.visibility = ProgressBar.VISIBLE
        frontViewGroupEdit.visibility = ImageView.VISIBLE
        Api.retrofitService.getGroupDetail("Bearer $id", getGroupInfo!!.id)
          .enqueue(object : Callback<GetGroupDetail> {
            override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
              Log.d(ViewModelGroup.TAG, t.message ?: "No message")
            }

            override fun onResponse(
              call: Call<GetGroupDetail>,
              response: Response<GetGroupDetail>
            ) {
              if (response.isSuccessful) {
                Log.d(ViewModelGroup.TAG, response.body().toString())
                groupDetail = response.body()
                if (groupDetail?.thumbnailUrl!!.isNotEmpty()) {
                  Picasso.with(context).load(groupDetail!!.thumbnailUrl).into(groupThumnail)
                }
                groupEditName.setText(groupDetail?.name)
                groupMembers.addAll(groupDetail!!.users)
                viewModel.friendsList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
                  recycleAdapter?.friendList = groupMembers
                })
              } else {
                Toast.makeText(
                  activity,
                  resources.getString(R.string.bad_internet_connection),
                  Toast.LENGTH_SHORT
                ).show()
              }
              progressBarGroupEdit.visibility = ProgressBar.INVISIBLE
              frontViewGroupEdit.visibility = ImageView.INVISIBLE
            }
          })
      } catch (e: Exception) {
        Log.d(TAG, "debug $e")
      }
    }
    id = MainActivity.firebaseId

  }

  private lateinit var clickListener: AdapterFriendPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
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
    if(!isOnline(requireContext())){
      Toast.makeText(requireContext(),resources.getString(R.string.bad_internet_connection),Toast.LENGTH_LONG).show()
      goBackScreen()
    }
    groupEditAddFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupEditFragmentDirections.actionGroupEditFragmentToGroupEditAddFriend(groupDetail)
      )
    }

    addUnauthorizedUsers.setOnClickListener {
      this.findNavController().navigate(
        GroupEditFragmentDirections.actionGroupEditFragmentToGroupEditAddUnauthorizedFragment(
          groupDetail
        )
      )
    }

    groupThumnail.setOnClickListener {
      requestPermission()
    }

    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackScreen()
        }
      }
    )
    if (getGroupInfo!!.isHidden) {
      hiddenSwitch.isChecked = true
    }
    setHasOptionsMenu(true)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      cropImage(data?.data!!)
    }
    if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
      val resultUri = UCrop.getOutput(data!!)
      groupThumnail.setImageURI(resultUri)
      groupThumnail.setBackgroundColor(Color.TRANSPARENT)
      changeProfilePicture3.text = resources.getText(R.string.change_image)
    }
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
    val thumbnail = encodeImage2Base64(groupThumnail)
    val groupName: String = groupEditName.text.toString()
    try {
      val editGroup = EditGroup(groupName, thumbnail, groupDetail!!.users.map { it.id })
      execute(editGroup, getGroupInfo!!.id)
    } catch (e: IOException) {
      Log.d(TAG, "debug $e")
    }
  }

  private fun execute(editGroupProperty: EditGroup, groupId: String) {
    doneButton.isEnabled = false
    progressBarGroupEdit.visibility = ProgressBar.VISIBLE
    frontViewGroupEdit.visibility = ImageView.VISIBLE
    Api.retrofitService.editGroup("Bearer $id", editGroupProperty, groupId)
      .enqueue(object : Callback<GroupPropertyResponse> {
        override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
          viewChange()
        }

        override fun onResponse(
          call: Call<GroupPropertyResponse>,
          response: Response<GroupPropertyResponse>
        ) {
          if (!response.isSuccessful) {
            Toast.makeText(
              activity,
              "グループ情報を変更できませんでした",
              Toast.LENGTH_SHORT
            ).show()
            viewChange()
          } else {
            hiddenCheck(groupId)
          }
        }
      })

  }

  private fun goBackScreen() {
    this.findNavController().navigate(
      GroupEditFragmentDirections.actionGroupEditFragmentToGroupDetail(getGroupInfo)
    )
  }

  private fun goBackHome() {
    this.findNavController().navigate(
      GroupEditFragmentDirections.actionGroupEditFragmentToHomeFragment()
    )
  }

  private fun hiddenCheck(groupId: String) {
    val mSwitch: Switch = hiddenSwitch
    Log.d(TAG, mSwitch.toString())
    if (mSwitch.isChecked && !getGroupInfo!!.isHidden) {
      Api.retrofitService.addHiddenGroup("Bearer $id", groupId)
        .enqueue(object : Callback<HiddenGroups> {
          override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
            Toast.makeText(activity, "グループ情報を変更できませんでした", Toast.LENGTH_LONG).show()
            Log.d(TAG, t.message ?: "No message")
            viewChange()
          }

          override fun onResponse(call: Call<HiddenGroups>, response: Response<HiddenGroups>) {
            if (!response.isSuccessful) {
              Toast.makeText(
                activity,
                "グループ情報を変更できませんでした",
                Toast.LENGTH_SHORT
              ).show()
              viewChange()
            } else {
              viewChange()
              goBackScreen()
            }
          }
        })
    } else if (!mSwitch.isChecked && getGroupInfo!!.isHidden) {
      Api.retrofitService.deleteHiddenGroup("Bearer $id", groupId)
        .enqueue(object : Callback<HiddenGroups> {
          override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
            Toast.makeText(activity,"グループ情報を変更できませんでした", Toast.LENGTH_LONG).show()
            Log.d(TAG, t.message ?: "No message")
            viewChange()
          }

          override fun onResponse(call: Call<HiddenGroups>, response: Response<HiddenGroups>) {
            if (!response.isSuccessful) {
              Toast.makeText(
                activity,
                "グループ情報を変更できませんでした",
                Toast.LENGTH_SHORT
              ).show()
              viewChange()
            } else {
              viewChange()
              goBackHome()
            }
          }
        })
    } else {
      viewChange()
      goBackHome()
    }
  }

  private fun viewChange() {
    progressBarGroupEdit.visibility = ProgressBar.INVISIBLE
    frontViewGroupEdit.visibility = ImageView.INVISIBLE
    doneButton.isEnabled = true
  }

  companion object {
    const val TAG = "GroupEditFragment"
  }
}
