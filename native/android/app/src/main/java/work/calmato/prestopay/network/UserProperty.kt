package work.calmato.prestopay.network

import android.os.Parcelable
import com.google.gson.JsonArray
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProperty (
  val id:String,
  val name:String,
  val username:String,
  val email:String,
  val thumbnail:String?
):Parcelable{}

@Parcelize
data class Users(
  val users:List<UserProperty>
):Parcelable {}
