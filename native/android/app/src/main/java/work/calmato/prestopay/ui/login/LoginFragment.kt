package work.calmato.prestopay.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentLoginBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.asDomainModel
import work.calmato.prestopay.network.RegisterDeviceIdProperty
import work.calmato.prestopay.util.finishHttpConnection
import work.calmato.prestopay.util.startHttpConnection
import work.calmato.prestopay.util.ViewModelUser
import java.util.*


class LoginFragment : Fragment() {
  private val viewModel : ViewModelUser by lazy {
    val activity = requireNotNull(this.activity){
      "You can only access the viewModel after onActivityCreated()"
    }

    ViewModelProviders.of(this, ViewModelUser.Factory(activity.application))
      .get(ViewModelUser::class.java)
  }

  private lateinit var auth: FirebaseAuth
  private lateinit var googleSignInClient: GoogleSignInClient
  private lateinit var callbackManager: CallbackManager
  private lateinit var sharedPreferences: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FacebookSdk.sdkInitialize(activity)
  }

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
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    loginNewText.setOnClickListener {
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToNewAccountFragment()
      )
    }

    auth = FirebaseAuth.getInstance()

    //Google Oauth
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build()

    googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)


    //Facebook Oauth
    facebookSingin?.setOnClickListener(View.OnClickListener {
      callbackManager = CallbackManager.Factory.create()
      LoginManager.getInstance()
        .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

      LoginManager.getInstance().registerCallback(callbackManager,
        object : FacebookCallback<LoginResult> {
          override fun onSuccess(result: LoginResult?) {
            Log.d(FACEBOOK_TAG, "facebook:onSuccess:$result")
            if (result != null) {
              handleFacebookAccessToken(result.accessToken)
            } else {
              Toast.makeText(
                requireContext(), resources.getString(R.string.authorization_failed),
                Toast.LENGTH_SHORT
              ).show()
            }
          }

          override fun onCancel() {
            Log.d(FACEBOOK_TAG, "facebook:onCancel")
            updateUI(null)
          }

          override fun onError(error: FacebookException?) {
            Log.d(FACEBOOK_TAG, "facebook:onError", error)
            updateUI(null)
          }
        })
    })

    //email password sign in
    loginButton.setOnClickListener {
      defaultSignIn(
        loginEmailField.text.toString(),
        loginPasswordField.text.toString()
      )
    }

    googleSingnin.setOnClickListener {
      googleSignIn()
    }

    //Auth check
    val value = sharedPreferences.getString("token", null)
    Log.d(DEFAULT_TAG, "token default: " + value)

    loginForgetText.setOnClickListener {
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToResetPassFragment()
      )
    }
    twitterSingnIn.setOnClickListener {
      firebaseAuthWithTwitter()
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
        Log.w(GOOGLE_TAG, "Google sign in failed", e)
        // [START_EXCLUDE]
        updateUI(null)
        // [END_EXCLUDE]
      }
    } else {
      callbackManager.onActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onStart() {
    super.onStart()

    // Check if user is signed in (non-null) and update UI accordingly.
    val currentUser = auth.currentUser
    updateUI(currentUser)
  }

  private fun defaultSignIn(email: String, password: String) {
    Log.d(DEFAULT_TAG, "signInAccount:$email")
    // [START create_user_with_email]
    if (email != "" && password != "") {
      startHttpConnection(loginButton, nowLoading, requireContext())
      auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(OnCompleteListener { task ->
          if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(DEFAULT_TAG, "signInWithEmail:success")
            val user = auth.currentUser
            user?.getIdToken(true)?.addOnCompleteListener(requireActivity()) { task ->
              val idToken = task.getResult()?.token
              val editor = sharedPreferences.edit()
              editor.putString("token", idToken)
              editor.apply()
            }
            updateUI(user)
          } else {
            finishHttpConnection(loginButton, nowLoading)
            // If sign in fails, display a message to the user.
            Log.w(DEFAULT_TAG, "signInWithEmail:failure", task.exception)
            Toast.makeText(
              requireContext(), resources.getString(R.string.authorization_failed),
              Toast.LENGTH_SHORT
            ).show()
            updateUI(null)
          }
        })
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.fill_email_password),
        Toast.LENGTH_SHORT
      ).show()
    }
    // [END create_user_with_email]
  }

  private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
    Log.d(GOOGLE_TAG, "firebaseAuthWithGoogle:" + acct.id)

    val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
    auth.signInWithCredential(credential)
      .addOnCompleteListener({ task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(GOOGLE_TAG, "signInWithGoogle:success")
          val user = auth.currentUser
          updateUI(user)
        } else {
          // If sign in fails, display a message to the user.
          Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.exception)
          Toast.makeText(
            requireContext(), resources.getString(R.string.authorization_failed),
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

  private fun firebaseAuthWithTwitter() {
    val provider: OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
    val pendingResultTask: Task<AuthResult>? = auth.pendingAuthResult
    if (pendingResultTask != null) {
      // There's something already here! Finish the sign-in for your user.
      pendingResultTask.addOnSuccessListener(object : OnSuccessListener<AuthResult?> {
        override fun onSuccess(p0: AuthResult?) {
          updateUI(auth.currentUser)
        }
      })
      pendingResultTask.addOnFailureListener(object : OnFailureListener {
        override fun onFailure(p0: java.lang.Exception) {
          Log.i("LoginFragment", "onFailure: ${p0.message}")
        }
      })
    } else {
      auth.startActivityForSignInWithProvider(/* activity= */ requireActivity(), provider.build())
        .addOnSuccessListener(object : OnSuccessListener<AuthResult> {
          override fun onSuccess(p0: AuthResult?) {
            updateUI(auth.currentUser)
          }
        })
        .addOnFailureListener(object : OnFailureListener {
          override fun onFailure(p0: java.lang.Exception) {
            Log.i("LoginFragment", "onFailure: ${p0.message}")
          }
        })
    }
  }

  private fun handleFacebookAccessToken(token: AccessToken) {
    Log.d(FACEBOOK_TAG, "handleFacebookAccessToken:$token")

    val credential = FacebookAuthProvider.getCredential(token.token)
    auth.signInWithCredential(credential)
      .addOnCompleteListener({ task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(FACEBOOK_TAG, "signInWithFacebook:success")
          val user = auth.currentUser

          updateUI(user)
        } else {
          // If sign in fails, display a message to the user.
          Log.w(FACEBOOK_TAG, "signInWithCredential:failure", task.exception)
          Toast.makeText(
            requireContext(), resources.getString(R.string.authorization_failed),
            Toast.LENGTH_SHORT
          ).show()
          updateUI(null)
        }
      })
  }

  private fun updateUI(user: FirebaseUser?) {
    if (user != null) {
      // 認証用トークンの保存
      user.getIdToken(true)
      setSharedPreference()

      // FCM用デバイスIDを送信
      sendFirebaseCloudMessageToken()

      // home pageの遷移
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
      )
    }
  }

  // sendFirebaseCloudMessageToken register instance_id to api
  private fun sendFirebaseCloudMessageToken() {
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
      if (!task.isSuccessful) {
        Log.i("LoginFragment", "getInstanceId failed: ${task.exception}")
        return@OnCompleteListener
      }

      // Get new Instance ID token and request property
      val instanceId = task.result?.token as String
      val registerDeviceIdProperty = RegisterDeviceIdProperty(instanceId)

      // send instance_id to api
      viewModel.registerDeviceId(registerDeviceIdProperty, requireActivity())
    })
  }

  private fun setSharedPreference() {
    FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
      if (it.isSuccessful) {
        val editor = sharedPreferences.edit()
        synchronized(sharedPreferences) {
          FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
              editor.putString("token", it.result?.token)
              editor.apply()
            }
          }
        }
        val id = sharedPreferences.getString("token", null)
        GlobalScope.launch(Dispatchers.IO) {
          try {
            val userProperty =
              Api.retrofitService.getLoginUserInformation("Bearer $id").await().asDomainModel()
            editor.putString("name", userProperty.name)
            editor.putString("username", userProperty.username)
            editor.putString("email", userProperty.email)
            editor.putString("thumbnailUrl", userProperty.thumbnailUrl)
            editor.apply()
          } catch (e: Exception) {
            Log.i("LoginFragment", "setSharedPreference: ${e.message}")
          }

        }
      }
    }
  }

  companion object {
    private const val DEFAULT_TAG = "EmailPassword"
    private const val GOOGLE_TAG = "GoogleActivity"
    private const val FACEBOOK_TAG = "FacebookActivity"
    private const val RC_SIGN_IN = 9001
  }
}
