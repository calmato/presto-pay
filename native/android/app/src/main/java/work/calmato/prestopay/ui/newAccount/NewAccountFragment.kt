package work.calmato.prestopay.ui.newAccount

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_new_account.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentNewAccountBinding
import work.calmato.prestopay.network.AccountResponse
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.NewAccountProperty
import work.calmato.prestopay.util.Constant.Companion.IMAGE_PICK_CODE
import work.calmato.prestopay.util.PermissionBase
import work.calmato.prestopay.util.encodeImage2Base64


class NewAccountFragment : PermissionBase() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentNewAccountBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_new_account, container, false
    )

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    passEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        val textLength = s?.length
        var textColor = Color.GRAY

        if (textLength != null) {
          if (textLength < 6) {
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

        if (passConfirmEditText.text.toString() != passEditText.text.toString()) {
          textColor = Color.RED
        }
        passwordConfirmInformation.setTextColor(textColor)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

    })

    accountThumbnailEdit.setOnClickListener {
      requestPermission()
    }

    // buttonを押した時の処理を記述
    createAccountButton.setOnClickListener {
      val thumbnails = encodeImage2Base64(accountThumbnailEdit)
      val name = fullNameEditText.text.toString()
      val userName = userNameEditText.text.toString()
      val email = emailEditText.text.toString()
      val password = passEditText.text.toString()
      val passwordConfirmation = passConfirmEditText.text.toString()

      if (name != "" && userName != "" && email != "" && password != "" && passwordConfirmation != "") {
        if (password == passwordConfirmation || password.length >= 8) {
          progressBarNewAccount.visibility = ProgressBar.VISIBLE
          frontViewNewAccount.visibility = ImageView.VISIBLE
          val accountProperty =
            NewAccountProperty(name, userName, email, thumbnails, password, passwordConfirmation)
          Api.retrofitService.createAccount(accountProperty)
            .enqueue(object : Callback<AccountResponse> {
              override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                progressBarNewAccount.visibility = ProgressBar.GONE
                frontViewNewAccount.visibility = ImageView.GONE
                Toast.makeText(activity, resources.getString(R.string.failed_create_account), Toast.LENGTH_LONG).show()
              }

              override fun onResponse(
                call: Call<AccountResponse>,
                response: Response<AccountResponse>
              ) {
                if (response.isSuccessful) {
                  Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.successed_to_create_account) + "\n" + resources.getString(
                      R.string.please_login
                    ),
                    Toast.LENGTH_LONG
                  )
                    .show()
                  navigateToLogin()
                } else {
                  Toast.makeText(
                    activity,
                    resources.getString(R.string.failed_create_account),
                    Toast.LENGTH_LONG
                  ).show()
                  progressBarNewAccount.visibility = ProgressBar.GONE
                  frontViewNewAccount.visibility = ImageView.GONE
                }
              }
            })
        } else {
          Toast.makeText(
            requireContext(),
            resources.getString(R.string.please_fill),
            Toast.LENGTH_SHORT
          ).show()
        }
      } else {
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.please_fill),
          Toast.LENGTH_SHORT
        ).show()
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
      cropImage(data?.data!!)
    }
    if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
      val resultUri = UCrop.getOutput(data!!)
      accountThumbnailEdit.setImageURI(resultUri)
      accountThumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
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
