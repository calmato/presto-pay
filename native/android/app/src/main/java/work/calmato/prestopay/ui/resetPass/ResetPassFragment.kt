package work.calmato.prestopay.ui.resetPass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_pass.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentResetPassBinding

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
        progressBarRestPass.visibility= ProgressBar.VISIBLE
        frontViewResetPass.visibility = ImageView.VISIBLE
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
          if (task.isSuccessful) {
            this.findNavController().navigate(
              ResetPassFragmentDirections.actionResetPassFragmentToMailCheckFragment()
            )
          } else {
            Toast.makeText(requireContext(), "メールを送れませんでした", Toast.LENGTH_SHORT).show()
          }
          progressBarRestPass.visibility= ProgressBar.GONE
          frontViewResetPass.visibility = ImageView.GONE
        }
      } else {
        Toast.makeText(requireContext(), resources.getString(R.string.fill_email), Toast.LENGTH_SHORT).show()
      }
    }
  }

}
