package work.calmato.prestopay.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.UserProperty

@Entity
data class DatabaseFriend constructor(
  @PrimaryKey
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String?,
  var checked: Boolean = false
)

@Entity
data class DatabaseGroup(
  @PrimaryKey
  val id: String,
  val name: String,
  val thumbnailUrl: String,
  val userIds: List<String>,
  val createdAt: String,
  val updateAt: String
)

fun List<DatabaseFriend>.asDomainModel(): List<UserProperty> {
  return map {
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

fun List<DatabaseGroup>.asGroupModel(): List<GroupPropertyResponse> {
  return map {
    GroupPropertyResponse(
      id = it.id,
      name = it.name,
      thumbnail_url = it.thumbnailUrl,
      user_ids = it.userIds as ArrayList<String>,
      created_at = it.createdAt,
      updated_at = it.updateAt
    )
  }
}

class ListTypeConverter {
    @TypeConverter
    fun toString(userIds: List<String>): String = userIds.joinToString()

    @TypeConverter
    fun toList(userIds: String): List<String> = listOf(userIds)
}
