package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
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
import work.calmato.prestopay.util.encodeImage2Base64
import java.io.IOException

class GroupEditAddUnauthorizedFragment : Fragment() {
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
  }

  private fun sendRequest() {
    val thumbnail = encodeImage2Base64(unauthorizedThumbnail)
    val accountName: String = unauthorizedAccountName.text.toString()
    if (accountName != "") {
      try {
        val unauthorizedProperty = RegisterUnauthorizedProperty(thumbnail, accountName)
        execute(unauthorizedProperty, getGroupInfo!!.id)

      } catch (e: IOException) {
        Log.d(TAG, "debug $e")
      }
    } else {
      Toast.makeText(requireContext(), resources.getString(R.string.please_fill), Toast.LENGTH_SHORT).show()
    }
  }

  private fun execute(registerUnauthorized: RegisterUnauthorizedProperty, groupId: String) {
    //doneButton.isEnabled = false
    val unauthorizedList: List<RegisterUnauthorizedProperty> = listOf(registerUnauthorized)
    frontView.visibility = ImageView.VISIBLE
    progressBar.visibility = android.widget.ProgressBar.VISIBLE
    Api.retrofitService.registerUnauthorizedUsers(
      "Bearer $id",
      mapOf("userIds" to unauthorizedList),
      getGroupInfo!!.id
    )
      .enqueue(object : Callback<GroupPropertyResponse> {
        override fun onFailure(call: Call<GroupPropertyResponse>, t: Throwable) {
          //doneButton.isEnabled = true
          frontView.visibility = ImageView.GONE
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
  }

  companion object {
    internal const val TAG = "EditAddUnauthorized"
  }
}
