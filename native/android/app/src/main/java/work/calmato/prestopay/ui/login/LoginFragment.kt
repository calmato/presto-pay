package work.calmato.prestopay.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.databinding.FragmentLoginBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.RegisterDeviceIdProperty
import work.calmato.prestopay.network.asDomainModel
import work.calmato.prestopay.repository.FriendsRepository
import work.calmato.prestopay.repository.GroupsRepository
import work.calmato.prestopay.repository.NationalFlagsRepository
import work.calmato.prestopay.repository.TagRepository
import work.calmato.prestopay.util.ViewModelUser
import work.calmato.prestopay.util.isOnline


class LoginFragment : Fragment() {
  private val viewModel: ViewModelUser by lazy {
    ViewModelProvider(this).get(ViewModelUser::class.java)
  }

  private lateinit var auth: FirebaseAuth
  private lateinit var googleSignInClient: GoogleSignInClient
  private lateinit var callbackManager: CallbackManager
  private lateinit var sharedPreferences: SharedPreferences
  private val setSharedPreferenceFlag = MutableLiveData<Boolean>(false)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentLoginBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_login, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    if (isOnline(requireContext())) {
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
      facebookSingin?.setOnClickListener {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
          .logInWithReadPermissions(this, listOf("public_profile", "email"))

        LoginManager.getInstance().registerCallback(callbackManager,
          object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
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
              updateUI(null, false)
            }

            override fun onError(error: FacebookException?) {
              updateUI(null, false)
            }
          })
      }

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

      loginForgetText.setOnClickListener {
        this.findNavController().navigate(
          LoginFragmentDirections.actionLoginFragmentToResetPassFragment()
        )
      }
      twitterSingnIn.setOnClickListener {
        firebaseAuthWithTwitter()
      }

      setSharedPreferenceFlag.observe(viewLifecycleOwner, Observer {
        if (it) {
          MainActivity.currency = sharedPreferences.getString("currency", "JPY")!!
          navigateToHome()
        }
      })

      requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
          override fun handleOnBackPressed() {
          }
        }
      )
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.turn_off_air_plane_mode),
        Toast.LENGTH_LONG
      ).show()
      MainActivity.currency = sharedPreferences.getString("currency", "JPY")!!
      navigateToHome()
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
        // [START_EXCLUDE]
        updateUI(null, false)
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
    updateUI(currentUser, false)
  }

  private fun defaultSignIn(email: String, password: String) {
    // [START create_user_with_email]
    if (email != "" && password != "") {
      progressBarLogIn.visibility = ProgressBar.VISIBLE
      frontViewLogIn.visibility = ImageView.VISIBLE
      auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            val user = auth.currentUser
            user?.getIdToken(true)?.addOnCompleteListener(requireActivity()) { thisTask ->
              val idToken = thisTask.result?.token
              val editor = sharedPreferences.edit()
              editor.putString("token", idToken)
              editor.apply()
            }
            updateUI(user, true)
          } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(
              requireContext(), resources.getString(R.string.authorization_failed),
              Toast.LENGTH_SHORT
            ).show()
            updateUI(null, false)
          }
        }
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
    val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
    auth.signInWithCredential(credential)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          val user = auth.currentUser
          updateUI(user, true)
        } else {
          // If sign in fails, display a message to the user.
          Toast.makeText(
            requireContext(), resources.getString(R.string.authorization_failed),
            Toast.LENGTH_SHORT
          ).show()
          updateUI(null, false)
        }
      }
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
      pendingResultTask.addOnSuccessListener(OnSuccessListener<AuthResult?> {
        updateUI(
          auth.currentUser,
          true
        )
      })
      pendingResultTask.addOnFailureListener(OnFailureListener { p0 ->
      })
    } else {
      auth.startActivityForSignInWithProvider(/* activity= */ requireActivity(), provider.build())
        .addOnSuccessListener { updateUI(auth.currentUser, true) }
    }
  }

  private fun handleFacebookAccessToken(token: AccessToken) {
    val credential = FacebookAuthProvider.getCredential(token.token)
    auth.signInWithCredential(credential)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          val user = auth.currentUser
          updateUI(user, true)
        } else {
          // If sign in fails, display a message to the user.
          Toast.makeText(
            requireContext(), resources.getString(R.string.authorization_failed),
            Toast.LENGTH_SHORT
          ).show()
          updateUI(null, false)
        }
      }
  }

  private fun updateUI(user: FirebaseUser?, isFirstLogin: Boolean) {
    if (user != null) {
      progressBarLogIn.visibility = ImageView.VISIBLE
      frontViewLogIn.visibility = ImageView.VISIBLE
      // 認証用トークンの保存
      user.getIdToken(true)
      setSharedPreference()

      // 最初のログインのみ実行される。　
      // トークンの情報が変更たときにFCM用デバイスIDを送信することで、ユーザーが権限を持っていることを確実にした。401帰ってきてたので。

      sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
        when (key) {
          // FCM用デバイスIDを送信
          "token" -> {
            if (isFirstLogin) {
              sendFirebaseCloudMessageToken()
              lifecycleScope.launch(Dispatchers.IO) {
                TagRepository(getAppDatabase(requireContext())).setTagDatabase(resources)
                NationalFlagsRepository(getAppDatabase(requireContext())).setNationalFlagDatabase(
                  resources
                )
              }
            }
          }
        }

      }
    } else {
      progressBarLogIn.visibility = ImageView.INVISIBLE
      frontViewLogIn.visibility = ImageView.INVISIBLE
    }
  }

  private fun navigateToHome() {
    // home pageの遷移
    this.findNavController().navigate(
      LoginFragmentDirections.actionLoginFragmentToHomeFragment()
    )
  }

  // sendFirebaseCloudMessageToken register instance_id to api
  private fun sendFirebaseCloudMessageToken() {
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
      if (!task.isSuccessful) {
        return@OnCompleteListener
      } else {
      }

      // Get new Instance ID token and request property
      val instanceId = task.result?.token as String
      val registerDeviceIdProperty = RegisterDeviceIdProperty(instanceId)

      // send instance_id to api
      viewModel.registerDeviceId(registerDeviceIdProperty)
    })
  }

  private fun setSharedPreference() {
    FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { it ->
      if (it.isSuccessful) {
        val editor = sharedPreferences.edit()
        synchronized(sharedPreferences) {
          editor.putString("token", it.result.token)
          editor.apply()
          MainActivity.firebaseId = it.result.token!!
          val id = sharedPreferences.getString("token", null)
          GlobalScope.launch(Dispatchers.IO) {
            try {
              GroupsRepository(getAppDatabase(requireContext())).refreshGroups(id!!)
              FriendsRepository(getAppDatabase(requireContext())).refreshFriends(id)
              val userProperty =
                Api.retrofitService.getLoginUserInformation("Bearer $id").await()
                  .asDomainModel()
              editor.putString("name", userProperty.name)
              editor.putString("username", userProperty.username)
              editor.putString("email", userProperty.email)
              editor.putString("thumbnailUrl", userProperty.thumbnailUrl)
              editor.apply()
              setSharedPreferenceFlag.postValue(true)
            } catch (e: Exception) {
              progressBarLogIn.visibility = ImageView.INVISIBLE
              frontViewLogIn.visibility = ImageView.INVISIBLE
            }
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
