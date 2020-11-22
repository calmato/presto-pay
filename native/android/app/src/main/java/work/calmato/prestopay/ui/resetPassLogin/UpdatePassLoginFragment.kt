package work.calmato.prestopay.ui.resetPassLogin

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_update_pass_login.*
import kotlinx.android.synthetic.main.fragment_update_pass_login.passwordInformation
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentUpdatePassLoginBinding

class UpdatePassLoginFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentUpdatePassLoginBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_update_pass_login, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    changePassButton.setOnClickListener {
      changePass()
    }

    passEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        val textLength = s?.length
        var textColor = Color.GRAY

        if (textLength != null) {
          if (textLength < 6) {
            textColor = Color.RED
          }
        }
        passwordInformation.setTextColor(textColor)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }
    })
    passConfirmEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        var textColor = Color.GRAY

        if (!passConfirmEditText.text.toString().equals(passEditText.text.toString())) {
          textColor = Color.RED
        }
        passwordConfirmInformation.setTextColor(textColor)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

    })
  }

  private fun changePass() {
    if (passEditText.text.isNotEmpty() && passConfirmEditText.text.isNotEmpty() && currentPassEdit.text.isNotEmpty()) {
      if (passEditText.text.toString().length >= 6) {
        val newPass = passEditText.text.toString()
        if (newPass.equals(passConfirmEditText.text.toString())) {
          val auth = FirebaseAuth.getInstance()
          val user = auth.currentUser
          val cred =
            EmailAuthProvider.getCredential(
              user!!.email.toString(),
              currentPassEdit.text.toString()
            )
          progressBarUpdatePassLogIn.visibility = ProgressBar.VISIBLE
          frontViewUpdatePassLogIn.visibility = ImageView.VISIBLE
          user.reauthenticate(cred).addOnCompleteListener {
            if (it.isSuccessful) {
              user.updatePassword(newPass).addOnCompleteListener {
                if (it.isSuccessful) {
                  Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.password_changed),
                    Toast.LENGTH_SHORT
                  ).show()
                  this.findNavController().navigate(
                    UpdatePassLoginFragmentDirections.actionUpdatePassLoginFragmentToAccountHome()
                  )
                } else {
                  Toast.makeText(
                    requireContext(),
                    it.exception!!.message.toString(),
                    Toast.LENGTH_LONG
                  )
                    .show()
                }
                progressBarUpdatePassLogIn.visibility = ProgressBar.GONE
                frontViewUpdatePassLogIn.visibility = ImageView.GONE
              }
            } else {
              Toast.makeText(
                requireContext(),
                resources.getString(R.string.current_password_wrong),
                Toast.LENGTH_LONG
              ).show()
            }
          }
        } else {
          Toast.makeText(
            requireContext(),
            resources.getString(R.string.password_doesnt_match),
            Toast.LENGTH_LONG
          ).show()
        }
      } else {
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.pass_at_least_6),
          Toast.LENGTH_LONG
        ).show()
      }
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.empty_form_exist),
        Toast.LENGTH_LONG
      ).show()
    }
  }
}
