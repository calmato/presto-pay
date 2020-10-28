package work.calmato.prestopay.ui.groupList

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_hidden_group_list.*
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
      renderGroupListView()
/*      try {
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
      }*/
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    id = sharedPreferences.getString("token", "")!!
    val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(recycleGroupListAdapter!!)
    swipeToDismissTouchHelper.attachToRecyclerView(groupHiddenRecycle)
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

    // Groupの削除click ver
/*    viewModelGroup.itemClickedGroup.observe(viewLifecycleOwner, Observer {
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
                          renderGroupListView()
                        }

                        override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
                          Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                          Log.d(ViewModelGroup.TAG, t.message)
                        }
                      })
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
    })*/
  }

  private fun getSwipeToDismissTouchHelper(adapter: RecyclerView.Adapter<AdapterGroupPlane.AddGroupViewHolder>) =
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.LEFT,
      ItemTouchHelper.LEFT
    ) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        return false
      }

      //スワイプ時に実行
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // ここを変更すれば他でも対応可能
        Api.retrofitService.deleteHiddenGroup("Bearer ${id}", hiddenGroups!!.hiddenGroups[viewHolder.adapterPosition].id)
          .enqueue(object : Callback<HiddenGroups> {
            override fun onResponse(call: Call<HiddenGroups>, response: Response<HiddenGroups>) {
              Log.d(ViewModelGroup.TAG, response.body().toString())
              hiddenGroups = response.body()
              Log.d(ViewModelGroup.TAG, hiddenGroups.toString())
              renderGroupListView()
            }

            override fun onFailure(call: Call<HiddenGroups>, t: Throwable) {
              Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
              Log.d(ViewModelGroup.TAG, t.message)
            }
          })
      }

      //スワイプした時の背景を設定
      override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
      ) {
        super.onChildDraw(
          c,
          recyclerView,
          viewHolder,
          dX,
          dY,
          actionState,
          isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val background = ColorDrawable(Color.RED)
        val deleteIcon = AppCompatResources.getDrawable(
          requireContext(),
          R.drawable.ic_baseline_delete_sweep_24
        )
        val iconMarginVertical =
          (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2

        deleteIcon.setBounds(
          itemView.left + iconMarginVertical,
          itemView.top + iconMarginVertical,
          itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
          itemView.bottom - iconMarginVertical
        )
        background.setBounds(
          itemView.left,
          itemView.top,
          itemView.right + dX.toInt(),
          itemView.bottom
        )
        background.draw(c)
        deleteIcon.draw(c)
      }

    })

  fun renderGroupListView() {
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

  companion object {
    const val TAG = "HiddenGroupListFragment"
  }
}
