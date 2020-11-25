package work.calmato.prestopay.ui.resetPass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_pass.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentResetPassBinding
import work.calmato.prestopay.util.finishHttpConnection
import work.calmato.prestopay.util.startHttpConnection

class ResetPassFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentResetPassBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_reset_pass, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    sendMailButton.setOnClickListener {
      if (emailEditText.text.isNotEmpty()) {
        val auth = FirebaseAuth.getInstance()
        val emailAddress = emailEditText.text.toString()
        startHttpConnection(sendMailButton,nowLoading,requireContext())
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
          if (task.isSuccessful) {
            sendEmail()
            this.findNavController().navigate(
              ResetPassFragmentDirections.actionResetPassFragmentToMailCheckFragment()
            )
          } else {
            finishHttpConnection(sendMailButton,nowLoading)
            Log.d("Reset password", "Email is not sent")
          }
        }
      } else {
        Toast.makeText(requireContext(), resources.getString(R.string.fill_email), Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun sendEmail() {
    val auth = FirebaseAuth.getInstance()
    val emailAddress = emailEditText.text.toString()
    auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        Log.d("Reset password", "Email sent.")
      } else {
        Log.d("Reset password", "Email is not sent")
      }
    }
  }
}
