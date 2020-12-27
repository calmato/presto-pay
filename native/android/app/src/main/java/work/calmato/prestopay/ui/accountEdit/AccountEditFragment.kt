package work.calmato.prestopay.ui.accountEdit

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_account_edit.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAccountEditBindingImpl
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.EditAccountProperty
import work.calmato.prestopay.network.EditAccountResponse
import work.calmato.prestopay.util.*

class AccountEditFragment : PermissionBase() {
  private lateinit var sharedPreferences: SharedPreferences

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentAccountEditBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_account_edit, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    thumbnailEdit.setOnClickListener {
      requestPermission()
    }
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    nameEditText.setText(sharedPreferences.getString("name", ""))
    userNameEditText.setText(sharedPreferences.getString("username", ""))
    mailEditText.setText(sharedPreferences.getString("email", ""))
    val thumbnailUrl = sharedPreferences.getString("thumbnailUrl", "")
    if (thumbnailUrl!!.isNotEmpty()) {
      Picasso.with(context).load(thumbnailUrl).into(thumbnailEdit)
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
      thumbnailEdit.setImageURI(resultUri)
      thumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
      changeProfilePicture.text = resources.getText(R.string.change_image)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendRequest(item)
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendRequest(item:MenuItem) {
    //保存buttonを押した時の処理を記述
    val thumbnails = encodeImage2Base64(thumbnailEdit)
    val name: String = nameEditText.text.toString()
    val userName: String = userNameEditText.text.toString()
    val email: String = mailEditText.text.toString()
    val id = MainActivity.firebaseId
    if (name != "" && userName != "" && email != "") {
      item.isEnabled = false
      progressBarAccountEdit.visibility = ProgressBar.VISIBLE
      frontViewAccountEdit.visibility = ImageView.VISIBLE
      val accountProperty = EditAccountProperty(name, userName, email, thumbnails)
      Api.retrofitService.editAccount("Bearer $id", accountProperty).enqueue(object :
        Callback<EditAccountResponse> {
        override fun onFailure(call: Call<EditAccountResponse>, t: Throwable) {
          item.isEnabled = true
          progressBarAccountEdit.visibility = ProgressBar.GONE
          frontViewAccountEdit.visibility = ImageView.GONE
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
        }

        override fun onResponse(
          call: Call<EditAccountResponse>,
          response: Response<EditAccountResponse>
        ) {
          if (response.isSuccessful) {
            val editor = sharedPreferences.edit()
            editor.putString("thumbnailUrl", response.body()?.thumbnailUrl)
            editor.putString("name", response.body()?.name)
            editor.putString("username", response.body()?.username)
            editor.putString("email", response.body()?.email)
            editor.apply()
            navigateToAccountHome()
          } else {
            try {
              val jObjError = JSONObject(response.errorBody()?.string()?:"Failed").getJSONArray("errors")
              for (i in 0 until jObjError.length()) {
                val errorMessage =
                  jObjError.getJSONObject(i).getString("field") + " " + jObjError.getJSONObject(
                    i
                  )
                    .getString("message")
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
              }
            } catch (e: Exception) {
              Toast.makeText(
                activity,
                resources.getString(R.string.failed_to_change_account_information),
                Toast.LENGTH_LONG
              ).show()
            }
            item.isEnabled = true
            progressBarAccountEdit.visibility = ProgressBar.GONE
            frontViewAccountEdit.visibility = ImageView.GONE
          }
        }
      })
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.fill_all_required_forms),
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  private fun navigateToAccountHome() {
    this.findNavController().navigate(
      AccountEditFragmentDirections.actionEditAccountFragmentToAccountHome()
    )
  }

  companion object {
    internal const val TAG = "AccountEditFragment"
  }
}

