package work.calmato.prestopay.util

import okhttp3.*

class RestClient {
  fun postExecute(jsonText: String, serverUrl: String): Response {
    val client: OkHttpClient = OkHttpClient.Builder().build()
    val postBody =
      RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonText)
    val request: Request = Request.Builder().url(serverUrl).post(postBody).build()
    return client.newCall(request).execute()
  }
}
