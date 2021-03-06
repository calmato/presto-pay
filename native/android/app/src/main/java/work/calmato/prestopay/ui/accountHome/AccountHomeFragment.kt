package work.calmato.prestopay.ui.accountHome

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
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
import work.calmato.prestopay.util.isOnline

class AccountHomeFragment : Fragment() {
  private lateinit var sharedPreferences: SharedPreferences

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
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
    if (thumbnailUrl!!.isNotEmpty()) {
      Picasso.with(context).load(thumbnailUrl).into(UserAccountThumnail)
    }
    // TODO: 通知の設定を実装する時はここを利用する
/*    displayNotificationButton.setOnClickListener {
      this.findNavController().navigate(
        AccountHomeFragmentDirections.actionAccountHomeToNotificationSetFragment()
      )
    }*/
  }

  private fun showAlertDialog() {
    if (isOnline(requireContext())) {
      val builder = AlertDialog.Builder(requireContext())
      builder.setTitle(resources.getString(R.string.logout))
      builder.setMessage(resources.getString(R.string.logout_confirmation))
      builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
        logout()
      }
      builder.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
      }
      val dialog: AlertDialog = builder.create()
      dialog.show()
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.bad_internet_connection),
        Toast.LENGTH_LONG
      ).show()
    }
  }

  private fun logout() {
    progressBarAccountHome.visibility = ProgressBar.VISIBLE
    frontViewAccountHome.visibility = ImageView.VISIBLE
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
    progressBarAccountHome.visibility = ProgressBar.GONE
    frontViewAccountHome.visibility = ImageView.GONE
  }
}
