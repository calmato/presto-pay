package work.calmato.prestopay.ui.home

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentHomeBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users


class HomeFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentHomeBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_home, container, false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    floatingActionButton.setOnClickListener {
      this.findNavController().navigate(
        HomeFragmentDirections.ActionHomeFragmentToFriendListFragment(Users(emptyList<UserProperty>()))
      )
    }
    bottom_navigation.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_person -> {
          this.findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToAccountHome()
          )
          true
        }
        R.id.action_people -> {
          this.findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToGroupFriendFragment()
          )
          true
        }
        else -> false
      }
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()
    synchronized(sharedPreferences) {
      FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
        if (it.isSuccessful) {
          editor.putString("token", it.result?.token)
          editor.apply()
        }
      }
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
        }
      })
  }
}
