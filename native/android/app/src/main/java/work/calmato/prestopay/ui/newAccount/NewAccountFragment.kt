package work.calmato.prestopay.ui.newAccount

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_new_account.*
import okhttp3.Response
import org.json.JSONObject
import org.xml.sax.Parser
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentNewAccountBinding
import work.calmato.prestopay.ui.login.LoginFragmentDirections
import work.calmato.prestopay.util.RestClient
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import kotlin.math.roundToInt

class NewAccountFragment : Fragment() {
  val serverUrl: String = "https://api.presto-pay-stg.calmato.work/v1/users"
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

//
//
//  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // buttonを押した時の処理を記述
    createAccountButton.setOnClickListener {
      var thumbnails = ""
      if (setThumbnail) {
        thumbnails = encodeImage2Base64()
      }
      val name = fullNameEditText.text.toString()
      val userName = userNameEditText.text.toString()
      val email = emailEditText.text.toString()
      val password = passEditText.text.toString()
      val passwordConfirmation = passConfirmEditText.text.toString()

      if (password == passwordConfirmation) {
        var map: MutableMap<String, Any> = mutableMapOf()
        map.put("name", name)
        map.put("username", userName)
        map.put("email", email)
        map.put("thumbnail", thumbnails)
        map.put("password", password)
        map.put("passwordConfirmation", passwordConfirmation)

        val gson = Gson()
        jsonText = gson.toJson(map)
        Log.d("New Account Post Json", jsonText)

        val response = MyAsyncTask().execute().get()
        if(response.isSuccessful){
          this.findNavController().navigate(
            NewAccountFragmentDirections.actionNewAccountFragmentToMailCheckFragment()
          )
        } else{
          var errorMessage = ""
          val jsonArray = JSONObject(response.body()!!.string()).getJSONArray("errors")
          for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)
            errorMessage += jsonObject.getString("field") + " " + jsonObject.getString("message")
            if (i != jsonArray.length() - 1){
              errorMessage += "\n"
            }
          }
          Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG).show()

        }
      } else {
        println("The password and the confirmation password did not match.")
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
  }

  inner class MyAsyncTask : AsyncTask<Void, Void, Response>() {
    override fun doInBackground(vararg params: Void?): Response {
      val responseCode = RestClient().postExecute(jsonText, serverUrl)
      return responseCode
    }
  }


  companion object {
    private val IMAGE_PICK_CODE = 1000
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001
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
      thumbnail.setImageURI(data?.data)
      thumbnail.setBackgroundColor(Color.TRANSPARENT)
      editPhotoText.setText("写真を変更")
    }
  }

  private fun encodeImage2Base64(): String {
    val bitmap: Bitmap = thumbnail.drawable.toBitmap()
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageBytes: ByteArray = baos.toByteArray()
    return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    //decode base64 string to image
    //    val imageBytesDecode = Base64.decode(imageString, Base64.DEFAULT)
    //    val decodedImage = BitmapFactory.decodeByteArray(imageBytesDecode, 0, imageBytesDecode.size)
    //    testImageView.setImageBitmap(decodedImage)
  }
}
