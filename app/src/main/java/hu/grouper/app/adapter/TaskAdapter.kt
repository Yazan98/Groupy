package hu.grouper.app.adapter

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.data.models.Task
import io.vortex.android.utils.random.VortexBaseAdapter
import kotlinx.android.synthetic.main.row_task.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 7:36 PM
 */

class TaskAdapter : VortexBaseAdapter<TaskAdapter.Holder>() {

    private val list = ArrayList<Task>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getLayoutRes(): Int {
        return R.layout.row_task
    }

    override fun getViewHolder(view: View): Holder {
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.name?.let {
            it.text = list[position].name
        }

        holder.card?.let {
            when (list[position].status) {
                "NEW" -> it.setBackgroundResource(R.drawable.bg_failed)
                "IN_PROGRESS" -> it.setBackgroundResource(R.drawable.bg_progress)
                "DONE" -> it.setBackgroundResource(R.drawable.bg_success)
            }
        }

        holder.card?.apply {
            this.setOnClickListener {
                showDialog(position , holder)
            }
        }
    }

    private fun showDialog(position: Int , holder: Holder) {
        context.let {
            val updateDialog = Dialog(it)
            updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            updateDialog.setCancelable(true)
            updateDialog.setContentView(R.layout.dialog_tasks)
            val lWindowParams = WindowManager.LayoutParams()
            lWindowParams.copyFrom(updateDialog.window.attributes)
            lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            updateDialog.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.attributes = lWindowParams
            }

            updateDialog.findViewById<Button>(R.id.ChangeStatus)?.apply {
                this.setOnClickListener {
                    when {
                        updateDialog.findViewById<RadioButton>(R.id.NewButton).isChecked -> {
                            updateTaskStatus(list[position] , "NEW")
                            holder.card?.setBackgroundResource(R.drawable.bg_failed)
                        }
                        updateDialog.findViewById<RadioButton>(R.id.ProgressButton).isChecked -> {
                            updateTaskStatus(list[position] , "IN_PROGRESS")
                            holder.card?.setBackgroundResource(R.drawable.bg_progress)
                        }
                        else -> {
                            updateTaskStatus(list[position] , "DONE")
                            holder.card?.setBackgroundResource(R.drawable.bg_success)
                        }
                    }
                    updateDialog.dismiss()
                }
            }

        }
    }

    private fun updateTaskStatus(task: Task , status: String) {
        val data = HashMap<String , Any>()
        data["status"] = status
        task.id?.let {
            FirebaseFirestore.getInstance().collection("tasks")
                    .document(it).update(data)
        }
    }

    fun add(task: Task) {
        this.list.add(task)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.TaskName
        val card = view.Card
    }
}