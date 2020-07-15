package work.calmato.prestopay.repository

import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.database.FriendsDatabase
import work.calmato.prestopay.database.asDomainModel
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.asDatabaseModel

class FriendsRepository(private val database: FriendsDatabase) {
  val friends:LiveData<List<UserProperty>> =
        Transformations.map(database.friendDao.getFriends()){
          it.asDomainModel()
        }
  suspend fun refreshFriends(id:String){
    withContext(Dispatchers.IO){
      val friendList = Api.retrofitService.getFriends(id).await()
      database.friendDao.insertAll(*friendList.asDatabaseModel())
    }
  }
}
