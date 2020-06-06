package work.calmato.prestopay.ui.login

import android.annotation.SuppressLint
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_login.*
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentLoginBinding
import work.calmato.prestopay.ui.newAccount.NewAccountFragmentDirections


class LoginFragment : Fragment() {
  private lateinit var auth: FirebaseAuth
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

    loginButton.setOnClickListener {
      singInAccount(
        loginEmailFileld.text.toString(),
        loginPasswordField.text.toString()
      )
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    val value = sharedPreferences.getString("token", null)
    Log.d(TAG, "token default: " + value)
    auth = FirebaseAuth.getInstance()
  }
    override fun onStart() {
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
            Log.w(TAG, "signInWithEmail:failure", task.exception)
            Toast.makeText(
              requireContext(), "Authentication failed.",
              Toast.LENGTH_SHORT
            ).show()
            Toast.makeText(requireContext(), "Type it again", Toast.LENGTH_SHORT).show()
          }

        })
    } else {
      Toast.makeText(requireContext(), "email and password input", Toast.LENGTH_SHORT).show()
    }
    // [END create_user_with_email]
  }
  private fun updateUI(user: FirebaseUser?) {
    if (user != null) {
      //home pageの遷移
      Log.d(TAG,user.email)
      user.getIdToken(true)
      this.findNavController().navigate(
        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
      )
    }
  }
  companion object {
    internal const val TAG = "EmailPassword"
  }
}
