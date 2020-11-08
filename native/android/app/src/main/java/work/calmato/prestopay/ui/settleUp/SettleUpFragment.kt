package work.calmato.prestopay.ui.settleUp

import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settle_up.*
import kotlinx.android.synthetic.main.fragment_settle_up.currency
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentSettleUpBindingImpl
import work.calmato.prestopay.network.UserExpense
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_add_friend.*

class SettleUpFragment : Fragment() {
  private val viewModel: ViewModelAddPayment by lazy {
    ViewModelProvider(this).get(ViewModelAddPayment::class.java)
  }
  lateinit var borrower: UserExpense
  lateinit var lender: UserExpense
  private lateinit var doneButton: MenuItem

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentSettleUpBindingImpl =
      DataBindingUtil.inflate(inflater, R.layout.fragment_settle_up, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setHasOptionsMenu(true)
    viewModel.setGroupInfo(SettleUpFragmentArgs.fromBundle(requireArguments()).groupDetail)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    viewModel.setId(sharedPreferences.getString("token", "")!!)
    viewModel.getGroupDetail()
    val c = Calendar.getInstance()
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
    viewModel.setPaidAt("${date}T00:00:00.000Z")
    viewModel.getCountryList()

    thumbnail1.setOnClickListener {
      showUserDialog(thumbnail1, username1, 1)
    }
    thumbnail2.setOnClickListener {
      showUserDialog(thumbnail2, username2, 2)
    }
    currency.setOnClickListener {
      showCurrencyDialog()
    }
    viewModel.navigateToGroupDetail.observe(viewLifecycleOwner, Observer {
      this.findNavController().navigate(
        SettleUpFragmentDirections.actionSettleUpToGroupDetail(viewModel.groupInfo)
      )
    })
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> {
        if (total.text.isNullOrEmpty()) {
          Toast.makeText(
            requireContext(),
            resources.getString(R.string.fill_amount),
            Toast.LENGTH_LONG
          ).show()
        } else if(username1.text.isNullOrEmpty() || username2.text.isNullOrEmpty()){
          Toast.makeText(
            requireContext(),
            resources.getString(R.string.choose_involved_users),
            Toast.LENGTH_LONG
          ).show()
        } else{
          viewModel.setTotal(total.text.toString().toFloat())
          viewModel.setCurrency(currency.text.toString())
          viewModel.setThumbnail(encodeImage2Base64(thumbnail1))
          lender.amount = viewModel.total.value!!
          borrower.amount = viewModel.total.value!! * -1
          startHttpConnectionMenu(doneButton, nowLoading, requireContext())
          viewModel.settleUp(borrower, lender)
          finishHttpConnectionMenu(doneButton, nowLoading)
        }
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    doneButton = menu.getItem(0)
  }

  private fun showCurrencyDialog() {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val currencyRecycleAdapter = AdapterCurrency(AdapterCurrency.OnClickListener {
      currency.text = it.name
    })
    currencyRecycleAdapter.countryList = viewModel.countryList
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = currencyRecycleAdapter
    }
    builder?.setView(recycleView)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
    currency.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dialog?.dismiss()
      }

      override fun afterTextChanged(s: Editable?) {
      }

    })
  }

  private fun showUserDialog(thumbanil: ImageView, name: TextView, status: Int) {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val userRecycleAdapter =
      AdapterGrid(Users(viewModel.groupDetail.users), AdapterGrid.OnClickListener {
        name.text = it.name
        Picasso.with(requireContext()).load(it.thumbnailUrl).into(thumbanil)
        if (status == 1) {
          lender = UserExpense(amount = 0f, id = it.id)
        } else {
          borrower = UserExpense(amount = 0f, id = it.id)
        }
      })
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = GridLayoutManager(requireContext(), 3)
      adapter = userRecycleAdapter
    }
    builder?.setView(recycleView)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
    name.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dialog?.dismiss()
      }

      override fun afterTextChanged(s: Editable?) {
      }

    })
  }
}
