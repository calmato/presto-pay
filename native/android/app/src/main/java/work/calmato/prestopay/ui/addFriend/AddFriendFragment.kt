package work.calmato.prestopay.ui.addFriend

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding
import work.calmato.prestopay.util.AdapterRecyclePlane
import work.calmato.prestopay.util.ViewModelFriendGroup

class AddFriendFragment : Fragment() {
  private val viewModel : ViewModelFriendGroup by lazy {
    val activity = requireNotNull(this.activity){
      "You can only access the viewModel after onActivityCreated()"
    }
    ViewModelProviders.of(this,ViewModelFriendGroup.Factory(activity.application))
      .get(ViewModelFriendGroup::class.java)
  }
  private lateinit var clickListener: AdapterRecyclePlane.OnClickListener
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
    clickListener = AdapterRecyclePlane.OnClickListener { viewModel.itemIsClicked(it) }
    binding.usersRecycleView.adapter = AdapterRecyclePlane(null, clickListener)

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
      viewModel.getUserProperties(userName.text.toString(),requireActivity())
    }
    viewModel.usersList.observe(viewLifecycleOwner, Observer {
      it?.let {
        usersRecycleView.swapAdapter(
          AdapterRecyclePlane(it, clickListener), false
        )
        if (it.users.isEmpty()) {
          Toast.makeText(requireContext(), "ユーザーが見つかりません", Toast.LENGTH_SHORT).show()
        }
      }
    })
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setTitle("友だち追加しますか？")
          ?.setPositiveButton("追加する",
            DialogInterface.OnClickListener { _, _ ->
              viewModel.addFriendApi(it,requireActivity())
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
  companion object {
    internal const val TAG = "AddFriendFragment"
  }
}
