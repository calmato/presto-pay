package work.calmato.prestopay.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import work.calmato.prestopay.network.*


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

@Entity
data class DatabasePayment(
  @PrimaryKey
  val id: String,
  val groupId: String,
  val name: String,
  val currency: String,
  val total: Float,
  val payers: List<NetworkPayer>,
  val isCompleted: Boolean,
  val tags: List<String>?,
  val comment: String?,
  val imageUrls: List<String>?,
  val paidAt: String,
  val createdAt: String,
  val updatedAt: String
)

@Entity
data class DatabaseTag(
  @PrimaryKey
  val name:String,
  val imageId:Int,
  var isSelected:Boolean = false
)

@Entity
data class DatabaseNationalFlag(
  @PrimaryKey
  val name:String,
  val imageId:Int,
  val fullName:String
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
      thumbnailUrl = it.thumbnailUrl,
      userIds = it.userIds as List<String>,
      createdAt = it.createdAt,
      updatedAt = it.updateAt
    )
  }
}

fun List<DatabasePayment>.asPaymentModel():List<PaymentPropertyGet>{
  return map{
    PaymentPropertyGet(
      id = it.id,
      name = it.name,
      currency = it.currency,
      total = it.total,
      payers = it.payers,
      isCompleted = it.isCompleted,
      tags = it.tags,
      comment = it.comment,
      imageUrls = it.imageUrls,
      paidAt = it.paidAt,
      createdAt = it.createdAt,
      updatedAt = it.updatedAt
    )
  }
}

fun DatabaseTag.asTagModel():Tag{
  return Tag(
      name = this.name,
      imageId = this.imageId,
      isSelected = this.isSelected
    )
}

fun DatabaseNationalFlag.asNationalFlagModel():NationalFlag{
  return NationalFlag(
    name = this.name,
    imageId = this.imageId,
    fullName = this.fullName
  )
}
class ListTypeConverter {
    @TypeConverter
    fun toString(userIds: List<String?>?): String? = userIds?.joinToString() ?: ""

    @TypeConverter
    fun toList(userIds: String): List<String> = listOf(userIds)
}

class ListPayerConverter {
  @TypeConverter
  fun storedStringToMyObjects(data: String?): List<NetworkPayer>? {
    if (data == null) {
      return emptyList()
    }
    return Gson().fromJson(data, object : TypeToken<List<NetworkPayer?>?>() {}.type)
  }

  @TypeConverter
  fun myObjectsToStoredString(myObjects: List<NetworkPayer?>?): String? {
    return Gson().toJson(myObjects)
  }
}
