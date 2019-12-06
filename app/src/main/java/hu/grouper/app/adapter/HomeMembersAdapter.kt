package hu.grouper.app.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_profile.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 6:40 PM
 */

class HomeMembersAdapter : VortexBaseAdapter<HomeMembersAdapter.Holder>() {

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

            }
        }
    }

    fun add(profile: Profile) {
        this.items.add(profile)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val bio = view.BioF
        val name = view.textView2
        val container = view.ProfileContainer
    }
}