package hu.grouper.app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_profile.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 2:09 PM
 */

class MembersAdapter : VortexBaseAdapter<MembersAdapter.Holder>() {

    private val items = ArrayList<Profile>()
    val mainSelected = ArrayList<Profile>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_profile
    }

    override fun getViewHolder(view: View): Holder {
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var selected = false
        holder.bio?.let {
            it.text = items[position].bio
        }

        holder.name?.let {
            it.text = items[position].name
        }

        holder.container?.apply {
            this.setOnClickListener {
                selected = if (selected == false) {
                    mainSelected.add(items[position])
                    holder.container?.setBackgroundResource(R.drawable.bg_selected)
                    true
                } else {
                    mainSelected.remove(items[position])
                    holder.container?.setBackgroundResource(R.drawable.bg_default)
                    false
                }
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