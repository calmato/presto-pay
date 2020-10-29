package work.calmato.prestopay.ui.createGroup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_create_group.*
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentCreateGroupBinding
import work.calmato.prestopay.network.CreateGroupProperty
import work.calmato.prestopay.network.Users
import work.calmato.prestopay.util.*
import work.calmato.prestopay.util.Constant.Companion.IMAGE_PICK_CODE

class CreateGroupFragment : PermissionBase() {
  private var usersList: Users? = null
  private var usersListToBeSent: Users? = null
  private val viewModel: ViewModelFriendGroup by lazy {
    ViewModelProvider(this).get(ViewModelFriendGroup::class.java)
  }
  private lateinit var recycleAdapter: AdapterGrid
  private lateinit var clickListener: AdapterGrid.OnClickListener
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var doneButton: MenuItem


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentCreateGroupBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_create_group, container, false)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    clickListener = AdapterGrid.OnClickListener { viewModel.itemIsClicked(it) }
    usersList = CreateGroupFragmentArgs.fromBundle(requireArguments()).friendsList
    usersListToBeSent = Users(usersList!!.users.filter { userProperty -> userProperty.checked })
    recycleAdapter = AdapterGrid(usersListToBeSent, clickListener)
    binding.GridViewFriends.adapter = recycleAdapter
    viewManager = LinearLayoutManager(requireContext())
    binding.GridViewFriends.apply {
      setHasFixedSize(true)
    }
    return binding.root
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    doneButton = menu.getItem(0)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.done -> sendGroupInfo()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun sendGroupInfo() {
    val thumbnailStr = encodeImage2Base64(thumbnailEdit)
    val groupName = groupName.text.toString()
    if (groupName.length in 1..31) {
      if (usersListToBeSent!!.users.size in 0..100) {
        val groupProperty =
          CreateGroupProperty(groupName, thumbnailStr, usersListToBeSent!!.users.map { it.id })
        viewModel.createGroupApi(groupProperty, requireActivity())
      } else {
        Toast.makeText(
          requireContext(),
          resources.getString(R.string.group_member_restriction),
          Toast.LENGTH_LONG
        ).show()
      }
    } else {
      Toast.makeText(
        requireContext(),
        resources.getString(R.string.group_name_restriction),
        Toast.LENGTH_LONG
      ).show()
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    thumbnailEdit.setOnClickListener {
      requestPermission()
    }
    viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
      if (it) {
        this.findNavController().navigate(
          CreateGroupFragmentDirections.actionCreateGroupFragmentToHomeFragment()
        )
        viewModel.navigationCompleted()
      }
    })
    setHasOptionsMenu(true)

    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          reChooseMember()
        }
      }
    )
    viewModel.nowLoading.observe(viewLifecycleOwner, Observer {
      if (it) {
        startHttpConnectionMenu(doneButton, nowLoading, requireContext())
      } else {
        finishHttpConnectionMenu(doneButton, nowLoading)
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.header_done, menu)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
      cropImage(data?.data!!)
    }
    if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
      val resultUri = UCrop.getOutput(data!!)
      thumbnailEdit.setImageURI(resultUri)
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
