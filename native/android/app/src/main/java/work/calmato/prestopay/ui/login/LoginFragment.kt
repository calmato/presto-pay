package work.calmato.prestopay.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_login.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
  private lateinit var auth: FirebaseAuth
  private lateinit var googleSignInClient: GoogleSignInClient

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentLoginBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_login, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loginNewText.setOnClickListener {
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToNewAccountFragment()
      )
    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build()

    googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    val value = sharedPreferences.getString("token", null)
    Log.d(DEFAULTTAG, "token default: " + value)
    auth = FirebaseAuth.getInstance()

    loginButton.setOnClickListener {
      defaultSignIn(
        loginEmailFileld.text.toString(),
        loginPasswordField.text.toString()
      )
    }

    googleSingnin.setOnClickListener {
      googleSignIn()
    }

    twitterSignin.setOnClickListener {
      twitterSignIn()
    }

    facebookSingin.setOnClickListener {
      facebookSignIn()
    }

    loginForgetText.setOnClickListener {
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToResetPassFragment()
      )
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == RC_SIGN_IN) {
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      try {
        // Google Sign In was successful, authenticate with Firebase
        val account = task.getResult(ApiException::class.java)
        firebaseAuthWithGoogle(account!!)
      } catch (e: ApiException) {
        // Google Sign In failed, update UI appropriately
        Log.w(GOOGLETAG, "Google sign in failed", e)
        // [START_EXCLUDE]
        updateUI(null)
        // [END_EXCLUDE]
      }
    }
  }

  override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null) and update UI accordingly.
    val currentUser = auth.currentUser
    updateUI(currentUser)
  }

  @SuppressLint("ShowToast")
  private fun defaultSignIn(email: String, password: String) {
    Log.d(DEFAULTTAG, "signInAccount:$email")
    // [START create_user_with_email]
    if (email != "" && password != "") {
      FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener(OnCompleteListener { task ->
          auth.signInWithEmailAndPassword(email, password)
          if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(DEFAULTTAG, "signInWithEmail:success")
            val user = auth.currentUser
            user?.getIdToken(true)?.addOnCompleteListener(requireActivity()) { task ->
              val idToken = task.getResult()?.token
              val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
              val editor = sharedPreferences.edit()
              editor.putString("token", idToken)
              editor.apply()
            }
            updateUI(user)
          } else {
            // If sign in fails, display a message to the user.
            Log.w(DEFAULTTAG, "signInWithEmail:failure", task.exception)
            Toast.makeText(
              requireContext(), "Authentication failed.",
              Toast.LENGTH_SHORT
            ).show()
            Toast.makeText(requireContext(), "Type it again", Toast.LENGTH_SHORT).show()
            updateUI(null)
          }
        })
    } else {
      Toast.makeText(requireContext(), "email and password input", Toast.LENGTH_SHORT).show()
    }
    // [END create_user_with_email]
  }

  private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
    Log.d(GOOGLETAG, "firebaseAuthWithGoogle:" + acct.id)

    val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
    auth.signInWithCredential(credential)
      .addOnCompleteListener({ task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(GOOGLETAG, "signInWithGoogle:success")
          val user = auth.currentUser
          updateUI(user)
        } else {
          // If sign in fails, display a message to the user.
          Log.w(GOOGLETAG, "signInWithCredential:failure", task.exception)
          Toast.makeText(
            requireContext(), "Authentication failed.",
            Toast.LENGTH_SHORT
          ).show()
          updateUI(null)
        }
      })
  }

  private fun googleSignIn() {
    val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
  }

  private fun twitterSignIn() {

  }

  private fun facebookSignIn() {

  }

  private fun updateUI(user: FirebaseUser?) {
    if (user != null) {
      //home pageの遷移
      Log.d(DEFAULTTAG, user.email)
      user.getIdToken(true)
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
      )
    }
  }

  companion object {
    private const val DEFAULTTAG = "EmailPassword"
    private const val GOOGLETAG = "GoogleActivity"
    private const val TwitterTAG = "TWITTERActｋjkivity"
    private const val RC_SIGN_IN = 9001
  }
}
