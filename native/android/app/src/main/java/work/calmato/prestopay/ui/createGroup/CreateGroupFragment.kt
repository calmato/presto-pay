package work.calmato.prestopay.ui.createGroup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_create_group.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentCreateGroupBinding
import work.calmato.prestopay.network.CreateGroupProperty
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.AdapterGrid
import work.calmato.prestopay.util.Constant.Companion.IMAGE_PICK_CODE
import work.calmato.prestopay.util.Constant.Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import work.calmato.prestopay.util.ViewModelFriendGroup
import work.calmato.prestopay.util.encodeImage2Base64

class CreateGroupFragment : Fragment() {
  var setThumbnail = false
  var idToken = ""
  private var usersList: Users? = null
  private var usersListToBeSent: Users? = null
  private val viewModel = ViewModelFriendGroup()
  private lateinit var recycleAdapter: AdapterGrid
  private lateinit var clickListener: AdapterGrid.OnClickListener
  private lateinit var viewManager: RecyclerView.LayoutManager


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentCreateGroupBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_create_group, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    viewModel.getIdToken()
    clickListener = AdapterGrid.OnClickListener { viewModel.itemIsClicked(it) }
    usersList = CreateGroupFragmentArgs.fromBundle(requireArguments()).friendsList
    usersListToBeSent = Users(usersList!!.users.filter { userProperty -> userProperty!!.checked })
    recycleAdapter = AdapterGrid(usersListToBeSent, clickListener)
    binding.GridViewFriends.adapter = recycleAdapter
    viewManager = LinearLayoutManager(requireContext())
    binding.GridViewFriends.apply {
      setHasFixedSize(true)
    }
    return binding.root
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendGroupInfo()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendGroupInfo() {
    var thumbnailStr = encodeImage2Base64(thumbnailEdit)
    if (setThumbnail) {
      thumbnailStr = encodeImage2Base64(thumbnailEdit)
    }
    val groupName = groupName.text.toString()
    if (groupName.length in 1..31) {
      if (usersListToBeSent!!.users.size in 0..100) {
        val groupProperty =
          CreateGroupProperty(groupName, thumbnailStr, usersListToBeSent!!.users.map { it!!.id })
        viewModel.createGroupApi(groupProperty,requireActivity())
      } else {
        Toast.makeText(requireContext(), "グループメンバーは100人までです", Toast.LENGTH_LONG).show()
      }
    } else {
      Toast.makeText(requireContext(), "1〜31文字のグループ名を入力してください", Toast.LENGTH_LONG).show()
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    thumbnailEdit.setOnClickListener {
      //check runtime permission
      if (ContextCompat.checkSelfPermission(
          requireActivity(),
          Manifest.permission.READ_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED
      ) {
        // Permission is not granted
        if (ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
          )
        ) {
          Toast.makeText(requireActivity(), "設定からギャラリーへのアクセスを許可してください", Toast.LENGTH_LONG).show()
        } else {
          // No explanation needed, we can request the permission.
          ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
          )
        }
      } else {
        //permission already granted
        pickImageFromGallery()
      }
    }
    viewModel.navigateToHome.observe(viewLifecycleOwner, Observer{
      if(it){
        this.findNavController().navigate(
          CreateGroupFragmentDirections.actionCreateGroupFragmentToHomeFragment()
        )
        viewModel.navigationCompleted()
      }
    })
    setHasOptionsMenu(true)
    val mUser = FirebaseAuth.getInstance().currentUser
    mUser?.getIdToken(true)?.addOnCompleteListener(requireActivity()) {
      idToken = it.result?.token!!
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          reChooseMember()
        }

      }
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.header_done, menu)

  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          pickImageFromGallery()
        } else {
          Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()
        }
        return
      }
      else -> {
        // Ignore all other requests.
      }
    }
  }

  private fun pickImageFromGallery() {
    //Intent to pick image
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(
      intent,
      IMAGE_PICK_CODE
    )
    setThumbnail = true
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
      thumbnailEdit.setImageURI(data?.data)
      thumbnailEdit.setBackgroundColor(Color.TRANSPARENT)
    }
  }

  private fun reChooseMember() {
    this.findNavController().navigate(
      CreateGroupFragmentDirections.actionCreateGroupFragmentToFriendListFragment(usersList)
    )
  }

  companion object {
    internal const val TAG = "CreateGroupFragment"
  }
}
