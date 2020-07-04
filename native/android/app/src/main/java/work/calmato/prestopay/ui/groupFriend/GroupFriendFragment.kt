package work.calmato.prestopay.ui.groupFriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_group_friend.*
import work.calmato.prestopay.R

class GroupFriendFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_group_friend, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addFriend.setOnClickListener {
      this.findNavController().navigate(
        GroupFriendFragmentDirections.actionGroupFriendFragmentToAddFriendFragment()
      )
    }
  }
}
