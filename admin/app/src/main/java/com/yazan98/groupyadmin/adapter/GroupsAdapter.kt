package com.yazan98.groupyadmin.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.models.Group
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_group_info.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupsAdapter(private val listener: GroupListener) :
    VortexBaseAdapter<GroupsAdapter.Holder>() {

    private val items = ArrayList<Group>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_group_info
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
                it.setBackgroundResource(R.drawable.bg_success)
            }
        } else {
            holder.Card?.let {
                it.setBackgroundResource(R.drawable.bg_failed)
            }
        }

        holder.Card?.let {
            it.setOnClickListener {
                listener?.onGroupSuccess(items[position])
            }
        }
    }

    interface GroupListener {
        fun onGroupSuccess(group: Group)
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