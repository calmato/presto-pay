package work.calmato.prestopay.ui.addFriend

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding
import work.calmato.prestopay.util.RecycleAdapterUser
import work.calmato.prestopay.util.ViewModelFriendGroup

class AddFriendFragment : Fragment() {
  private val viewModel = ViewModelFriendGroup()
  private lateinit var clickListener: RecycleAdapterUser.OnClickListener
  private lateinit var viewManager: RecyclerView.LayoutManager

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_friend, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    viewModel.getIdToken()
    clickListener = RecycleAdapterUser.OnClickListener { viewModel.itemIsClicked(it) }
    binding.usersRecycleView.adapter = RecycleAdapterUser(null, clickListener, CheckBox.GONE)

    viewManager = LinearLayoutManager(requireContext())
    binding.usersRecycleView.apply {
      setHasFixedSize(true)
      layoutManager = viewManager
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    search.setOnClickListener {
      val usersList = viewModel.getUserProperties(userName.text.toString())
      usersList?.let {
        usersRecycleView.swapAdapter(
          RecycleAdapterUser(usersList, clickListener, CheckBox.GONE), false
        )
        if (it.users.isEmpty()) {
          Toast.makeText(requireContext(), "ユーザーが見つかりません", Toast.LENGTH_SHORT).show()
        }
      }
    }
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setTitle("友だち追加しますか？")
          ?.setPositiveButton("追加する",
            DialogInterface.OnClickListener { dialog, id ->
              val isSuccess = viewModel.addFriendApi(it)
              if (isSuccess) {
                Toast.makeText(requireContext(), "友だち追加しました", Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(requireContext(), "友だち追加失敗しました", Toast.LENGTH_SHORT).show()
              }
            })
          ?.setNegativeButton("キャンセル", null)
          ?.setView(R.layout.dialog_add_friend)
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
        val name = dialog?.findViewById<TextView>(R.id.username_dialog)
        val thumbnail = dialog?.findViewById<ImageView>(R.id.thumbnail_dialog)
        name!!.text = it.name
        if (it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty()) {
          Picasso.with(context).load(it.thumbnailUrl).into(thumbnail)
        }
        viewModel.itemIsClickedCompleted()
      }
    })
  }
}
