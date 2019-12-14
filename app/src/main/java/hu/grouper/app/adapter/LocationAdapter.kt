package hu.grouper.app.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_location.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/14/2019
 * Time : 12:48 PM
 */

class LocationAdapter(private val listener: LocationListener) : VortexBaseAdapter<LocationAdapter.Holder>() {

    val items = ArrayList<Profile>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_location
    }

    override fun getViewHolder(view: View): Holder {
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.name?.let {
            it.text = items[position].name
        }

        holder.item?.let {
            it.setOnClickListener {
                listener.onCLick(items[position].lat , items[position].lng)
            }
        }
    }

    fun add(profile: Profile) {
        this.items.add(profile)
        notifyDataSetChanged()
    }

    interface LocationListener {
        fun onCLick(lat: Double? , lng: Double?)
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.Name
        val item = view.Location
    }
}