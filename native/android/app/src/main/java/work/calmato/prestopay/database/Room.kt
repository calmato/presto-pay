package work.calmato.prestopay.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FriendDao{
  @Query("select * from databasefriend")
    fun getFriends() : LiveData<List<DatabaseFriend>>
  @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg friends:DatabaseFriend)
  @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriend(friend: DatabaseFriend)
  @Query("delete from databasefriend where id = :userId")
    fun deleteFriend(userId:String)

}

@Database(entities = [DatabaseFriend::class],version = 1)
abstract class FriendsDatabase : RoomDatabase(){
  abstract val friendDao:FriendDao
}

private lateinit var INSTANCE: FriendsDatabase

fun getFriendsDatabase(context: Context):FriendsDatabase{
  synchronized(FriendsDatabase::class.java){
    if (!::INSTANCE.isInitialized){
      INSTANCE = Room.databaseBuilder(context.applicationContext,
      FriendsDatabase::class.java,
      "friends").build()
    }
  }
  return INSTANCE
}
