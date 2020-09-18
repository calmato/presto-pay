package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditBinding

class GroupEditFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupEditBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_edit, container, false)
    return binding.root
  }
}
