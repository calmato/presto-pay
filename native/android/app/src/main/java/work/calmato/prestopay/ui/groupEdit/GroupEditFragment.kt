package work.calmato.prestopay.ui.groupEdit

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_group_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentGroupEditBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GetGroupDetail
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.util.ViewModelGroup
import kotlin.concurrent.thread

class GroupEditFragment : Fragment() {
  private var getGroupInfo: GroupPropertyResponse? = null
  private var groupDetail: GetGroupDetail? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentGroupEditBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_group_edit, container, false)
    getGroupInfo = GroupEditFragmentArgs.fromBundle(requireArguments()).groupEditList
    return binding.root
  }

  private lateinit var id: String

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    thread {
      try {
        Api.retrofitService.getGroupDetail("Bearer $id", getGroupInfo!!.id)
          .enqueue(object : Callback<GetGroupDetail> {
            override fun onFailure(call: Call<GetGroupDetail>, t: Throwable) {
              Log.d(ViewModelGroup.TAG, t.message)
            }

            override fun onResponse(
              call: Call<GetGroupDetail>,
              response: Response<GetGroupDetail>
            ) {
              Log.d(ViewModelGroup.TAG, response.body().toString())
              groupDetail = response.body()
              groupEditName.setText(groupDetail?.name)
            }
          })
      } catch (e: Exception) {
        Log.d(TAG, "debug $e")
      }
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
  }

  companion object {
    const val TAG = "GroupEditFragment"
  }
}
