package work.calmato.prestopay.util

import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import work.calmato.prestopay.network.UserProperty

@BindingAdapter("thumbnail")
fun ImageView.setThumbnail(item:UserProperty?){
  item?.thumbnailUrl?.let{
    if(item.thumbnailUrl.isNotEmpty()) {
      Picasso.with(context).load(item.thumbnailUrl).into(this)
    }
  }
}

@BindingAdapter("username")
fun TextView.setUserName(item:UserProperty?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("checkbox")
fun CheckBox.isChecked(item:UserProperty?){
  item?.let{
    isChecked = item.checked
  }
}
