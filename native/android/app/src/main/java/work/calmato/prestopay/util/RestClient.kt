package work.calmato.prestopay.util

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class RestClient {
  var resultJson = ""
  fun postExecute(jsonText: String, serverUrl: String): Int {
    val client: OkHttpClient = OkHttpClient.Builder().build()
    val postBody =
      RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonText)
    val request: Request = Request.Builder().url(serverUrl).post(postBody).build()
    val response = client.newCall(request).execute()
    resultJson = response.body().toString()
    return response.code()
  }

  fun getResult(): String {
    return resultJson
  }
}
