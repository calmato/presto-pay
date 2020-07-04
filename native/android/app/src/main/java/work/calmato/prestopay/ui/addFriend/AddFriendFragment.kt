package work.calmato.prestopay.ui.addFriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_friend.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentAddFriendBinding

class AddFriendFragment:Fragment() {
  private val viewModel = AddFriendViewModel()
  var idToken = ""
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = FragmentAddFriendBinding.inflate(inflater)
    binding.setLifecycleOwner(this)
    binding.viewModel = viewModel
    return inflater.inflate(R.layout.fragment_add_friend, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val mUser = FirebaseAuth.getInstance().currentUser
    mUser?.getIdToken(true)?.addOnCompleteListener(requireActivity()){
      idToken = it.result?.token!!
    }
    search.setOnClickListener {
      viewModel.getUserProperties(userName.text.toString(),idToken)
    }
  }
}
