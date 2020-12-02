package work.calmato.prestopay.ui.groupEdit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_group_edit_add_unauthorized.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditAddUnauthorizedBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GetGroupDetail
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.RegisterUnauthorizedProperty
import work.calmato.prestopay.util.Constant
import work.calmato.prestopay.util.PermissionBase
import work.calmato.prestopay.util.encodeImage2Base64
import java.io.IOException

class GroupEditAddUnauthorizedFragment : PermissionBase() {
  private lateinit var id: String
  private var getGroupInfo: GetGroupDetail? = null
  private var responseGroup: GroupPropertyResponse? = null
  private lateinit var doneButton: MenuItem

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupEditAddUnauthorizedBinding =
      DataBindingUtil.inflate(
        inflater,
        R.layout.fragment_group_edit_add_unauthorized,
        container,
        false
      )
    binding.lifecycleOwner = this
    getGroupInfo = GroupEditAddFriendFragmentArgs.fromBundle(requireArguments()).groupEditList
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setHasOptionsMenu(true)

    frontView.visibility = ImageView.GONE
    progressBar.visibility = android.widget.ProgressBar.INVISIBLE

    unauthorizedAddButton.setOnClickListener {
      sendRequest()
    }

    unauthorizedThumbnail.setOnClickListener {
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
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    doneButton = menu.getItem(0)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val backParameter = RegisterUnauthorizedProperty("", "")
    when (item.itemId) {
      R.id.done -> goBackScreen()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      cropImage(data?.data!!)
    }
    if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
      val resultUri = UCrop.getOutput(data!!)
      unauthorizedThumbnail.setImageURI(resultUri)
      unauthorizedThumbnail.setBackgroundColor(Color.TRANSPARENT)
      changeProfileUnauthorized.text = resources.getText(R.string.change_image)
    }
  }

  private fun sendRequest() {
    val name: String = unauthorizedAccountName.text.toString()
    val thumbnail = encodeImage2Base64(unauthorizedThumbnail)
    if (name != "") {
      try {
        val unauthorizedProperty = RegisterUnauthorizedProperty(name, thumbnail)
        execute(unauthorizedProperty)

      } catch (e: IOException) {
        Log.d(TAG, "debug $e")
      }
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.please_fill),
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  private fun execute(registerUnauthorized: RegisterUnauthorizedProperty) {
    var limitSize: Int
    val unauthorizedList: List<RegisterUnauthorizedProperty> = listOf(registerUnauthorized)
    frontView.visibility = ImageView.VISIBLE
    progressBar.visibility = android.widget.ProgressBar.VISIBLE
    if (responseGroup == null) {
      limitSize = getGroupInfo!!.users.size
    } else {
      limitSize = responseGroup!!.userIds.size
    }
    if (limitSize <= 64) {
      Api.retrofitService.registerUnauthorizedUsers(
        "Bearer $id",
        mapOf("users" to unauthorizedList),
        getGroupInfo!!.id
      )
        .enqueue(object : Callback<GroupPropertyResponse> {
          override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
            progressBar.visibility = android.widget.ProgressBar.INVISIBLE
          }

          override fun onResponse(
            call: Call<GroupPropertyResponse>,
            response: Response<GroupPropertyResponse>
          ) {
            if (response.isSuccessful) {
              responseGroup = response.body()
              Toast.makeText(
                activity,
                "追加しました",
                Toast.LENGTH_SHORT
              ).show()
            } else {
              Toast.makeText(
                activity,
                "グループに友達を追加できませんでした",
                Toast.LENGTH_SHORT
              ).show()
            }
            frontView.visibility = ImageView.GONE
            progressBar.visibility = android.widget.ProgressBar.INVISIBLE
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
      GroupEditAddUnauthorizedFragmentDirections.actionGroupEditAddUnauthorizedFragmentToGroupEditFragment(
        responseGroup
      )
    )
  }

  companion object {
    internal const val TAG = "EditAddUnauthorized"
  }
}
