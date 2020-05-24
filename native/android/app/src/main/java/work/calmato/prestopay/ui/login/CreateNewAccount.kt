package work.calmato.prestopay.ui.login

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_create_new_account.*
import work.calmato.prestopay.R

class CreateNewAccount : AppCompatActivity() {
  val serverUrl: String = "https://api.presto-pay-stg.calmato.work/v1/users"
  var jsonText: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_create_new_account)

    val createButton: Button = findViewById(R.id.createAccountButton)
    // buttonを押した時の処理を記述
    createButton.setOnClickListener {
      val thumbnails = ""
      val name = fullNameEditText.text.toString()
      val userName = userNameEditText.text.toString()
      val email = emailEditText.text.toString()
      val password = passEditText.text.toString()
      val passwordConfirmation = passConfirmEditText.text.toString()

      if (password == passwordConfirmation) {
        var map: MutableMap<String, Any> = mutableMapOf()
        map.put("name", name)
        map.put("username", userName)
        map.put("email", email)
        map.put("thumbnail", "")
        map.put("password", password)
        map.put("passwordConfirmation", passwordConfirmation)

        val gson = Gson()
        jsonText = gson.toJson(map)
        Log.d("New Account Post Json", jsonText)

        MyAsyncTask().execute()
      } else {
        println("The password and the confirmation password did not match.")
      }
    }
  }

  inner class MyAsyncTask: AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
      RestClient().postExecute(jsonText, serverUrl)
      return RestClient().getResult()
    }
  }

}
