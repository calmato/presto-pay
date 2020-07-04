package work.calmato.prestopay.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class userProperty (
  val id:String,
  val name:String,
  val thumbnail:String
):Parcelable{}
