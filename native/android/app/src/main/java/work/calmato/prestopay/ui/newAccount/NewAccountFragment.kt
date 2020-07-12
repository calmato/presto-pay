package work.calmato.prestopay.ui.newAccount

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_new_account.*
import okhttp3.Response
import org.json.JSONObject
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentNewAccountBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.NewAccountProperty
import work.calmato.prestopay.util.Constant.Companion.IMAGE_PICK_CODE
import work.calmato.prestopay.util.Constant.Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import work.calmato.prestopay.util.RestClient
import work.calmato.prestopay.util.encodeImage2Base64
import java.lang.Exception

class NewAccountFragment : Fragment() {
  val serverUrl: String = "https://api.presto-pay-stg.calmato.work/v1/auth"
  var jsonText: String = ""
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
//          val map: MutableMap<String, Any> = mutableMapOf()
//          map.put("name", name)
//          map.put("username", userName)
//          map.put("email", email)
//          map.put("thumbnail", thumbnails)
//          map.put("password", password)
//          map.put("passwordConfirmation", passwordConfirmation)
//
//          val gson = Gson()
//          jsonText = gson.toJson(map)
//          Log.d("New Account Post Json", jsonText)

//          val response = MyAsyncTask().execute().get()
          val accountProperty = NewAccountProperty(name,userName,email,thumbnails,password,passwordConfirmation)
          var resultBool = false
          val newAccountRequest = Api.retrofitService.createAccount(accountProperty)
          val thread = Thread(Runnable {
//            try {
                val result = newAccountRequest.execute()
              resultBool = result.isSuccessful
//            }catch (e:Exception){
//              Log.i(TAG, "onViewCreated: ")
//            }
          })
          thread.start()
          thread.join()
          if (resultBool) {
            Toast.makeText(requireContext(),"アカウントを作成しました\nログインしてください",Toast.LENGTH_LONG).show()
            this.findNavController().navigate(
              NewAccountFragmentDirections.actionNewAccountFragmentToLoginFragment()
            )
          } else {
//            var errorMessage = ""
//            val jsonArray = JSONObject(response.body()!!.string()).getJSONArray("errors")
//            for (i in 0 until jsonArray.length()) {
//              val jsonObject = jsonArray.getJSONObject(i)
//              errorMessage += jsonObject.getString("field") + " " + jsonObject.getString("message")
//              if (i != jsonArray.length() - 1) {
//                errorMessage += "\n"
//              }
//            }
//            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG).show()
          }
        } else {
          Toast.makeText(requireContext(), "入力を確認してください", Toast.LENGTH_SHORT).show()
        }
      } else {
        Toast.makeText(requireContext(), "入力を行ってください", Toast.LENGTH_SHORT).show()
      }
    }

    editPhotoText.setOnClickListener {
      //check runtime permission
      if (ContextCompat.checkSelfPermission(
          requireActivity(),
          Manifest.permission.READ_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED
      ) {
        // Permission is not granted
        if (ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
          )
        ) {
          // Show an explanation to the user *asynchronously* -- don't block
          // this thread waiting for the user's response! After the user
          // sees the explanation, try again to request the permission.
          Toast.makeText(requireActivity(), "設定からギャラリーへのアクセスを許可してください", Toast.LENGTH_LONG).show()
        } else {
          // No explanation needed, we can request the permission.
          ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
          )
        }
      } else {
        //permission already granted
        pickImageFromGallery()
      }
    }
    hasAccountText.setOnClickListener {
      this.findNavController().navigate(
        NewAccountFragmentDirections.actionNewAccountFragmentToLoginFragment()
      )
    }
  }

  inner class MyAsyncTask : AsyncTask<Void, Void, Response>() {
    override fun doInBackground(vararg params: Void?): Response {
      val responseCode = RestClient().postExecute(jsonText, serverUrl)
      return responseCode
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          pickImageFromGallery()
        } else {
          Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()
        }
        return
      }
      else -> {
        // Ignore all other requests.
      }
    }
  }

  private fun pickImageFromGallery() {
    //Intent to pick image
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(
      intent,
      IMAGE_PICK_CODE
    )
    setThumbnail = true
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
      thumbnailEdit.setImageURI(data?.data)
      thumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
      editPhotoText.setText("写真を変更")
    }
  }
  companion object {
    internal const val TAG = "NewAccountFragment"
  }
}
