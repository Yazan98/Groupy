package com.yazan98.groupyadmin.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.models.Profile
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_profile_2.view.*

class HomeMembersAdapter(private val listener: HomeMembersAdapter.Listener) : VortexBaseAdapter<HomeMembersAdapter.Holder>() {

    val items = ArrayList<Profile>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_profile_2
    }

    override fun getViewHolder(view: View): Holder {
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bio?.let {
            it.text = items[position].bio
        }

        holder.name?.let {
            it.text = items[position].name
        }

        holder.container?.apply {
            this.setOnClickListener {
                listener.onClick(items[position])
            }
        }
    }

    fun add(profile: Profile) {
        this.items.add(profile)
        notifyDataSetChanged()
    }

    interface Listener {
        fun onClick(profile: Profile)
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val bio = view.BioF
        val name = view.textView2
        val container = view.ProfileContainer
    }
}