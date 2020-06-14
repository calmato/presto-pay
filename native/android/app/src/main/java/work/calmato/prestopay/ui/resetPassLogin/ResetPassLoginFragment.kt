package work.calmato.prestopay.ui.resetPassLogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_pass_login.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentResetPassLoginBinding

class ResetPassLoginFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentResetPassLoginBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_reset_pass_login, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    changePassButton.setOnClickListener {
      changePass()
    }
  }

  private fun changePass() {
    val newPass = newPassEdit.text.toString()
    if (newPass.equals(passConfirmEdit.text.toString())) {
      val auth = FirebaseAuth.getInstance()
      val user = auth.currentUser
      val cred =
        EmailAuthProvider.getCredential(user!!.email.toString(), currentPassEdit.text.toString())
      user.reauthenticate(cred).addOnCompleteListener {
        if (it.isSuccessful) {
          user.updatePassword(newPass).addOnCompleteListener {
            if (it.isSuccessful) {
              Toast.makeText(requireContext(), "パスワードを変更しました", Toast.LENGTH_SHORT).show()
              this.findNavController().navigate(
                ResetPassLoginFragmentDirections.actionResetPassLoginFragmentToAccountHome()
              )
            } else {
              Toast.makeText(requireContext(), it.exception!!.message.toString(), Toast.LENGTH_LONG)
                .show()
            }
          }
        } else {
          Toast.makeText(requireContext(), "現在のパスワードが正しくありません", Toast.LENGTH_LONG).show()
        }
      }
    } else {
      Toast.makeText(requireContext(), "パスワードが一致しません", Toast.LENGTH_LONG).show()
    }

  }
}
