package work.calmato.prestopay.ui.createGroup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_create_group.*
import okhttp3.Response
import org.json.JSONObject
import work.calmato.prestopay.R
import work.calmato.prestopay.util.Constant.Companion.IMAGE_PICK_CODE
import work.calmato.prestopay.util.Constant.Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import work.calmato.prestopay.util.RestClient
import work.calmato.prestopay.util.encodeImage2Base64

class CreateGroupFragment : Fragment() {
  val serverUrl: String = "https://api.presto-pay-stg.calmato.work/v1/groups"
  var jsonText: String = ""

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_create_group, container, false)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendGroupInfo()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendGroupInfo() {
    val thumbnail = encodeImage2Base64(thumbnailEdit)
    val groupName = groupName.text.toString()
    val gson = Gson()
    val map: MutableMap<String, Any> = mutableMapOf()
    map.put("name", groupName)
    map.put("thumbnail", thumbnail)
    map.put("userIds", listOf("1924e4ce-cbfd-4420-a902-ba83653f7d4e"))
    //TODO ユーザー検索をできるように
    jsonText = gson.toJson(map)
    Log.i(TAG, "sendGroupInfo: " + jsonText)
    val response = MyAsyncTask().execute().get()
    Log.i(TAG, "MyAsyncTask: " + response.isSuccessful)
    if (response.isSuccessful) {
      Toast.makeText(requireContext(), "新しいグループを作成しました", Toast.LENGTH_SHORT).show()
    } else {
      var errorMessage = ""
      Log.i(TAG, "responseBody: " + response.body()!!.string())
      val jsonArray = JSONObject(response.body()!!.string()).getJSONArray("errors")
      for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        errorMessage += jsonObject.getString("field") + " " + jsonObject.getString("message")
        if (i != jsonArray.length() - 1) {
          errorMessage += "\n"
        }
      }
      Toast.makeText(requireActivity(), "sippai", Toast.LENGTH_LONG).show()
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    thumbnailEdit.setOnClickListener {
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
    setHasOptionsMenu(true)

  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.header_done, menu)

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
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
      thumbnailEdit.setImageURI(data?.data)
      thumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
    }
  }

  inner class MyAsyncTask : AsyncTask<Void, Void, Response>() {
    override fun doInBackground(vararg params: Void?): Response {
      val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
      val token = sharedPreferences.getString("token", null)
      val responseCode = RestClient().postAuthExecute(jsonText, serverUrl, token)
      return responseCode
    }
  }

  companion object {
    internal const val TAG = "CreateGroupFragment"
  }
}
