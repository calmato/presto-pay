package work.calmato.prestopay.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.database.AppDatabase
import work.calmato.prestopay.database.asGroupModel
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.asDatabaseModel

class GroupsRepository(private val database: AppDatabase) {
  val groups:LiveData<List<GroupPropertyResponse>> =
    Transformations.map(database.groupDao.getGroups()) {
      it.asGroupModel()
    }
  suspend fun refreshGroups(id:String) {
    withContext(Dispatchers.IO) {
      val groupList = Api.retrofitService.getGroups(id).await()
      database.groupDao.insertAll(*groupList.asDatabaseModel())
    }
  }
}