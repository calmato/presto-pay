package work.calmato.prestopay.repository

import android.content.res.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.R
import work.calmato.prestopay.database.AppDatabase
import work.calmato.prestopay.database.DatabaseNationalFlag
import work.calmato.prestopay.database.asNationalFlagModel
import work.calmato.prestopay.network.NationalFlag

class NationalFlagsRepository(private val database:AppDatabase) {
  val nationalFlags:List<NationalFlag> = database.nationalFlagDao.getNationalFlags().map {
    it.asNationalFlagModel()
  }
  suspend fun setNationalFlagDatabase(resources: Resources){
    withContext(Dispatchers.IO){
      val countryList = mutableListOf<DatabaseNationalFlag>()
      val imageIds = resources.getIdList(R.array.national_flags_array)
      val countryCodes = resources.getStringArray(R.array.currency_array)
      for (i in countryCodes.indices){
        countryList.add(DatabaseNationalFlag(countryCodes[i],imageIds[i]))
      }
      database.nationalFlagDao.insertNationalFlags(*countryList.toTypedArray())
    }
  }
}
