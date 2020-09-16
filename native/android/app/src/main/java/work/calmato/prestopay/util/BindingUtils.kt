package work.calmato.prestopay.util

import android.media.Image
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.network.UserProperty
import java.text.SimpleDateFormat

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

@BindingAdapter("paymentName")
fun TextView.setPaymentName(item:PaymentPropertyGet?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("paymentAmount")
fun TextView.setPaymentAmount(item:String){
  item?.let {
    text = item
  }
}

@BindingAdapter("paymentWhoPaid")
fun TextView.setWhoPaid(item:String?){
  item?.let {
    text = item
  }
}

//@BindingAdapter("paymentTag")
//fun ImageView.setTag(item:PaymentPropertyGet?){
//  item?.let{
//    setImageResource()
//  }
//}

@BindingAdapter("paymentDate")
fun TextView.setDate(item:PaymentPropertyGet?){
  item?.let{
    text = item.paidAt.split("T")[0]
  }
}
