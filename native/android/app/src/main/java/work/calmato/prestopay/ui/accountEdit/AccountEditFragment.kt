package work.calmato.prestopay.ui.accountEdit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account_edit.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAccountEditBindingImpl
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.EditAccountProperty
import work.calmato.prestopay.network.EditAccountResponse
import work.calmato.prestopay.util.Constant
import work.calmato.prestopay.util.PermissionBase
import work.calmato.prestopay.util.encodeImage2Base64
import java.lang.Exception

class AccountEditFragment : PermissionBase() {
  var idToken = ""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAccountEditBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_account_edit, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    //保存buttonを押した時の処理を記述
    savaButton.setOnClickListener {
      val thumbnails = encodeImage2Base64(thumbnailEdit)
      val name: String = nameEditText.text.toString()
      val userName: String = userNameEditText.text.toString()
      val email: String = mailEditText.text.toString()
      if (name != "" && userName != "" && email != "") {
        val accountProperty = EditAccountProperty(name, userName, email, thumbnails)
        Api.retrofitService.editAccount("Bearer $idToken", accountProperty).enqueue(object:Callback<EditAccountResponse>{
          override fun onFailure(call: Call<EditAccountResponse>, t: Throwable) {
            Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
          }
          override fun onResponse(
            call: Call<EditAccountResponse>,
            response: Response<EditAccountResponse>
          ) {
            if(response.isSuccessful){
              Toast.makeText(requireContext(), "変更しました", Toast.LENGTH_LONG).show()
              navigateToAccountHome()
            } else{
              try{
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
                Toast.makeText(activity, "アカウント情報の修正に失敗しました", Toast.LENGTH_LONG).show()
              }
            }
          }
        })
      } else {
        Toast.makeText(requireContext(), "入力を行ってください", Toast.LENGTH_SHORT).show()
      }
    }

    thumbnailEdit.setOnClickListener {
      requestPermission()
    }
    val mUser = FirebaseAuth.getInstance().currentUser
    mUser?.getIdToken(true)?.addOnCompleteListener(requireActivity()) {
      idToken = it.result?.token!!
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      thumbnailEdit.setImageURI(data?.data)
      thumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
      changeProfilePicture.text = "写真を変更"
    }
  }

  private fun navigateToAccountHome(){
    this.findNavController().navigate(
      AccountEditFragmentDirections.actionEditAccountFragmentToAccountHome()
    )
  }
  companion object {
    internal const val TAG = "AccountEditFragment"
  }
}

