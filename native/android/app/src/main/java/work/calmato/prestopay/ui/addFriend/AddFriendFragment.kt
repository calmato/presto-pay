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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_friend.*
import kotlinx.android.synthetic.main.fragment_add_friend.nowLoading
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.util.*

class AddFriendFragment : Fragment() {
  private val viewModel : ViewModelFriendGroup by lazy {
    ViewModelProvider(this).get(ViewModelFriendGroup::class.java)
  }
  private var recycleAdapter:AdapterFriendPlane? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.usersList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
      it?.apply {
        recycleAdapter?.friendList = it
      }
    })
  }

  private lateinit var clickListener: AdapterFriendPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentAddFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_friend, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    clickListener = AdapterFriendPlane.OnClickListener { viewModel.itemIsClicked(it) }
    recycleAdapter = AdapterFriendPlane(clickListener)
    binding.root.findViewById<RecyclerView>(R.id.usersRecycleView).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    searchFriend.setOnClickListener {
      val name = userName.text.toString()
      if (name.isEmpty()){
        Toast.makeText(requireContext(),resources.getString(R.string.fill_friend_username),Toast.LENGTH_SHORT).show()
      }else {
        viewModel.getUserProperties(name, requireActivity())
      }
    }
    viewModel.usersList.observe(viewLifecycleOwner, Observer {
        if (it.isEmpty()) {
          Toast.makeText(requireContext(), resources.getString(R.string.user_not_found), Toast.LENGTH_SHORT).show()
        }
    })
    viewModel.nowLoading.observe(viewLifecycleOwner, Observer {
      if(it){
        startHttpConnection(searchFriend,nowLoading,requireContext())
      }else{
        finishHttpConnection(searchFriend,nowLoading)
      }
    })
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer {
      if (null != it) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setTitle(resources.getString(R.string.add_friend_question))
          ?.setPositiveButton(resources.getString(R.string.add),
            DialogInterface.OnClickListener { _, _ ->
              viewModel.addFriendApi(it,requireActivity())
            })
          ?.setNegativeButton(resources.getString(R.string.cancel), null)
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
