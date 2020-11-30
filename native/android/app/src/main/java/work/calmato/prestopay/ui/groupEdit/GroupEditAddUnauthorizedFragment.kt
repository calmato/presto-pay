package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditAddUnauthorizedBinding
import work.calmato.prestopay.network.GetGroupDetail

class GroupEditAddUnauthorizedFragment : Fragment() {
  private lateinit var id: String
  private var getGroupInfo: GetGroupDetail? = null
  private lateinit var doneButton: MenuItem

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupEditAddUnauthorizedBinding =
      DataBindingUtil.inflate(
        inflater,
        R.layout.fragment_group_edit_add_unauthorized,
        container,
        false
      )
    binding.lifecycleOwner = this
    getGroupInfo = GroupEditAddFriendFragmentArgs.fromBundle(requireArguments()).groupEditList
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
    return binding.root
  }
}
