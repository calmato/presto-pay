package work.calmato.prestopay.network

import com.squareup.moshi.JsonClass
import work.calmato.prestopay.database.DatabaseFriend
import work.calmato.prestopay.database.DatabaseGroup

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
  val userIds: ArrayList<String>,
  val createdAt: String,
  val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class NetworkFriendContainer(val users: List<NetworkFriend>)

@JsonClass(generateAdapter = true)
data class NetworkGroupContainer(val groups: List<NetworkGroup>)

fun NetworkFriend.asDomainModel(): UserProperty {
  return UserProperty(id, name, username, email, thumbnailUrl, checked)
}

fun NetworkGroup.asDomainModel(): GroupPropertyResponse {
  return GroupPropertyResponse(id, name, thumbnailUrl, userIds, createdAt, updatedAt)
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
      thumbnail_url = it.thumbnailUrl,
      user_ids = it.userIds,
      created_at = it.createdAt,
      updated_at = it.updatedAt
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

