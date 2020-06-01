package work.calmato.prestopay


import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_login.*
import work.calmato.prestopay.ui.login.LoginFragmentDirections

class MainActivity : AppCompatActivity(), View.OnClickListener {
  private lateinit var auth: FirebaseAuth
  // [END declare_auth]


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_login)
    auth = FirebaseAuth.getInstance()

    loginButton.setOnClickListener(this)
    loginNewText.setOnClickListener(this)

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val value = sharedPreferences.getString("token", null)
    Log.d(TAG, "token default: " + value)
  }

  public override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null) and update UI accordingly.
    val currentUser = auth.currentUser
    updateUI(currentUser)
  }

  @SuppressLint("ShowToast")
  private fun singInAccount(email: String, password: String) {
    Log.d(TAG, "signInAccount:$email")
    // [START create_user_with_email]
    if (email != "" && password != "") {
      FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener(OnCompleteListener { task ->
      auth.signInWithEmailAndPassword(email, password)
          if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithEmail:success")
            val user = auth.currentUser
            user?.getIdToken(true)?.addOnCompleteListener(this) { task ->
              val idToken = task.getResult()?.token

              val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
              val editor = sharedPreferences.edit()
              editor.putString("token", idToken)
              editor.apply()
            }
            updateUI(user)
          } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.exception)
            Toast.makeText(
              baseContext, "Authentication failed.",
              Toast.LENGTH_SHORT
            ).show()
            updateUI(null)
          }

        })
    } else {
      Toast.makeText(baseContext, "email and password input", Toast.LENGTH_SHORT).show()
    }
    // [END create_user_with_email]
  }

  private fun updateUI(user: FirebaseUser?) {
    if (user != null) {
      //home pageの遷移
      Log.d(TAG,user.email)
      user.getIdToken(true)
    } else {
      Toast.makeText(baseContext, "Type it again", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onClick(v: View?) {
    val i = v?.id
    when (i) {
      R.id.loginNewText -> findNavController(R.id.nav_host_fragment)
        .navigate(LoginFragmentDirections.actionLoginFragmentToNewAccountFragment())
      R.id.loginButton -> singInAccount(
        loginEmailFileld.text.toString(),
        loginPasswordField.text.toString()
      )
    }
  }

  companion object {
    private const val TAG = "EmailPassword"
  }
}
