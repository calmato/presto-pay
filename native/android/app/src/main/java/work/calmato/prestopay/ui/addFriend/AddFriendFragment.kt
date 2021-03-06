package work.calmato.prestopay.ui.addFriend

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.util.AdapterUserNamePlane
import work.calmato.prestopay.util.ViewModelFriendGroup

class AddFriendFragment : Fragment() {
  private val viewModel: ViewModelFriendGroup by lazy {
    ViewModelProvider(this).get(ViewModelFriendGroup::class.java)
  }
  private var recycleAdapter: AdapterUserNamePlane? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.usersList.observe(viewLifecycleOwner, Observer<List<UserProperty>> {
      it?.apply {
        recycleAdapter?.friendList = it
      }
    })
  }

  private lateinit var clickListener: AdapterUserNamePlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentAddFriendBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_add_friend, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    clickListener = AdapterUserNamePlane.OnClickListener { viewModel.itemIsClicked(it) }
    recycleAdapter = AdapterUserNamePlane(clickListener)
    binding.root.findViewById<RecyclerView>(R.id.usersRecycleView).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.getDatabaseFriendList()
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
          if (newText.isNotEmpty()) {
            viewModel.getUserProperties(newText)
          }
        }
        return true
      }
    })
    viewModel.usersList.observe(viewLifecycleOwner, Observer {
      if (it.isEmpty()) {
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.user_not_found),
          Toast.LENGTH_SHORT
        ).show()
      }
    })
    viewModel.itemClicked.observe(viewLifecycleOwner, Observer { userProperty ->
      if (null != userProperty) {
        val builder: AlertDialog.Builder = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder.setTitle(resources.getString(R.string.add_friend_question))
          ?.setPositiveButton(resources.getString(R.string.add)
          ) { _, _ ->
            viewModel.addFriendApi(userProperty)
          }
          ?.setNegativeButton(resources.getString(R.string.cancel), null)
          ?.setView(R.layout.dialog_add_friend)
        val dialog: AlertDialog? = builder.create()
        dialog?.show()
        val name = dialog?.findViewById<TextView>(R.id.username_dialog)
        name!!.text = userProperty.username
        viewModel.itemIsClickedCompleted()
      }
    })
    viewModel.nowLoading.observe(viewLifecycleOwner, Observer {
      if (it){
        progressBarAddFriend.visibility = ProgressBar.VISIBLE
        frontViewAddFriend.visibility = ImageView.VISIBLE
      }else{
        progressBarAddFriend.visibility = ProgressBar.GONE
        frontViewAddFriend.visibility = ImageView.GONE
      }
    })
  }

  companion object {
    internal const val TAG = "AddFriendFragment"
  }
}
