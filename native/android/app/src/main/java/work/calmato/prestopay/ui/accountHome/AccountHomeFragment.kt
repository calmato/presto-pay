package work.calmato.prestopay.ui.accountHome

import android.app.AlertDialog
import android.content.SharedPreferences
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.databinding.FragmentAccountHomeBindingImpl
import work.calmato.prestopay.repository.FriendsRepository
import work.calmato.prestopay.repository.GroupsRepository
import work.calmato.prestopay.util.AdapterGroupPlane

class AccountHomeFragment : Fragment() {
  private var recycleGroupAdapter: AdapterGroupPlane? = null
  private lateinit var sharedPreferences: SharedPreferences

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

    displayHiddenGroupButton.setOnClickListener {
      this.findNavController().navigate(
        AccountHomeFragmentDirections.actionAccountHomeToGroupListHidden()
      )
    }

    displayProfile.setOnClickListener {
      this.findNavController().navigate(
        AccountHomeFragmentDirections.actionAccountHomeToAccountEditFragment()
      )
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          findNavController().navigate(
            AccountHomeFragmentDirections.actionAccountHomeToHomeFragment()
          )
        }
      })
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    setUserNameText.text = sharedPreferences.getString("name", "")
    val thumbnailUrl = sharedPreferences.getString("thumbnailUrl", "")
    if (thumbnailUrl.isNotEmpty()) {
      Picasso.with(context).load(thumbnailUrl).into(UserAccountThumnail)
    }
    displayNotificationButton.setOnClickListener {
      this.findNavController().navigate(
        AccountHomeFragmentDirections.actionAccountHomeToNotificationSetFragment()
      )
    }
  }

  private fun showAlertDialog() {
    val builder = AlertDialog.Builder(requireContext())
    builder.setTitle(resources.getString(R.string.logout))
    builder.setMessage(resources.getString(R.string.logout_confirmation))
    builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
      logout()
    }
    builder.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
    }
    val dialog: AlertDialog = builder.create()
    dialog.show()
  }

  private fun logout() {
    // Firebase からサインアウト
    FirebaseAuth.getInstance().signOut()

    // DatabaseとShared Preferenceを削除
    CoroutineScope(Dispatchers.IO).launch {
      GroupsRepository(getAppDatabase(requireContext())).deleteGroupAll()
      FriendsRepository(getAppDatabase(requireContext())).deleteFriendAll()
    }
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.apply()

    this.findNavController().navigate(
      AccountHomeFragmentDirections.actionAccountHomeToLoginFragment()
    )
  }
}
