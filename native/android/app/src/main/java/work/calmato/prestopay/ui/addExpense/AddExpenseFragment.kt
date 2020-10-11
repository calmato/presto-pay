package work.calmato.prestopay.ui.addExpense

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_add_expense.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.database.getAppDatabase
import work.calmato.prestopay.databinding.FragmentAddExpenseBindingImpl
import work.calmato.prestopay.network.*
import work.calmato.prestopay.repository.NationalFlagsRepository
import work.calmato.prestopay.repository.TagRepository
import work.calmato.prestopay.util.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

class AddExpenseFragment : PermissionBase() {
  private val viewModelFriend: ViewModelFriend by lazy {
    ViewModelProvider(this).get(ViewModelFriend::class.java)
  }

  private var getGroupInfo: GroupPropertyResponse? = null
  private var recycleAdapter: AdapterCheck? = null
  private lateinit var clickListener: AdapterCheck.OnClickListener
  private var groupMembers: MutableList<UserProperty> = mutableListOf()
  private var groupDetail: GetGroupDetail? = null

  private val CODE = 11

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddExpenseBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_add_expense, container, false
    )
    clickListener = AdapterCheck.OnClickListener { viewModelFriend.itemIsClicked(it) }
    recycleAdapter = AdapterCheck(clickListener)
    getGroupInfo = AddExpenseFragmentArgs.fromBundle(requireArguments()).group
    return binding.root
  }

  private lateinit var id: String
  private lateinit var doneButton: MenuItem
  private lateinit var tagList: List<Tag>
  private lateinit var countryList: List<NationalFlag>

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == CODE && resultCode == Activity.RESULT_OK) {
      inflateDate(data?.getStringExtra("selectedDate"))
      calendar.visibility = ImageView.INVISIBLE
      calendarYear.visibility = TextView.VISIBLE
      calendarDate.visibility = TextView.VISIBLE
    }
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      cropImage(data?.data!!)
    }
    if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
      val resultUri = UCrop.getOutput(data!!)
      thumbnail.setImageURI(resultUri)
      thumbnail.visibility = ImageView.VISIBLE
    }
  }

  private fun addExpensePayer(groupDetail: GetGroupDetail) {
    groupMembers.addAll(groupDetail.users)
    val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_checked,
      groupMembers.map { it.name })
    adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked)
    payerSpinner.adapter = adapter
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    thread {
      tagList = TagRepository(getAppDatabase(requireContext())).tags
      countryList = NationalFlagsRepository(getAppDatabase(requireContext())).nationalFlags
    }
    thread {
      try {
        Api.retrofitService.getGroupDetail("Bearer $id", getGroupInfo!!.id)
          .enqueue(object : Callback<GetGroupDetail> {
            override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
              Log.d(ViewModelGroup.TAG, t.message)
            }

            override fun onResponse(
              call: Call<GetGroupDetail>,
              response: Response<GetGroupDetail>
            ) {
              Log.d(ViewModelGroup.TAG, response.body().toString())
              groupDetail = response.body()!!
              groupName.text = groupDetail!!.name
              addExpensePayer(groupDetail!!)
              targetText.setOnClickListener {
                showTargetDialog()
              }
            }
          })
      } catch (e: Exception) {
        Log.d(TAG, "debug $e")
      }
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!

    val c = Calendar.getInstance()
    inflateDate(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(c.time))

    constraintDate.setOnClickListener {
      val datePickerFragment = DatePickerFragment()
      datePickerFragment.setTargetFragment(this, CODE)
      datePickerFragment.show(parentFragmentManager, "datePicker")
    }
    camera.setOnClickListener {
      requestPermission()
    }
    comment.setOnClickListener {
      showCommentDialog()
    }
    advancedSetting.setOnClickListener {
      val advancedDialog = AdvancedExpenseFragment()
      advancedDialog.show(parentFragmentManager, "advancedExpense")
    }
    tagImage.setOnClickListener {
      showTagDialog()
    }
    currencyTextView.setOnClickListener {
      showCurrencyDialog()
    }
    setHasOptionsMenu(true)

    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          goBackHome()
        }
      }
    )
  }

  fun showTargetDialog() {
    groupMembers.filter { userProperty -> userProperty.name == payerSpinner.selectedItem }[0].checked =
      true
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val targetRecycleAdapter = AdapterCheck(null)
    targetRecycleAdapter.friendList =
      groupDetail!!.users.filter { userProperty -> userProperty.name != payerSpinner.selectedItem }
    val recyclerView = RecyclerView(requireContext())
    recyclerView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = targetRecycleAdapter
    }
    builder?.setView(recyclerView)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendRequest()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendRequest() {
    val groupId = groupDetail!!.id
    //TODO: 必要な値はSTEPごとに入力させる
    val name = if (expenseName.text.isNullOrBlank()) {
      "unko"
    } else {
      expenseName.text.toString()
    }
    val totalString = if (amountEdit.text.isNullOrBlank()){
      "100.0"
    } else{
      amountEdit.text.toString()
    }
    val currency = currencyTextView.text.toString()
    val payer = payerSpinner.selectedItem
    val payers = mutableListOf(
      UserExpense(
        groupMembers.filter { userProperty -> userProperty.name == payer }[0].id,
        totalString.toFloat()
      )
    )
    val targetUsers = groupMembers.filter { it.checked && it.name != payer }
    val isPayerIncluded =
      groupMembers.filter { userProperty -> userProperty.name == payer }[0].checked
    val targetProperty = targetUsers.map { userProperty ->
      val isPayerIncludedInt = if (isPayerIncluded) 1 else 0
      UserExpense(
        userProperty.id,
        totalString.toFloat() / (targetUsers.size + isPayerIncludedInt) * -1f
      )
    }
    payers.addAll(targetProperty)
    if (isPayerIncluded) {
      payers[0].amount = payers[0].amount - totalString.toFloat() / (targetUsers.size + 1)
    }
    val tags = mutableListOf<String>()
    for (i in tagList.filter { it.isSelected }) {
      tags.add(i.name)
    }
    val monthDate = calendarDate.text.split("/")
    val paidAt = "${calendarYear.text}-${monthDate[0]}-${monthDate[1]}T00:00:00.000Z"
    val images = encodeImage2Base64(camera)
    val comment = commentEditText.text.toString()
    when {
      name.isEmpty() -> {
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.fill_expense_name),
          Toast.LENGTH_LONG
        ).show()
      }
      totalString.isEmpty() -> {
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.fill_total_amount),
          Toast.LENGTH_LONG
        ).show()
      }
      else -> {
        val total = totalString.toFloat()
        var sumPayment = 0f
        for (i in payers) {
          sumPayment += i.amount
        }
        Log.i(TAG, "sendRequest: $sumPayment")
        Log.i(TAG, "sendRequest: $payers")
        if (sumPayment.absoluteValue < 0.01f) {
          val expenseProperty = CreateExpenseProperty(
            name, currency, total, payers, tags, comment, listOf(images), paidAt
          )
          addExpenseApi(expenseProperty, groupId)
        } else {
          Toast.makeText(
            requireContext(),
            resources.getString(R.string.total_amount_error),
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }
  }

  private fun addExpenseApi(expenseProperty: CreateExpenseProperty, groupId: String) {
    startHttpConnectionMenu(doneButton, nowLoading, requireContext())
    Api.retrofitService.addExpense("Bearer $id", expenseProperty, groupId)
      .enqueue(object : Callback<CreateExpenseResponse> {
        override fun onFailure(call: Call<CreateExpenseResponse>, t: Throwable) {
          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
          finishHttpConnectionMenu(doneButton, nowLoading)
        }

        override fun onResponse(
          call: Call<CreateExpenseResponse>,
          response: Response<CreateExpenseResponse>
        ) {
          if (response.isSuccessful) {
            Toast.makeText(
              activity,
              resources.getString(R.string.registered_new_expense),
              Toast.LENGTH_SHORT
            ).show()
          } else {
            Toast.makeText(
              activity,
              resources.getString(R.string.failed_register_new_expense),
              Toast.LENGTH_LONG
            ).show()
          }
          finishHttpConnectionMenu(doneButton, nowLoading)
        }
      })
  }

  private fun inflateDate(yearDate: String?) {
    val dateList = yearDate?.split("/")
    calendarYear.text = dateList!![0]
    val concatDate = dateList[1] + "/" + dateList[2]
    calendarDate.text = concatDate
  }

  private fun showCommentDialog() {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val input = EditText(requireContext())
    input.setBackgroundResource(android.R.color.transparent)
    input.text = commentEditText.text

    builder?.setTitle(resources.getString(R.string.add_comment))
      ?.setPositiveButton(
        resources.getString(R.string.add)
      ) { _, _ ->
        commentEditText.setText(input.text.toString())
        commentEditText.visibility = EditText.VISIBLE
        commentText.visibility = TextView.VISIBLE
      }
      ?.setNegativeButton(resources.getString(R.string.cancel), null)
      ?.setView(input)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
  }

  private fun showTagDialog() {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val tagRecycleAdapter = AdapterTag()
    tagRecycleAdapter.tagList = tagList
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = GridLayoutManager(requireContext(), 3)
      adapter = tagRecycleAdapter
    }
    builder?.setView(recycleView)
      ?.setPositiveButton(resources.getString(R.string.done), null)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
  }

  private fun showCurrencyDialog() {
    val builder: AlertDialog.Builder? = requireActivity().let {
      AlertDialog.Builder(it)
    }
    val currencyRecycleAdapter = AdapterCurrency(AdapterCurrency.OnClickListener {
      currencyTextView.text = it.name
      Log.i(TAG, "showCurrencyDialog: called")
    })
    currencyRecycleAdapter.countryList = countryList
    val recycleView = RecyclerView(requireContext())
    recycleView.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = currencyRecycleAdapter
    }
    builder?.setView(recycleView)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
    currencyTextView.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dialog?.dismiss()
      }

      override fun afterTextChanged(s: Editable?) {
      }

    })
  }

  private fun goBackHome() {
    this.findNavController().navigate(
      AddExpenseFragmentDirections.actionAddExpenseFragmentToHomeFragment()
    )
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    doneButton = menu.getItem(0)
  }

  companion object {
    const val TAG = "AddExpenseFragment"
  }

}
