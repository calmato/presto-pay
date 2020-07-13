package work.calmato.prestopay.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://api.presto-pay-stg.calmato.work/v1/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
  .add(KotlinJsonAdapterFactory())
  .build()

private val client = OkHttpClient.Builder()
  .addInterceptor(
    HttpLoggingInterceptor()
      .setLevel(HttpLoggingInterceptor.Level.BODY)
  )
  .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
  .addConverterFactory(MoshiConverterFactory.create(moshi))
  .addCallAdapterFactory(CoroutineCallAdapterFactory())
  .baseUrl(BASE_URL)
  .client(client)
  .build()

interface ApiService {

  @GET("users")
  fun getPropertiesAsync(
    @Header("Authorization") token: String,
    @Query("username") username: String
  ):
    Deferred<Users>

  @POST("auth/friends")
  fun addFriend(@Header("Authorization") token: String, @Body userId: UserId):
    Call<AddFriendResponse>

  @GET("auth/friends")
  fun getFriendsAsync(@Header("Authorization") token: String):
    Deferred<Users>

  @POST("groups")
  fun createGroup(@Header("Authorization") token: String, @Body userId: CreateGroupProperty):
    Call<CreateGroupPropertyResponse>

  @PATCH("auth")
  fun editAccount(
    @Header("Authorization") token: String,
    @Body accountProperty: EditAccountProperty
  ):
    Call<EditAccountResponse>

  @POST("auth")
  fun createAccount(
    @Body accountProperty: NewAccountProperty
  ):
    Call<AccountResponse>

  @DELETE("auth/friends/{userId}")
  fun deleteFriend(@Header("Authorization") token: String, @Path("userId") userId:String):
    Call<AccountResponse>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object Api {
  val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}
