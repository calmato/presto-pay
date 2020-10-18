package work.calmato.prestopay.ui.groupList

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentHiddenGroupListBinding
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.GroupPropertyResponse
import work.calmato.prestopay.network.HiddenGroups
import work.calmato.prestopay.util.AdapterGroupPlane
import work.calmato.prestopay.util.ViewModelGroup
import kotlin.concurrent.thread

class GroupListHiddenFragment : Fragment() {
  private val viewModelGroup: ViewModelGroup by lazy {
    ViewModelProvider(this).get(ViewModelGroup::class.java)
  }
  private var hiddenGroups: HiddenGroups? = null
  private var recycleGroupListAdapter: AdapterGroupPlane? = null
  private lateinit var id: String

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    thread {
      try {
        Api.retrofitService.getHiddenGroups("Bearer $id")
          .enqueue(object : Callback<HiddenGroups> {
            override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
              Log.d(ViewModelGroup.TAG, t.message)
            }

            override fun onResponse(call: Call<HiddenGroups>, response: Response<HiddenGroups>) {
              Log.d(TAG, response.body().toString())
              hiddenGroups = response.body()
              viewModelGroup.groupsList.observe(
                viewLifecycleOwner,
                Observer<List<GroupPropertyResponse>> {
                  recycleGroupListAdapter?.groupList = hiddenGroups?.hiddenGroupList!!
                })
              hiddenGroups?.hiddenGroupList
            }
          })
      } catch (e: Exception) {
        Log.d(TAG, "debug $e")
      }
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
  }

  private lateinit var clickListener: AdapterGroupPlane.OnClickListener

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentHiddenGroupListBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_hidden_group_list, container, false)
    clickListener = AdapterGroupPlane.OnClickListener { viewModelGroup.itemIsClickedGroup(it) }
    recycleGroupListAdapter = AdapterGroupPlane(clickListener)

    binding.root.findViewById<RecyclerView>(R.id.groupHiddenRecycle).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupListAdapter
    }
    return binding.root
  }

  companion object {
    const val TAG = "HiddenGroupListFragment"
  }
}
