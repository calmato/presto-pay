package work.calmato.prestopay.ui.newAccount

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_new_account.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentNewAccountBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.NewAccountProperty
import work.calmato.prestopay.network.AccountResponse
import work.calmato.prestopay.util.Constant.Companion.IMAGE_PICK_CODE
import work.calmato.prestopay.util.Constant.Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import work.calmato.prestopay.util.PermissionBase
import work.calmato.prestopay.util.encodeImage2Base64
import java.lang.Exception


class NewAccountFragment : PermissionBase() {
  var setThumbnail = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentNewAccountBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_new_account, container, false
    )

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    passEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        var textLength = s?.length
        var textColor = Color.GRAY

        if (textLength != null) {
          if (textLength < 8) {
            textColor = Color.RED
          }
        }
        passwordInformation.setTextColor(textColor)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

    })

    passConfirmEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        var textColor = Color.GRAY

        if (!passConfirmEditText.text.toString().equals(passEditText.text.toString())) {
          textColor = Color.RED
        }
        passwordConfirmInformation.setTextColor(textColor)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

    })

    // buttonを押した時の処理を記述
    createAccountButton.setOnClickListener {
      var thumbnails = ""
      if (setThumbnail) {
        thumbnails = encodeImage2Base64(thumbnailEdit)
      }
      val name = fullNameEditText.text.toString()
      val userName = userNameEditText.text.toString()
      val email = emailEditText.text.toString()
      val password = passEditText.text.toString()
      val passwordConfirmation = passConfirmEditText.text.toString()

      if (name != "" && userName != "" && email != "" && password != "" && passwordConfirmation != "") {
        if (password == passwordConfirmation || password.length >= 8) {
          val accountProperty =
            NewAccountProperty(name, userName, email, thumbnails, password, passwordConfirmation)
          Api.retrofitService.createAccount(accountProperty)
            .enqueue(object : Callback<AccountResponse> {
              override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
              }
              override fun onResponse(
                call: Call<AccountResponse>,
                response: Response<AccountResponse>
              ) {
                if (response.isSuccessful) {
                  Toast.makeText(requireContext(), resources.getString(R.string.failed_to_change_account_information)+"\n"+resources.getString(R.string.please_login), Toast.LENGTH_LONG)
                    .show()
                  navigateToLogin()
                } else {
                  try {
                    val jObjError = JSONObject(response.errorBody()?.string()).getJSONArray("errors")
                    for (i in 0 until jObjError.length()) {
                      val errorMessage =
                        jObjError.getJSONObject(i).getString("field") + " " + jObjError.getJSONObject(
                          i
                        )
                          .getString("message")
                      Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                  }catch (e:Exception){
                    Toast.makeText(activity, resources.getString(R.string.failed_create_account), Toast.LENGTH_LONG).show()
                  }
                }
              }
            })
        } else {
          Toast.makeText(requireContext(), resources.getString(R.string.please_fill), Toast.LENGTH_SHORT).show()
        }
      } else {
        Toast.makeText(requireContext(), resources.getString(R.string.please_fill), Toast.LENGTH_SHORT).show()
      }
    }

    editPhotoText.setOnClickListener {
      requestPermission()
    }
    hasAccountText.setOnClickListener {
      this.findNavController().navigate(
        NewAccountFragmentDirections.actionNewAccountFragmentToLoginFragment()
      )
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
      thumbnailEdit.setImageURI(data?.data)
      thumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
      editPhotoText.text = resources.getString(R.string.change_image)
    }
  }

  private fun navigateToLogin() {
    this.findNavController().navigate(
      NewAccountFragmentDirections.actionNewAccountFragmentToLoginFragment()
    )
  }

  companion object {
    internal const val TAG = "NewAccountFragment"
  }
}
