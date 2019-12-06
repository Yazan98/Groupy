package hu.grouper.app.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hu.grouper.app.R
import hu.grouper.app.data.models.Group
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_group.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 1:38 PM
 */

class GroupsAdapter : VortexBaseAdapter<GroupsAdapter.Holder>() {

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

            }
        }
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