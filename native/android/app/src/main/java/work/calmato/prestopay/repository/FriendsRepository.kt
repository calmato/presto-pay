package work.calmato.prestopay.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.database.AppDatabase
import work.calmato.prestopay.database.DatabaseFriend
import work.calmato.prestopay.database.asDomainModel
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.UserId
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.asDatabaseModel

class FriendsRepository(private val database: AppDatabase) {
  val friends: LiveData<List<UserProperty>> =
    Transformations.map(database.friendDao.getFriends()) {
      it.asDomainModel()
    }

  suspend fun refreshFriends(id: String) {
    withContext(Dispatchers.IO) {
      val friendList = Api.retrofitService.getFriends(id).await()
      database.friendDao.deleteFriendAll()
      database.friendDao.insertAll(*friendList.asDatabaseModel())
    }
  }

  suspend fun deleteFriend(id: String) {
    withContext(Dispatchers.IO) {
      database.friendDao.deleteFriend(id)
    }
  }

  suspend fun addFriend(token: String, userId: UserId, userProperty: UserProperty) {
    withContext(Dispatchers.IO) {
      Api.retrofitService.addFriend(token, userId).await()
      database.friendDao.insertFriend(
        DatabaseFriend(
          id = userProperty.id,
          email = userProperty.email,
          name = userProperty.name,
          thumbnailUrl = userProperty.thumbnailUrl,
          username = userProperty.username
        )
      )
    }
  }

  suspend fun deleteFriendAll() {
    database.friendDao.deleteFriendAll()
  }
}
