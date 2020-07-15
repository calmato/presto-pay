package work.calmato.prestopay.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import work.calmato.prestopay.network.UserProperty

@Entity
data class DatabaseUser constructor(
  @PrimaryKey
  val id: String,
  val name: String,
  val username: String,
  val email: String,
  val thumbnailUrl: String?,
  var checked: Boolean = false
)

fun List<DatabaseUser>.asDomainModel(): List<UserProperty> {
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
