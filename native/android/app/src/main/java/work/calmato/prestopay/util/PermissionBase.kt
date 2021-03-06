package work.calmato.prestopay.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yalantis.ucrop.UCrop
import work.calmato.prestopay.R
import java.io.File

open class PermissionBase :Fragment() {
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          pickImageFromGallery()
        } else {
          Toast.makeText(requireActivity(), resources.getString(R.string.access_denied), Toast.LENGTH_LONG).show()
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
      Constant.IMAGE_PICK_CODE
    )
  }
  fun requestPermission(){
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
        Toast.makeText(requireActivity(), resources.getString(R.string.change_gallery_setting), Toast.LENGTH_LONG).show()
      } else {
        // No explanation needed, we can request the permission.
        requestPermissions(
          arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
          Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
        )
      }
    } else {
      //permission already granted
      pickImageFromGallery()
    }
  }

  fun cropImage(uri: Uri) {
    val destination = Uri.fromFile(
      File(requireActivity().cacheDir, "IMG_" + System.currentTimeMillis())
    )
    UCrop.of(uri, destination)
      .withAspectRatio(1f, 1f)
      .start(requireActivity(), this)
  }
}
