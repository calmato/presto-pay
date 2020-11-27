package work.calmato.prestopay.ui.settleUpGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import work.calmato.prestopay.util.ViewModelSettleUpGroup

class SettleUpGroupFragment : Fragment() {
  private val viewModel: ViewModelSettleUpGroup by lazy {
    ViewModelProvider(this).get(ViewModelSettleUpGroup::class.java)
  }
}
