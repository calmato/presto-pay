package work.calmato.prestopay.ui.addFriend

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding
import work.calmato.prestopay.network.Users

class AddFriendFragment : Fragment() {
  private val viewModel = AddFriendViewModel()
  private lateinit var clickListener: AddFriendAdapter.OnClickListener
  private lateinit var viewManager: RecyclerView.LayoutManager
  private var usersList: Users? = null
  var idToken = ""
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_friend, container, false)
    binding.setLifecycleOwner(this)
    binding.viewModel = viewModel

    clickListener = AddFriendAdapter.OnClickListener { viewModel.displayDialog(it) }
    binding.usersRecycleView.adapter = AddFriendAdapter(usersList, clickListener)

    viewManager = LinearLayoutManager(requireContext())
    binding.usersRecycleView.apply {
      setHasFixedSize(true)
      layoutManager = viewManager
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val mUser = FirebaseAuth.getInstance().currentUser
    mUser?.getIdToken(true)?.addOnCompleteListener(requireActivity()) {
      idToken = it.result?.token!!
    }
    search.setOnClickListener {
      usersList = viewModel.getUserProperties(userName.text.toString(), idToken)
      usersList?.let {
        usersRecycleView.swapAdapter(AddFriendAdapter(usersList, clickListener), false)
      } ?: run {
        Toast.makeText(requireContext(), "ユーザーが見つかりません", Toast.LENGTH_SHORT).show()
        usersRecycleView.adapter = null
      }
    }
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setPositiveButton("追加する",
          DialogInterface.OnClickListener { dialog, id ->
            val isSuccess = viewModel.addFriendApi(it,idToken)
            if(isSuccess){
              Toast.makeText(requireContext(),"友だち追加しました",Toast.LENGTH_SHORT).show()
            }else{
              Toast.makeText(requireContext(),"友だち追加失敗",Toast.LENGTH_SHORT).show()
            }
          })
          ?.setNegativeButton("キャンセル",
            DialogInterface.OnClickListener { dialog, id ->
              // User cancelled the dialog
            })
          ?.setView(R.layout.dialog_add_friend)

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
        val name = dialog?.findViewById<TextView>(R.id.username_dialog)
        val thumbnail = dialog?.findViewById<ImageView>(R.id.thumbnail_dialog)
        name!!.text = it.name
        if (it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty()) {
          Picasso.with(context).load(it.thumbnailUrl).into(thumbnail)
        }
        viewModel.displayDialogCompleted()
      }
    })
  }
}
