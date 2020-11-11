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
data class Groups(
  val groups: List<GroupPropertyResponse>
) : Parcelable {}

@Parcelize
data class HiddenGroups(
  val hiddenGroups: List<GroupPropertyResponse>
) : Parcelable {}

@Parcelize
data class RegisterDeviceIdProperty(
  val instanceId: String
) : Parcelable {}

@Parcelize
data class CreateGroupProperty(
  val name: String,
  val thumbnail: String,
  val userIds: List<String>
) : Parcelable {}

@Parcelize
data class EditGroup(
  val name: String,
  val thumbnail: String,
  val userIds: List<String>
) : Parcelable {}

@Parcelize
data class CreateExpenseProperty(
  var name: String,
  var currency: String?,
  var total: Float?,
  var positivePayers: List<UserExpense>?,
  var negativePayers: List<UserExpense>?,
  var tags: List<String>?,
  var comment: String?,
  var images: List<String>?,
  var paidAt: String?
) : Parcelable {}

@Parcelize
data class EditExpenseProperty(
  var name: String,
  var currency: String?,
  var total: Float?,
  var positivePayers: List<UserExpense>?,
  var negativePayers: List<UserExpense>?,
  val isCompleted: Boolean,
  var tags: List<String>?,
  var comment: String?,
  var images: List<String>?,
  var paidAt: String?
) : Parcelable {}

@Parcelize
data class UserExpense(
  val id: String,
  var amount: Float
) : Parcelable {}

@Parcelize
data class NetworkPayer(
  val id: String,
  val name: String,
  var amount: Float
) : Parcelable

@Parcelize
data class GroupPropertyResponse(
  val id: String,
  val name: String,
  val thumbnailUrl: String,
  val userIds: List<String>,
  val createdAt: String,
  val updatedAt: String,
  var selected: Boolean = false,
  var isHidden: Boolean = false
) : Parcelable {}

@Parcelize
data class GetGroupDetail(
  val id: String,
  val name: String,
  val thumbnailUrl: String,
  val users: List<UserProperty>,
  val createdAt: String,
  val updatedAt: String
) : Parcelable {}

@Parcelize
data class PaymentPropertyGet(
  val id: String,
  val name: String,
  val currency: String,
  val total: Float,
  val payers: List<NetworkPayer>,
  val positivePayers: List<NetworkPayer>,
  val negativePayers: List<NetworkPayer>,
  val isCompleted: Boolean,
  val tags: List<String>?,
  val comment: String?,
  val imageUrls: List<String>?,
  val paidAt: String,
  val createdAt: String,
  val updatedAt: String
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

@Parcelize
data class Payer(
  val id: String,
  val amount: Float,
  val isPaid: Boolean
) : Parcelable

@Parcelize
data class Tag(
  val name: String,
  val imageId: Int,
  var isSelected: Boolean
) : Parcelable

@Parcelize
data class NationalFlag(
  val name:String,
  val imageId:Int,
  val fullName:String
) : Parcelable

@Parcelize
data class PaymentCompleteResponse(
  val id: String,
  val name: String,
  val currency: String,
  val total: Float,
  val payers: List<Payer>,
  val isCompleted: Boolean,
  val tags: List<String>?,
  val comment: String?,
  val imageUrls: List<String>?,
  val paidAt: String,
  val createdAt: String,
  val updatedAt: String
) : Parcelable {}

@Parcelize
data class PayerAddPayment(
  val id: String,
  val name:String,
  val thumbnail: String,
  var amount: Float,
  var isPaid: Boolean
) : Parcelable
