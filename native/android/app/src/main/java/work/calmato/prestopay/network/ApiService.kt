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
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val BASE_URL_DEV = "https://api.presto-pay-dev.calmato.work/v1/"
private const val BASE_URL_STG = "https://api.presto-pay-stg.calmato.work/v1/"
/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
  .add(KotlinJsonAdapterFactory())
  .build()

fun getUnsafeOkHttpClient(): OkHttpClient {
  val x509TrustManager = object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
      return arrayOf()
    }
  }

  val trustManagers = arrayOf<TrustManager>(x509TrustManager)

  val sslContext = SSLContext.getInstance("TLS")
  sslContext.init(null, trustManagers, null)

  val builder = OkHttpClient.Builder()
  builder.sslSocketFactory(sslContext.socketFactory, x509TrustManager)
  builder.hostnameVerifier { _, _ -> true }
  builder.addInterceptor(
    HttpLoggingInterceptor()
      .setLevel(HttpLoggingInterceptor.Level.BODY)
  )
  return builder.build()
}

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
  .addConverterFactory(MoshiConverterFactory.create(moshi))
  .addCallAdapterFactory(CoroutineCallAdapterFactory())
  .baseUrl(BASE_URL_STG)
  .client(getUnsafeOkHttpClient())
  .build()

interface ApiService {

  @GET("users")
  fun getPropertiesAsync(
    @Header("Authorization") token: String,
    @Query("username") username: String
  ): Deferred<NetworkFriendContainer>

  @POST("auth/friends")
  fun addFriend(@Header("Authorization") token: String, @Body userId: UserId
  ): Deferred<NetworkFriend>

  @GET("auth/friends")
  fun getFriends(@Header("Authorization") token: String):
    Deferred<NetworkFriendContainer>

  @GET("auth")
  fun getLoginUserInformation(
    @Header("Authorization") token: String
  ): Deferred<NetworkFriend>

  @POST("auth/device")
  fun registerDeviceId(
    @Header("Authorization") token: String,
    @Body instanceId: RegisterDeviceIdProperty
  ): Call<AccountResponse>

  @GET("groups")
  fun getGroups(@Header("Authorization") token: String
  ): Deferred<NetworkGroupContainer>

  @GET("groups")
  fun getHiddenGroups(
    @Header("Authorization") token: String
  ): Call<HiddenGroups>


  @GET("groups/{groupId}")
  fun getGroupDetail(
    @Header("Authorization") token: String, @Path("groupId") groupId: String
  ): Call<GetGroupDetail>

  @POST("groups/{groupId}/hidden")
  fun addHiddenGroup(
    @Header("Authorization") token: String, @Path("groupId") groupId: String
  ): Call<HiddenGroups>

  @DELETE("groups/{groupId}/hidden")
  fun deleteHiddenGroup(
    @Header("Authorization") token: String, @Path("groupId") groupId: String
  ): Call<HiddenGroups>

  @DELETE("groups/{groupId}")
  fun deleteGroup(
    @Header("Authorization") token: String, @Path("groupId") groupId: String
  ): Call<Unit>

  @PATCH("auth")
  fun editAccount(
    @Header("Authorization") token: String,
    @Body accountProperty: EditAccountProperty
  ): Call<EditAccountResponse>

  @POST("groups")
  fun createGroup(@Header("Authorization") token: String, @Body userId: CreateGroupProperty
  ): Call<GroupPropertyResponse>

  @PATCH("groups/{groupId}")
  fun editGroup(
    @Header("Authorization") token: String,
    @Body accountProperty: EditGroup,
    @Path("groupId") groupId: String
  ): Call<GroupPropertyResponse>



  @POST("groups/{groupId}/users")
  fun registerFriendToGroup(
    @Header("Authorization") token: String,
    @Body userIds: Map<String, @JvmSuppressWildcards List<String>>,
    @Path("groupId") groupId: String
  ): Call<GroupPropertyResponse>

  @POST("groups/{groupId}/unauthorized-users")
  fun registerUnauthorizedUsers(
    @Header("Authorization") token: String,
    @Body userIds: Map<String, @JvmSuppressWildcards List<RegisterUnauthorizedProperty>>,
    @Path("groupId") groupId: String
  ): Call<GroupPropertyResponse>

  @POST("auth")
  fun createAccount(
    @Body accountProperty: NewAccountProperty
  ): Call<AccountResponse>

  @DELETE("auth/friends/{userId}")
  fun deleteFriend(@Header("Authorization") token: String, @Path("userId") userId: String
  ): Call<AccountResponse>

  @POST("groups/{groupId}/payments")
  fun addExpense(
    @Header("Authorization") token: String,
    @Body createExpenseProperty: CreateExpenseProperty,
    @Path("groupId") groupId: String
  ): Call<Unit>

  @GET("groups/{groupId}/payments")
  fun getPayments(
    @Header("Authorization") token: String,
    @Path("groupId") groupId: String
  ): Deferred<PaymentsResponse>

  @PATCH("groups/{groupId}/payments-status/{paymentId}")
  fun completePayment(
    @Header("Authorization") token: String,
    @Path("groupId") groupId: String,
    @Path("paymentId") paymentId: String
  ): Call<Unit>

  @DELETE("groups/{groupId}/payments/{paymentId}")
  fun deletePayment(
    @Header("Authorization") token: String,
    @Path("groupId") groupId: String,
    @Path("paymentId") paymentId: String
  ): Call<Unit>

  @PATCH("groups/{groupId}/payments/{paymentId}")
  fun updatePayment(
    @Header("Authorization") token: String,
    @Body editExpenseProperty: EditExpenseProperty,
    @Path("groupId") groupId: String,
    @Path("paymentId") paymentId: String
  ): Call<Unit>

  @PATCH("groups/{groupId}/payments-status")
  fun settleUp(
    @Header("Authorization") token: String,
    @Path("groupId") groupId: String
  ): Call<Unit>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object Api {
  val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}

