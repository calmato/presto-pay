package work.calmato.prestopay.ui.addFriend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding
import work.calmato.prestopay.network.UserProperty
import work.calmato.prestopay.network.Users

class AddFriendFragment : Fragment() {
  private val viewModel = AddFriendViewModel()
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
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
    viewAdapter = AddFriendAdapter(usersList)
    viewManager = LinearLayoutManager(requireContext())
    binding.usersRecycleView.apply {
      setHasFixedSize(true)
      layoutManager = viewManager
      adapter = viewAdapter
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
      usersList = viewModel.getUserProperties(userName.text.toString(), idToken)!!
      if (usersList!!.users.isEmpty()) {
        Toast.makeText(requireContext(), "ユーザーが見つかりません", Toast.LENGTH_SHORT).show()
      }
      usersRecycleView.swapAdapter(AddFriendAdapter(usersList), false)
    }
  }

  companion object {
    internal const val TAG = "AddFriendFragment"
  }
}
