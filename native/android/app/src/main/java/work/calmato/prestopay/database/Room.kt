package work.calmato.prestopay.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FriendDao {
  @Query("select * from databasefriend")
  fun getFriends(): LiveData<List<DatabaseFriend>>
  @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg friends:DatabaseFriend)
  @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriend(friend: DatabaseFriend)
  @Query("delete from databasefriend where id = :userId")
    fun deleteFriend(userId:String)
  @Query("delete from databasefriend")
    fun deleteFriendAll()
}

@Dao
interface GroupDao {
  @Query("select * from databasegroup")
  fun getGroups(): LiveData<List<DatabaseGroup>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(vararg groups: DatabaseGroup)

  @Query("delete from databasegroup")
  fun deleteGroupAll()
}

@Dao
interface PaymentDao{
  @Query("select * from databasepayment")
    fun getPayments():LiveData<List<DatabasePayment>>
  @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPayment(payment:DatabasePayment)
  @Query("delete from databasepayment")
    fun deleteAll()
  @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg payments:DatabasePayment)
}

@Database(
  entities =
  [DatabaseFriend::class,
    DatabaseGroup::class,
    DatabasePayment::class],
  version = 4,
  exportSchema = false
)

@TypeConverters(ListTypeConverter::class,ListPayerConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract val friendDao: FriendDao
  abstract val groupDao: GroupDao
  abstract val paymentDao : PaymentDao
}

private lateinit var INSTANCE: AppDatabase

fun getAppDatabase(context: Context): AppDatabase {
  synchronized(AppDatabase::class.java) {
    if (!::INSTANCE.isInitialized) {
      INSTANCE = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "prestoPay"
      ).fallbackToDestructiveMigration()
        .build()
    }
  }
  return INSTANCE
}
