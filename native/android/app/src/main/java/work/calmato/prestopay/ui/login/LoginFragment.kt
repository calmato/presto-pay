package work.calmato.prestopay.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
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
  }
}
