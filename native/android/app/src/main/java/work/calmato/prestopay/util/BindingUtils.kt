package work.calmato.prestopay.util

import android.media.Image
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import work.calmato.prestopay.network.GroupPropertyResponse
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

@BindingAdapter("thumbnailGroup")
fun ImageView.setThumbnail(item:GroupPropertyResponse?){
  item?.thumbnail_url?.let{
    if(item.thumbnail_url.isNotEmpty()) {
      Picasso.with(context).load(item.thumbnail_url).into(this)
    }
  }
}

@BindingAdapter("groupName")
fun TextView.setUserName(item:GroupPropertyResponse?){
  item?.let {
    text = item.name
  }
}


@BindingAdapter("tagName")
fun TextView.setTagName(item:Tag?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("tagImage")
fun ImageView.setTagImage(item:Tag?){
  item?.let {
    setImageResource(item.imageId)
  }
}

@BindingAdapter("tagCheckbox")
fun CheckBox.setTagCheckBox(item:Tag?){
  item?.let {
    isChecked = item.isSelected
  }
}
