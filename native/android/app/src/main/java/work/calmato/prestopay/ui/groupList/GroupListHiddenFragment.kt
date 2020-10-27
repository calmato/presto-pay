package work.calmato.prestopay.ui.groupList

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentHiddenGroupListBinding
import work.calmato.prestopay.network.Api
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
              Log.d(TAG, hiddenGroups.toString())
              recycleGroupListAdapter?.groupList = hiddenGroups?.hiddenGroups!!
              recycleGroupListAdapter?.notifyDataSetChanged()
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

    binding.lifecycleOwner = this
    binding.viewModelGroup = viewModelGroup

    clickListener = AdapterGroupPlane.OnClickListener { viewModelGroup.itemIsClickedGroup(it) }
    recycleGroupListAdapter = AdapterGroupPlane(clickListener)

    binding.root.findViewById<RecyclerView>(R.id.groupHiddenRecycle).apply {
      layoutManager = LinearLayoutManager(context)
      adapter = recycleGroupListAdapter
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModelGroup.itemClickedGroup.observe(viewLifecycleOwner, Observer {
      if (it != null) {
        val builder: AlertDialog.Builder? = requireActivity().let {
          AlertDialog.Builder(it)
        }
        builder?.setView(R.layout.dialog_add_friend)
          ?.setPositiveButton(resources.getString(R.string.delete_hidden_group),
            DialogInterface.OnClickListener { _, _ ->
              val builder2: AlertDialog.Builder? = requireActivity().let {
                AlertDialog.Builder(it)
              }
              builder2?.setMessage(resources.getString(R.string.delete_question))
                ?.setPositiveButton(
                  resources.getString(R.string.delete),
                  DialogInterface.OnClickListener { _, _ ->
                    Api.retrofitService.deleteHiddenGroup("Bearer ${id}", it.id)
                      .enqueue(object : Callback<HiddenGroups> {
                        override fun onResponse(call: Call<HiddenGroups>, response: Response<HiddenGroups>) {
                          Log.d(ViewModelGroup.TAG, response.body().toString())
                          hiddenGroups = response.body()
                          Log.d(ViewModelGroup.TAG, hiddenGroups.toString())
                        }

                        override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
                          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                          Log.d(ViewModelGroup.TAG, t.message)
                        }
                      })
                    viewModelGroup.deleteHiddenGroup(it.id, requireActivity())
                  })
                ?.setNegativeButton(resources.getString(R.string.cancel), null)
              val dialog2: AlertDialog? = builder2?.create()
              dialog2?.show()
            })
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
        val name = dialog?.findViewById<TextView>(R.id.username_dialog)
        val thumbnail = dialog?.findViewById<ImageView>(R.id.thumbnail_dialog)
        name!!.text = it.name
        if (it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty()) {
          Picasso.with(context).load(it.thumbnailUrl).into(thumbnail)
        }
        viewModelGroup.itemIsClickedCompleted()
      }
    })
  }

  companion object {
    const val TAG = "HiddenGroupListFragment"
  }
}
