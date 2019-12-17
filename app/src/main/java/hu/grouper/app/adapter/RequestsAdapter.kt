package hu.grouper.app.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.data.models.JoinRequest
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_request.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/17/2019
 * Time : 6:56 PM
 */

class RequestsAdapter : VortexBaseAdapter<RequestsAdapter.Holder>() {
    private val data: ArrayList<JoinRequest> = ArrayList()
    override fun getItemCount(): Int {
        return data.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_request
    }

    override fun getViewHolder(view: View): Holder {
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.name?.let {
            it.text = data[position].name
        }

        holder.no?.apply {
            this.setOnClickListener {
                data[position].docuementId?.let {
                    FirebaseFirestore.getInstance().collection("requests")
                            .document(it).delete()
                    data.remove(data[position])
                    notifyDataSetChanged()
                    Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.yes?.apply {
            this.setOnClickListener {
                data[position].userId?.let {
                    val result = HashMap<String, Any>()
                    result.put("groupID", data[position].groupID!!)
                    FirebaseFirestore.getInstance().collection("users")
                            .document(it).update(result)

                    Toast.makeText(context, "User Has Been Accepted", Toast.LENGTH_SHORT).show()
                    data.remove(data[position])
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val yes = view.YesButton
        val no = view.NoButton
        val name = view.NameRequest
    }

    fun add(request: JoinRequest) {
        this.data.add(request)
        notifyDataSetChanged()
    }
}