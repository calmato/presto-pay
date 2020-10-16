package work.calmato.prestopay.util

import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_add_payment_check.view.*
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.TagRepository
import kotlin.concurrent.thread
import kotlin.math.roundToInt

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
  item?.thumbnailUrl?.let{
    if(item.thumbnailUrl.isNotEmpty()) {
      Picasso.with(context).load(item.thumbnailUrl).into(this)
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
fun TextView.setTagName(item: Tag?){
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

@BindingAdapter("tagCheck")
fun ConstraintLayout.setTagCheck(item:Tag?){
  item?.let {
    if(item.isSelected) {
      setBackgroundColor(resources.getColor(R.color.completedPayment,null))
    }else {
      setBackgroundColor(resources.getColor(R.color.tw__solid_white,null))
    }
  }
}

@BindingAdapter("paymentName")
fun TextView.setPaymentName(item:PaymentPropertyGet?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("paymentAmount","paymentCurrency")
fun TextView.setPaymentAmount(amount:Float,currency:String){
  amount.let {
    if(it > 0){
      setTextColor(resources.getColor(R.color.positiveNumberColor,null))
    } else if(it < 0){
      setTextColor(resources.getColor(R.color.negativeNumberColor,null))
    }
    val output = it.toString() + currency
    text = output
  }
}

@BindingAdapter("paymentWhoPaid")
fun TextView.setWhoPaid(item:String?){
  item?.let {
    text = item
  }
}

@BindingAdapter("paymentTag")
fun ImageView.setTag(item:PaymentPropertyGet?){
  item?.let{
    val tagName = it.tags?.get(0)
    var tag = emptyList<Tag>()
    thread {
      tag = TagRepository(getAppDatabase(context)).tags.filter { thisTag->
        thisTag.name == tagName
      }
    }.join()
    if(tag.isNotEmpty()){
      setImageResource(tag[0].imageId)
    }
  }
}

@BindingAdapter("paymentDate")
fun TextView.setDate(item:PaymentPropertyGet?){
  item?.let{
    text = item.createdAt.split("T")[0]
  }
}

@BindingAdapter("paymentBackColor")
fun ConstraintLayout.setBackColor(item:Boolean){
  if (item){
    setBackgroundColor(resources.getColor(R.color.completedPayment,null))
  } else{
    setBackgroundColor(resources.getColor(R.color.tw__solid_white,null))
  }
}

@BindingAdapter("countryImage")
fun ImageView.setCountryImage(item:NationalFlag?){
  item?.let{
    setImageResource(item.imageId)
  }
}

@BindingAdapter("countryCode")
fun TextView.setCountryCode(item:NationalFlag?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("countryFullName")
fun TextView.setCountryFullName(item:NationalFlag?){
  item?.let{
    text = item.fullName
  }
}

@BindingAdapter("addPaymentUserName")
fun TextView.setAddPaymentUserName(item:PayerAddPayment?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("addPaymentThumbnail")
fun ImageView.setAddPaymentThumbnail(item:PayerAddPayment?){
  item?.thumbnail?.let{
    if(it.isNotEmpty()){
      Picasso.with(context).load(it).into(this)
    }
  }
}

@BindingAdapter("addPaymentAmount")
fun TextView.setAddPaymentAmount(item:PayerAddPayment?){
  item?.let {
    text = ((item.amount * 100).roundToInt().toFloat() / 100).toString()
  }
}

@BindingAdapter("addPaymentCheckBox")
fun CheckBox.setAddPaymentAmount(item:PayerAddPayment?){
  item?.let {
    isChecked = it.isPaid
  }
}
