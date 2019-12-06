package hu.grouper.app.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.TaskAdapter
import hu.grouper.app.data.models.Task
import hu.grouper.app.screens.NewTaskScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 5:43 PM
 */

class TasksFragment : VortexBaseFragment() {

    private val list = TaskAdapter()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_tasks
    }

    @SuppressLint("WrongConstant")
    override fun initScreen(view: View) {

        GlobalScope.launch {
            VortexPrefs.get("AccountType", "")?.also {
                (it as String).let {
                    if (it.equals("ADMIN")) {
                        AddNewTask?.let {
                            it.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        activity?.let {
            TasksRecyclerView?.apply {
                this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                this.adapter = list
                (this.adapter as TaskAdapter).context = it
            }
        }

        GlobalScope.launch {
            val groupID = VortexPrefs.get("GroupID", "") as String
            val type = VortexPrefs.get("AccountType", "") as String
            val userID = VortexPrefs.get("UserID", "") as String
            FirebaseFirestore.getInstance().collection("tasks")
                    .whereEqualTo("groupId", groupID)
                    .get().addOnCompleteListener {
                        it.result?.let {
                            for (doc in it.documents) {
                                FirebaseFirestore.getInstance().collection("tasks")
                                        .document(doc.id).get().addOnCompleteListener {
                                            it.result?.let {
                                                if (type.equals("ADMIN")) {
                                                    list.add(Task(
                                                            id = it.getString("id"),
                                                            userId = it.getString("userId"),
                                                            status = it.getString("status"),
                                                            groupId = it.getString("groupId"),
                                                            name = it.getString("name")
                                                    ))
                                                } else {
                                                    if (userID.equals(it.getString("userId"))) {
                                                        list.add(Task(
                                                                id = it.getString("id"),
                                                                userId = it.getString("userId"),
                                                                status = it.getString("status"),
                                                                groupId = it.getString("groupId"),
                                                                name = it.getString("name")
                                                        ))
                                                    }
                                                }
                                            }
                                        }
                            }
                        }
                    }
        }



        AddNewTask?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    startScreen<NewTaskScreen>(false)
                }
            }
        }
    }
}