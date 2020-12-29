package work.calmato.prestopay.ui.groupDetail

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_group_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.R
import work.calmato.prestopay.database.DatabaseGroup
import work.calmato.prestopay.databinding.FragmentGroupDetailBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.NetworkPayerContainer
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.util.*
import java.util.*

class GroupDetailFragment : Fragment() {
  private val viewModel: ViewModelPayment by lazy {
    ViewModelProvider(this).get(ViewModelPayment::class.java)
  }
  private var recycleAdapter: AdapterPayment? = null
  private var groupDetail: GroupPropertyResponse? = null
  private var payments: List<PaymentPropertyGet>? = null
  private lateinit var clickListenerGroupDetail: AdapterPayment.OnClickListener
  private lateinit var id: String
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentGroupDetailBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_detail, container, false)
    binding.lifecycleOwner = this
    binding.viewModelGroupDetail = viewModel
    clickListenerGroupDetail = AdapterPayment.OnClickListener {
      viewModel.itemIsClicked(it)
    }
    recycleAdapter = AdapterPayment(requireContext(), clickListenerGroupDetail)
    binding.recyclerViewPayment.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    groupDetail = GroupDetailFragmentArgs.fromBundle(requireArguments()).groupDetail
    id = MainActivity.firebaseId
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    countryImageView.setOnClickListener {
      showCurrencyDialog()
    }
    viewModel.getCountryList()
    viewModel.setInitPaymentList(groupDetail!!.id)
    viewModel.groupStatus?.value?.lendingStatus.apply {
      this?.let {
        inflateGraph(
          chart,
          it.map { it.name },
          this.map { it.amount },
          requireContext()
        )
      }
      chart.isDoubleTapToZoomEnabled = false
    }
    currency.text = MainActivity.currency.toUpperCase(Locale.ROOT)
    viewModel.countryList.observe(viewLifecycleOwner, Observer {
      it?.let {
        if (it.isNotEmpty()) {
          countryImageView.setBackgroundResource(it.filter { it.name.equals(MainActivity.currency,true)}[0].imageId)
        }
      }
    })

    viewModel.groupStatus?.observe(viewLifecycleOwner, Observer<DatabaseGroup> { list ->
      list?.let {
        it.lendingStatus.apply {
          inflateGraph(chart, this.map { it.name }, this.map { it.amount }, requireContext())
        }
      }
    })
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      it?.apply {
        navigateToDetail(this)
      }
    })
    groupDetail?.let {
      viewModel.getPayments(it.id)
    }
    setting.setOnClickListener {
      this.findNavController().navigate(
        GroupDetailFragmentDirections.actionGroupDetailToGroupEditFragment(groupDetail)
      )
    }
    viewModel.paymentsList!!.observe(viewLifecycleOwner, Observer<List<PaymentPropertyGet>> {
      it?.apply {
        recycleAdapter?.paymentList = it
        payments = it
        if(it.isEmpty()){
          settleUp.text = "支払い情報がありません"
          settleUp.isEnabled = false
        }
      }
    })

    val swipeToPaymentDismissTouchHelper = getSwipeToDismissTouchHelper()
    swipeToPaymentDismissTouchHelper.attachToRecyclerView(recyclerViewPayment)

    viewModel.refreshing.observe(viewLifecycleOwner, Observer {
      it?.apply {
        swipeContainer.isRefreshing = it
        floatingActionButton.isClickable = !it
        groupIcon.isClickable = !it
        settleUp.isClickable = !it
        setting.isClickable = !it
      }
    })
    swipeContainer.setOnRefreshListener {
      groupDetail?.let {
        viewModel.getPayments(it.id)
      }
    }
    groupDetail?.thumbnailUrl?.let {
      if (!it.isNullOrEmpty()) {
        Picasso.with(requireContext()).load(it).into(groupIcon)
      }
    }

    frontView.visibility = ImageView.GONE
    progressBar.visibility = ProgressBar.INVISIBLE

    bottom_navigation.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_person -> {
          this.findNavController().navigate(
            GroupDetailFragmentDirections.actionGroupDetailToAccountHome()
          )
          true
        }
        R.id.action_people -> {
          this.findNavController().navigate(
            GroupDetailFragmentDirections.actionGroupDetailToGroupFriendFragment()
          )
          true
        }
        else -> true
      }
    }
    floatingActionButton.setOnClickListener {
      this.findNavController().navigate(
        GroupDetailFragmentDirections.actionGroupDetailToAddPayment(groupDetail!!, null)
      )
    }
    settleUp.setOnClickListener {
      this.findNavController().navigate(
        GroupDetailFragmentDirections.actionGroupDetailToSettleUpGroup(
          NetworkPayerContainer(
            viewModel.groupStatus!!.value!!.lendingStatus
          ),
          groupDetail!!
        )
      )
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackHome()
        }
      }
    )
    if(!isOnline(requireContext())){
      countryImageView.isEnabled = false
    }
  }

  private fun getSwipeToDismissTouchHelper() =
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.LEFT,
      ItemTouchHelper.LEFT
    ) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        return false
      }

      //スワイプ時に実行
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val builder: AlertDialog.Builder = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder.setMessage(resources.getString(R.string.delete_question))
          ?.setPositiveButton(
            resources.getString(R.string.delete)
          ) { _, _ ->
            frontView.visibility = ImageView.VISIBLE
            progressBar.visibility = android.widget.ProgressBar.VISIBLE
            Api.retrofitService.deletePayment(
              "Bearer $id",
              groupDetail!!.id,
              payments!![viewHolder.adapterPosition].id
            )
              .enqueue(object : Callback<Unit> {
                override fun onResponse(
                  call: Call<Unit>,
                  response: Response<Unit>
                ) {
                  if(response.isSuccessful) {
                    viewModel.deletePayment(
                      payments!![viewHolder.adapterPosition].id
                    )
                  } else {
                    Toast.makeText(activity, resources.getString(R.string.failed_delete_payment), Toast.LENGTH_LONG).show()
                  }
                  frontView.visibility = ImageView.GONE
                  progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                  Toast.makeText(activity, resources.getString(R.string.failed_delete_payment), Toast.LENGTH_LONG).show()
                  frontView.visibility = ImageView.GONE
                  progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                }
              })
          }
          ?.setNegativeButton(resources.getString(R.string.cancel), null)
        recycleAdapter?.notifyDataSetChanged()
        val dialog: AlertDialog? = builder.create()
        dialog?.show()
      }

      //スワイプした時の背景を設定
      override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
      ) {
        super.onChildDraw(
          c,
          recyclerView,
          viewHolder,
          dX,
          dY,
          actionState,
          isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val background = ColorDrawable(Color.RED)
        val deleteIcon = AppCompatResources.getDrawable(
          requireContext(),
          R.drawable.ic_baseline_delete_sweep_24
        )
        val iconMarginVertical =
          (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2
        deleteIcon.setBounds(
          itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
          itemView.top + iconMarginVertical,
          itemView.right - iconMarginVertical,
          itemView.bottom - iconMarginVertical
        )
        background.setBounds(
          itemView.right,
          itemView.top,
          itemView.left + dX.toInt(),
          itemView.bottom
        )
        background.draw(c)
        deleteIcon.draw(c)
      }

    })

  private fun navigateToDetail(payment: PaymentPropertyGet) {
    this.findNavController().navigate(
      GroupDetailFragmentDirections.actionGroupDetailToPaymentDetail(payment, groupDetail!!)
    )
    viewModel.navigationCompleted()
  }

  private fun goBackHome() {
    this.findNavController().navigate(
      GroupDetailFragmentDirections.actionGroupDetailToHomeFragment()
    )
  }

  private fun showCurrencyDialog() {
    val builder: AlertDialog.Builder = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val currencyRecycleAdapter = AdapterCurrency(AdapterCurrency.OnClickListener {
      countryImageView.setBackgroundResource(it.imageId)
      currency.text = it.name.toUpperCase(Locale.ROOT)
    })
    currencyRecycleAdapter.countryList = viewModel.countryList.value!!
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = currencyRecycleAdapter
    }
    builder.setView(recycleView)
    val dialog: AlertDialog? = builder.create()
    dialog?.show()
    currency.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dialog?.dismiss()
        MainActivity.currency = s.toString()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = sharedPreferences.edit()
        editor.putString("currency",s.toString())
        editor.apply()
        groupDetail?.let {
          viewModel.getPayments(it.id)
        }
      }

      override fun afterTextChanged(s: Editable?) {
      }
    })
  }
}
