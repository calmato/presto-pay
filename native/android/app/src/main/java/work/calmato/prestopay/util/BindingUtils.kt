package work.calmato.prestopay.util

import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.TagRepository
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.math.round

@BindingAdapter("thumbnail")
fun ImageView.setThumbnail(item:UserProperty?){
  item?.thumbnailUrl?.let{
    if(item.thumbnailUrl.isNotEmpty()) {
      Picasso.with(context).load(item.thumbnailUrl).into(this)
    }
  }
}

@BindingAdapter("name")
fun TextView.setUserName(item:UserProperty?){
  item?.let {
    text = item.name
  }
}

@BindingAdapter("username")
fun TextView.setListUserName(item:UserProperty?){
  item?.let {
    text = item.username
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
    when {
        it > 0 -> {
          setTextColor(resources.getColor(R.color.positiveNumberColor,null))
        }
        it < 0 -> {
          setTextColor(resources.getColor(R.color.negativeNumberColor,null))
        }
        else -> {
          setTextColor(resources.getColor(R.color.clearButtonPressed,null))
        }
    }
    val output = it.toString() + currency.toUpperCase()
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
    } else{
      setImageResource(R.drawable.ic_baseline_label_24)
    }
  }
}

@BindingAdapter("paymentDate")
fun TextView.setDate(item:PaymentPropertyGet?){
  item?.let{
    text = item.paidAt.split(" ","T")[0]
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
    text = item.name.toUpperCase(Locale.ROOT)
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

@BindingAdapter("addPaymentCheckBox")
fun CheckBox.setAddPaymentAmount(item:PayerAddPayment?){
  item?.let {
    isChecked = it.isPaid
  }
}

@BindingAdapter("paymentAmount")
fun EditText.setPaymentAmount(item:Float?){
    item?.let {
      this.setText(item.toString())
    }
}
@BindingAdapter("paymentAmountCheck")
fun TextView.setPaymentAmountCheck(item:Float?){
  item?.let {
    this.text = item.toString()
  }
}
@BindingAdapter("networkPayerName")
fun TextView.setSettleUpUserName(item:NetworkPayer?){
  item?.let {
    this.text = item.name
  }
}
@BindingAdapter("networkPayerAmount")
fun TextView.setSettleUpAmount(item:NetworkPayer?){
  item?.let {
    if (it.amount <= 0){
      this.text = "${item.name}から${round(item.amount.absoluteValue * 100)/100}${MainActivity.currency.toUpperCase(
        Locale.ROOT)}受け取る"
    } else{
      this.text = "${item.name}に${round(item.amount * 100)/100}${MainActivity.currency.toUpperCase(Locale.ROOT)}返す"
    }
  }
}

@BindingAdapter("settleUpChild")
fun RecyclerView.inflateSettleUp(item:NetworkPayerContainer?){
  item?.let {
    val recyclerAdapter = AdapterSettleUpChild(it.payers)
    this.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recyclerAdapter
    }
  }
}

