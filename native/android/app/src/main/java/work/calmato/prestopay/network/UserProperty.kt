package work.calmato.prestopay.network

import android.os.Parcelable
import com.google.gson.JsonArray
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProperty(
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String?
) : Parcelable {}

@Parcelize
data class Users(
  val users: List<UserProperty?>
) : Parcelable {}

@Parcelize
data class addFriendResponse(
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String?
) : Parcelable {}

data class UserId(
  val userId: String
)
