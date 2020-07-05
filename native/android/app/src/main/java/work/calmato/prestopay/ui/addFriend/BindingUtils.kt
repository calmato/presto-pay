package work.calmato.prestopay.ui.addFriend

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.util.decodeImageFromBase64

//@BindingAdapter("thumbnail")
//fun ImageView.setThumbnail(item:UserProperty?){
//  item?.let{
//    setImageBitmap(decodeImageFromBase64(item.thumbnail!!))
//  }
//}

@BindingAdapter("username")
fun TextView.setUserName(item:UserProperty?){
  item?.let {
    text = item.name
  }
}
