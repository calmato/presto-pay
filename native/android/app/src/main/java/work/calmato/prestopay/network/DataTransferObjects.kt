package work.calmato.prestopay.network

import com.squareup.moshi.JsonClass
import work.calmato.prestopay.database.DatabaseFriend

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
data class NetworkFriendContainer(val users:List<NetworkFriend>)

fun NetworkFriend.asDomainModel(): UserProperty{
  return UserProperty(id, name, username, email, thumbnailUrl, checked)
}

fun NetworkFriend.asDatabaseModel():DatabaseFriend{
  return DatabaseFriend(
    id = this.id,
    name = this.name,
    username = this.username,
    email = this.email,
    thumbnailUrl = this.thumbnailUrl,
    checked = this.checked
  )
}

fun NetworkFriendContainer.asDomainModel(): List<UserProperty>{
  return users.map{
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

fun NetworkFriendContainer.asDatabaseModel():Array<DatabaseFriend>{
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

