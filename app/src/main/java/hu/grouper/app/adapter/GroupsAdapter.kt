package hu.grouper.app.adapter

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.data.JoinRequest
import hu.grouper.app.data.models.Group
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_group.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 1:38 PM
 */

class GroupsAdapter(private val listener: GroupListener) : VortexBaseAdapter<GroupsAdapter.Holder>() {

    private val items = ArrayList<Group>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_group
    }

    override fun getViewHolder(view: View): Holder {
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.Name?.let {
            it.text = items[position].name
        }

        if (items[position].members?.size!! < 4) {
            holder.Card?.let {
                it.setBackgroundResource(R.drawable.bg_failed)
            }
        } else {
            holder.Card?.let {
                it.setBackgroundResource(R.drawable.bg_success)
            }
        }

        holder.Card?.let {
            it.setOnClickListener {
                if (items[position].members?.size!! < 4) {
                    Toast.makeText(context, "This Group Is Available", Toast.LENGTH_SHORT).show()
                    GlobalScope.launch {
                        showGroupDialog(position)
                    }
                } else {
                    Toast.makeText(context, "This Group Is Closed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun showGroupDialog(position: Int) {
        withContext(Dispatchers.Main) {
            context?.let {
                val updateDialog = Dialog(it)
                updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                updateDialog.setCancelable(true)
                updateDialog.setContentView(R.layout.dialog_add_user_to_group)
                val lWindowParams = WindowManager.LayoutParams()
                lWindowParams.copyFrom(updateDialog.window.attributes)
                lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT; // this is where the magic happens
                lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                updateDialog.window?.let {
                    it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    it.attributes = lWindowParams
                }

                updateDialog.findViewById<ImageView>(R.id.YesButton)?.apply {
                    this.setOnClickListener {
                        updateDialog.dismiss()
                        GlobalScope.launch {
                            sendRequest(position)
                        }
                    }
                }

                updateDialog.findViewById<ImageView>(R.id.NoButton)?.apply {
                    this.setOnClickListener {
                        updateDialog.dismiss()
                    }
                }

                updateDialog.show()
            }
        }
    }

    private suspend fun sendRequest(position: Int) {
        withContext(Dispatchers.IO) {
            val name = VortexPrefs.get("Name", "") as String
            val userId = VortexPrefs.get("UserID", "") as String
            val id = UUID.randomUUID().toString()
            FirebaseFirestore.getInstance().collection("requests")
                    .document().set(
                    JoinRequest(
                        id = id,
                        name = name,
                        userId = userId,
                        groupID = items[position].id
                    )
                ).addOnCompleteListener {
                        GlobalScope.launch {
                            listener.onGroupSuccess()
                        }
                    }
        }
    }

    interface GroupListener {
        suspend fun onGroupSuccess()
    }

    fun addGroup(group: Group) {
        this.items.add(group)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val Name = view.Name
        val Card = view.Card
    }
}