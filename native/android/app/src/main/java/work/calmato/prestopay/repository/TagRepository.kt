package work.calmato.prestopay.repository

import android.content.res.Resources
import android.content.res.TypedArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.R
import work.calmato.prestopay.database.AppDatabase
import work.calmato.prestopay.database.DatabaseTag
import work.calmato.prestopay.database.asTagModel
import work.calmato.prestopay.network.Tag


class TagRepository(private val database:AppDatabase) {
  val tags:List<Tag> = database.tagDao.getTags().map {
    it.asTagModel()
  }

  suspend fun setTagDatabase(resources: Resources){
    withContext(Dispatchers.IO){
      val tagList = mutableListOf<DatabaseTag>()
      val imageIds = resources.getIdList(R.array.tag_array)
      val tagNames = resources.getStringArray(R.array.tag_name)
      for (i in tagNames.indices) {
        tagList.add(DatabaseTag(tagNames[i], imageIds[i], false))
      }
      database.tagDao.insertTags(*tagList.toTypedArray())
    }
  }
}

/**
 * Returns resource ids from the array resource id.
 */
fun Resources.getIdList(arrayResourceId: Int): List<Int> {
  with(obtainTypedArray(arrayResourceId)) {
    try {
      return (0 until length()).map { getResourceId(it) }
    } finally {
      recycle()
    }
  }
}

/**
 * Returns the resource id.
 * @throws IllegalStateException when the resource id does not exist.
 */
fun TypedArray.getResourceId(index: Int): Int {
  return getResourceId(index, 0).apply { if (this == 0) throw IllegalStateException("resourceId not found.") }
}
