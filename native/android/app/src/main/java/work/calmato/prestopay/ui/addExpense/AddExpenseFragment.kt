package work.calmato.prestopay.ui.addExpense

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_add_expense.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddExpenseBindingImpl
import work.calmato.prestopay.network.*
import work.calmato.prestopay.util.*
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment() : PermissionBase() {
  private var groupsList: Groups? = null
  private var checkedGroup: Groups? = null
  private var getGroupInfo: GroupPropertyResponse? = null

  val REQUEST_CODE = 11

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view: View = inflater.inflate(R.layout.fragment_add_expense, container, false)

    val binding: FragmentAddExpenseBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_add_expense, container, false
    )

    groupsList = AddExpenseFragmentArgs.fromBundle(requireArguments()).groupsList
    checkedGroup =
      Groups(groupsList!!.groups.filter { groupPropertyResponse -> groupPropertyResponse.selected })
    checkedGroup.let {
      if (it != null) {
        getGroupInfo = it.groups[0]
      }
    }
    return binding.root
  }

  private lateinit var imageIds: List<Int>
  private lateinit var tagNames: Array<String>
  private lateinit var tagList: MutableList<Tag>
  private lateinit var id: String
  private lateinit var doneButton: MenuItem

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    groupName.text = getGroupInfo!!.name
    imageIds = resources.getIdList(R.array.tag_array)
    tagNames = resources.getStringArray(R.array.tag_name)
    tagList = mutableListOf<Tag>()
    for (i in tagNames.indices) {
      tagList.add(Tag(tagNames[i], imageIds[i], false))
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!

    val c = Calendar.getInstance()
    inflateDate(SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(c.time))

    constraintDate.setOnClickListener {
      val datePickerFragment = DatePickerFragment()
      datePickerFragment.setTargetFragment(this, REQUEST_CODE)
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
    ArrayAdapter.createFromResource(
      requireContext(),
      R.array.currency_array,
      R.layout.spinner_item_currency
    )
      .also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        currencySpinner.adapter = adapter
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
    val groupId = "618ff440-e663-4fcb-a519-aaf81d017535"
    val name = expenseName.text.toString()
    val totalString = amountEdit.text.toString()
    val currency = currencySpinner.selectedItem.toString()
    val payers = listOf<UserExpense>(
      UserExpense("30TqoiY59wb9aZwdzqwrE4fwim42", 250.1f),
      UserExpense("58cf47e8-cf77-4c92-88bc-48deee452208", -250.1f)
    )
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
        for (i in payers.filter { it.amount > 0 }) {
          sumPayment += i.amount
        }
        if (total == sumPayment) {
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
    Api.retrofitService.addExpense("Bearer ${id}", expenseProperty, groupId)
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
    input.setText(commentEditText.text)

    builder?.setTitle(resources.getString(R.string.add_comment))
      ?.setPositiveButton(resources.getString(R.string.add),
        DialogInterface.OnClickListener { _, _ ->
          commentEditText.setText(input.text.toString())
          commentEditText.visibility = EditText.VISIBLE
          commentText.visibility = TextView.VISIBLE
        })
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
      layoutManager = LinearLayoutManager(requireContext())
      adapter = tagRecycleAdapter
    }
    builder?.setView(recycleView)
    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
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
    val TAG = "AddExpenseFragment"
  }
}
