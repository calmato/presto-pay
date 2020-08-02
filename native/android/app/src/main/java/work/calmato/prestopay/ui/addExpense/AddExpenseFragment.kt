package work.calmato.prestopay.ui.addExpense

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_expense.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddExpenseBindingImpl
import work.calmato.prestopay.network.CreateExpenseProperty
import work.calmato.prestopay.network.UserExpense
import work.calmato.prestopay.util.*

class AddExpenseFragment : PermissionBase() {
  val REQUEST_CODE = 11
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddExpenseBindingImpl = DataBindingUtil.inflate(
      inflater, R.layout.fragment_add_expense, container, false
    )
    return binding.root
  }
  private lateinit var imageIds:List<Int>
  private lateinit var tagNames: Array<String>
  private lateinit var tagList:MutableList<Tag>

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
      val selectedDate = data?.getStringExtra("selectedDate")
      val dateList = selectedDate?.split("/")
      calendarYear.text = dateList!![0]
      val concatDate = dateList[1] + "/" + dateList[2]
      calendarDate.text = concatDate
      calendar.visibility = ImageView.INVISIBLE
      calendarYear.visibility = TextView.VISIBLE
      calendarDate.visibility = TextView.VISIBLE
    }
    if (resultCode == Activity.RESULT_OK && requestCode == Constant.IMAGE_PICK_CODE) {
      thumbnail.setImageURI(data?.data)
      thumbnail.visibility = ImageView.VISIBLE
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    imageIds = resources.getIdList(R.array.tag_array)
    tagNames = resources.getStringArray(R.array.tag_name)
    tagList = mutableListOf<Tag>()
    for (i in tagNames.indices){
      tagList.add(Tag(tagNames[i],imageIds[i],false))
    }
    constraintDate.setOnClickListener {
      val datePickerFragment = DatePickerFragment()
      datePickerFragment.setTargetFragment(this,REQUEST_CODE)
      datePickerFragment.show(parentFragmentManager,"datePicker")
    }
    camera.setOnClickListener {
      requestPermission()
    }
    comment.setOnClickListener {
      val builder: AlertDialog.Builder? = requireActivity().let{
        AlertDialog.Builder(it)
      }
      val input = EditText(requireContext())
      input.setBackgroundResource(android.R.color.transparent)
      input.setText(commentEditText.text)

      builder?.setTitle("コメント追加")
        ?.setPositiveButton("追加",
        DialogInterface.OnClickListener{_,_ ->
          commentEditText.setText(input.text.toString())
          commentEditText.visibility = EditText.VISIBLE
          commentText.visibility = TextView.VISIBLE
        })
        ?.setNegativeButton("キャンセル", null)
        ?.setView(input)
      val dialog:AlertDialog? = builder?.create()
      dialog?.show()
    }
    advancedSetting.setOnClickListener {
      val advancedDialog = AdvancedExpenseFragment()
      advancedDialog.show(parentFragmentManager,"advancedExpense")
    }
    tagImage.setOnClickListener {
      val builder:AlertDialog.Builder? = requireActivity().let{
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
      val dialog:AlertDialog? = builder?.create()
      dialog?.show()
    }
    ArrayAdapter.createFromResource(requireContext(),R.array.currency_array,R.layout.spinner_item_currency)
      .also {
          adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        currencySpinner.adapter = adapter
      }
    setHasOptionsMenu(true)
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
  private fun sendRequest(){
    val name = expenseName.text.toString()
    val currency = currencySpinner.selectedItem.toString()
    val total = (amountEdit.text.toString()).toInt()
    val payers = emptyList<UserExpense>()
    val tags = mutableListOf<String>()
    for (i in tagList.filter { it.isSelected }){
      tags.add(i.name)
    }
    val paidAt = calendarYear.text.toString() + "/" + calendarDate.text.toString()
    val images = encodeImage2Base64(camera)
    val comment = commentEditText.text.toString()
    val expenseProperty = CreateExpenseProperty(
      name,currency,total,payers,tags,comment, listOf(images),paidAt
    )
    Log.i(TAG, "sendRequest: $expenseProperty")
    //TODO 日付を選択しない場合は現在の日にちにして、かつFirebase用にフォーマットする。
    //TODO ユーザーリストの作成
    //TODO　入力内容のベリフィケーション
  }
  companion object{
    val TAG = "AddExpenseFragment"
  }
}
