package work.calmato.prestopay.ui.groupDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_group_detail.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupDetailBinding
import work.calmato.prestopay.util.AdapterPayment
import work.calmato.prestopay.util.ViewModelPayment

class GroupDetailFragment : Fragment() {
  private val viewModel : ViewModelPayment by lazy {
    ViewModelProvider(this).get(ViewModelPayment::class.java)
  }

  private var recycleAdapter:AdapterPayment? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding:FragmentGroupDetailBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_detail,container,false)
    binding.lifecycleOwner = this
    binding.viewModelGroupDetail = viewModel
    recycleAdapter = AdapterPayment()
    binding.recyclerViewPayment.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }
}
