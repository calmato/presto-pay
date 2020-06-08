package work.calmato.prestopay.ui.accountHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAccountHomeBindingImpl
import work.calmato.prestopay.databinding.FragmentHomeBinding

class AccountHomeFragment : Fragment(){
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAccountHomeBindingImpl = DataBindingUtil.inflate (
      inflater, R.layout.fragment_account_home, container, false
    )
    return binding.root
  }
}
