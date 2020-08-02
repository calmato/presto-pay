package work.calmato.prestopay.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProperty(
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String?,
  var checked: Boolean = false
) : Parcelable {}

@Parcelize
data class Users(
  val users: List<UserProperty>
) : Parcelable {}

data class UserId(
  val userId: String
)

@Parcelize
data class CreateGroupProperty(
  val name: String,
  val thumbnail: String,
  val userIds: List<String>
) : Parcelable {}

@Parcelize
data class CreateExpenseProperty(
  val name: String,
  val currency:String,
  val total:Int,
  val payers:List<UserExpense>,
  val tags:List<String>,
  val comment:String,
  val images: List<String>,
  val paidAt:String
) : Parcelable {}

@Parcelize
data class UserExpense(
  val id:String,
  val amount:Int
): Parcelable {}

@Parcelize
data class GroupPropertyResponse(
  val id: String,
  val name: String,
  val thumbnail_url: String,
  val user_ids: List<String>,
  val created_at: String,
  val updated_at: String
) : Parcelable {}

@Parcelize
data class EditAccountProperty(
  val name: String,
  val username: String,
  val email: String,
  val thumbnail: String
) : Parcelable {}

@Parcelize
data class EditAccountResponse(
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String,
  val groupIds: List<String>,
  val friendIds: List<String>,
  val createdAt: String,
  val updatedAt: String
) : Parcelable {}

@Parcelize
data class NewAccountProperty(
  val name: String,
  val username: String,
  val email: String,
  val thumbnail: String,
  val password: String,
  val passwordConfirmation: String
) : Parcelable

@Parcelize
data class AccountResponse(
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String,
  val groupIds: List<String>,
  val friendIds: List<String>,
  val createdAt: String,
  val updatedAt: String
) : Parcelable


