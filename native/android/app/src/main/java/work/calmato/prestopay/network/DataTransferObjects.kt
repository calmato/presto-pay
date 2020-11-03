package work.calmato.prestopay.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import work.calmato.prestopay.database.DatabaseFriend
import work.calmato.prestopay.database.DatabaseGroup
import work.calmato.prestopay.database.DatabasePayment

@JsonClass(generateAdapter = true)
data class NetworkFriend(
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String?,
  var checked: Boolean = false
)

@JsonClass(generateAdapter = true)
data class NetworkGroup(
  val id: String,
  val name: String,
  val thumbnailUrl: String,
  @Json(name = "userIds")val userIds: List<String>,
  val createdAt: String,
  val updatedAt: String,
  var selected: Boolean = false
)

@JsonClass(generateAdapter = true)
data class NetworkPayment(
  val id:String,
  val groupId:String,
  val name:String,
  val currency:String,
  val total:Float,
  @Json(name = "payers")val payers:List<NetworkPayer>,
  @Json(name = "postivePayers")val postivePayers:List<NetworkPayer>,
  @Json(name = "negativePayers")val negativePayers:List<NetworkPayer>,
  val isCompleted:Boolean,
  @Json(name = "tags")val tags:List<String>?,
  val comment:String?,
  @Json(name = "imageUrls")val imageUrls:List<String>?,
  val paidAt:String,
  val createdAt:String,
  val updatedAt:String
)

@JsonClass(generateAdapter = true)
data class NetworkFriendContainer(val users: List<NetworkFriend>)

@JsonClass(generateAdapter = true)
data class NetworkGroupContainer(val groups: List<NetworkGroup>)

@JsonClass(generateAdapter = true)
data class NetworkPaymentContainer(val payments:List<NetworkPayment>)

fun NetworkFriend.asDomainModel(): UserProperty {
  return UserProperty(id, name, username, email, thumbnailUrl, checked)
}

fun NetworkGroup.asDomainModel(): GroupPropertyResponse {
  return GroupPropertyResponse(id, name, thumbnailUrl, userIds, createdAt, updatedAt, selected)
}

fun NetworkPayment.asDomainModel(): PaymentPropertyGet{
  return PaymentPropertyGet(id, name, currency, total, payers, postivePayers,negativePayers,isCompleted,tags, comment, imageUrls, paidAt, createdAt, updatedAt)
}

fun NetworkFriendContainer.asDomainModel(): List<UserProperty> {
  return users.map {
    UserProperty(
      id = it.id,
      name = it.name,
      username = it.username,
      email = it.email,
      thumbnailUrl = it.thumbnailUrl,
      checked = it.checked
    )
  }
}

fun NetworkGroupContainer.asDomainModel(): List<GroupPropertyResponse> {
  return groups.map {
    GroupPropertyResponse(
      id = it.id,
      name = it.name,
      thumbnailUrl = it.thumbnailUrl,
      userIds = it.userIds,
      createdAt = it.createdAt,
      updatedAt = it.updatedAt,
      selected = it.selected
    )
  }
}

fun NetworkFriendContainer.asDatabaseModel(): Array<DatabaseFriend> {
  return users.map {
    DatabaseFriend(
      id = it.id,
      name = it.name,
      username = it.username,
      email = it.email,
      thumbnailUrl = it.thumbnailUrl,
      checked = it.checked
    )
  }.toTypedArray()
}

fun NetworkGroupContainer.asDatabaseModel(): Array<DatabaseGroup> {
  return groups.map {
    DatabaseGroup(
      id = it.id,
      name = it.name,
      thumbnailUrl = it.thumbnailUrl,
      userIds = it.userIds,
      createdAt = it.createdAt,
      updateAt = it.updatedAt
    )
  }.toTypedArray()
}

fun NetworkPaymentContainer.asDatabaseModel():Array<DatabasePayment>{
  return payments.map {
    DatabasePayment(
      id = it.id,
      groupId = it.groupId,
      name = it.name,
      currency = it.currency,
      total = it.total,
      payers = it.payers,
      positivePayers = it.postivePayers,
      negativePayers = it.negativePayers,
      isCompleted = it.isCompleted,
      tags = it.tags,
      comment = it.comment,
      imageUrls = it.imageUrls,
      paidAt = it.paidAt,
      createdAt = it.createdAt,
      updatedAt = it.updatedAt
    )
  }.toTypedArray()
}
