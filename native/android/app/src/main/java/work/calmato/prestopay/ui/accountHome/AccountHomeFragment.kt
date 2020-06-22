package work.calmato.prestopay.ui.accountHome

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account_home.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAccountHomeBindingImpl

class AccountHomeFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAccountHomeBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_account_home, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    logoutButton.setOnClickListener {
      showAlertDialog()
    }
    
    displayPasswordButton.setOnClickListener {
      this.findNavController().navigate(
        AccountHomeFragmentDirections.actionAccountHomeToResetPassLoginFragment()
      )
    }
  }

  private fun showAlertDialog() {
    val builder = AlertDialog.Builder(requireContext())
    builder.setTitle("ログアウト")
    builder.setMessage("本当にログアウトしていいですか？")
    builder.setPositiveButton("YES") { dialog, which ->
      logout()
    }
    builder.setNegativeButton("NO") { dialog, which ->
    }
    val dialog: AlertDialog = builder.create()
    dialog.show()
  }

  private fun logout() {
//    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//    val editor = sharedPreferences.edit()
//    editor.putString("token", null)
    FirebaseAuth.getInstance().signOut()
    this.findNavController().navigate(
      AccountHomeFragmentDirections.actionAccountHomeToLoginFragment()
    )
  }
}
