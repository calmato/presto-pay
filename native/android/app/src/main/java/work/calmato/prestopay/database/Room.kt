package work.calmato.prestopay.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FriendDao {
  @Query("select * from databasefriend")
  fun getFriends(): LiveData<List<DatabaseFriend>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(vararg friends: DatabaseFriend)
}

@Dao
interface GroupDao {
  @Query("select * from databasegroup")
  fun getGroups(): LiveData<List<DatabaseGroup>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(vararg groups: DatabaseGroup)
}

@Database(
  entities =
    [DatabaseFriend::class,
    DatabaseGroup::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(ListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract val friendDao: FriendDao
  abstract val groupDao: GroupDao
}
private lateinit var INSTANCE: AppDatabase

fun getAppDatabase(context: Context): AppDatabase {
  synchronized(AppDatabase::class.java) {
    if (!::INSTANCE.isInitialized) {
      INSTANCE = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "prestoPay"
      ).build()
    }
  }
  return INSTANCE
}
