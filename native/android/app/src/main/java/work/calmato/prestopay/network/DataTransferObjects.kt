package work.calmato.prestopay.network

import androidx.room.Database
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
  val thumbnail_url: String,
  @Json(name = "userIds")val userIds: List<String>,
  val created_at: String,
  val updated_at: String,
  var selected: Boolean = false
)

@JsonClass(generateAdapter = true)
data class NetworkPayment(
  val id:String,
  val name:String,
  val currency:String,
  val total:Float,
  @Json(name = "payers")val payers:List<NetworkPayer>,
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
  return GroupPropertyResponse(id, name, thumbnail_url, userIds, created_at, updated_at, selected)
}

fun NetworkPayment.asDomainModel(): PaymentPropertyGet{
  return PaymentPropertyGet(id, name, currency, total, payers, tags, comment, imageUrls, paidAt, createdAt, updatedAt)
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
      thumbnail_url = it.thumbnail_url,
      user_ids = it.userIds,
      created_at = it.created_at,
      updated_at = it.updated_at,
      selected = it.selected
    )
  }
}

fun NetworkPaymentContainer.asDomainModel(): List<PaymentPropertyGet>{
  return payments.map {
    PaymentPropertyGet(
      id = it.id,
      name = it.name,
      currency = it.currency,
      total = it.total,
      payers = it.payers,
      tags = it.tags,
      comment = it.comment,
      imageUrls = it.imageUrls,
      paidAt = it.paidAt,
      createdAt = it.createdAt,
      updatedAt = it.updatedAt
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
      thumbnailUrl = it.thumbnail_url,
      userIds = it.userIds,
      createdAt = it.created_at,
      updateAt = it.updated_at
    )
  }.toTypedArray()
}

fun NetworkPaymentContainer.asDatabaseModel():Array<DatabasePayment>{
  return payments.map {
    DatabasePayment(
      id = it.id,
      name = it.name,
      currency = it.currency,
      total = it.total,
      payers = it.payers,
      tags = it.tags,
      comment = it.comment,
      imageUrls = it.imageUrls,
      paidAt = it.paidAt,
      createdAt = it.createdAt,
      updatedAt = it.updatedAt
    )
  }.toTypedArray()
}
