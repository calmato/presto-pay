package work.calmato.prestopay.ui.groupDetail

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupDetailBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.NetworkPayerContainer
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.util.AdapterPayment
import work.calmato.prestopay.util.ViewModelGroup
import work.calmato.prestopay.util.ViewModelPayment
import work.calmato.prestopay.util.inflateGraph

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
    viewModel.setInitPaymentList(groupDetail!!.id)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.groupStatus?.observe(viewLifecycleOwner, Observer { list ->
      list.lendingStatus.apply {
          inflateGraph(chart, this.map { it.name }, this.map { it.amount }, requireContext())
          chart.isDoubleTapToZoomEnabled = false
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
      Picasso.with(requireContext()).load(it).into(groupIcon)
    }

    frontView.visibility = ImageView.GONE
    progressBar.visibility = android.widget.ProgressBar.INVISIBLE

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
//        GroupDetailFragmentDirections.actionGroupDetailToSettleUp(
//          groupDetail!!,
//          NetworkPayerContainer(viewModel.groupStatus!!.value!!.lendingStatus)
//        )
        GroupDetailFragmentDirections.actionGroupDetailToSettleUpGroup(
          NetworkPayerContainer(
            viewModel.groupStatus!!.value!!.lendingStatus
          )
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
                  Log.d(ViewModelGroup.TAG, response.body().toString())
                  viewModel.deletePayment(
                    payments!![viewHolder.adapterPosition].id,
                    requireActivity()
                  )
                  frontView.visibility = ImageView.GONE
                  progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                  Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                  Log.d(ViewModelGroup.TAG, t.message ?: "No message")
                  frontView.visibility = ImageView.GONE
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
}
