package work.calmato.prestopay.ui.mailCheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_mail_check.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentMailCheckBinding
class MailCheckFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentMailCheckBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_mail_check, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    navigateToLoginButton.setOnClickListener {
      navigateToLogin()
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
          navigateToLogin()
        }
      }
    )
  }

  private fun navigateToLogin(){
    this.findNavController().navigate(
      MailCheckFragmentDirections.actionMailCheckFragmentToLoginFragment()
    )
  }

}
