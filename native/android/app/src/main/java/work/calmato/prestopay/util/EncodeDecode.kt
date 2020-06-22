package work.calmato.prestopay.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

//  Base64をBitmapに変換
fun decodeImageFromBase64(imageString: String): Bitmap {
  val imageBytesDecode = Base64.decode(imageString, Base64.DEFAULT)
  return BitmapFactory.decodeByteArray(imageBytesDecode, 0, imageBytesDecode.size)
}

