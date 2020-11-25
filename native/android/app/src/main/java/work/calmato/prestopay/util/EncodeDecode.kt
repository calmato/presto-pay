package work.calmato.prestopay.util

import android.graphics.Bitmap
import android.util.Base64
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

//  ImageViewをBase64に変換
fun encodeImage2Base64(thumbnail: ImageView): String {
  val bitmap: Bitmap = thumbnail.drawable.toBitmap()
  val baos = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
  val imageBytes: ByteArray = baos.toByteArray()
  return Base64.encodeToString(imageBytes, Base64.DEFAULT)
}

