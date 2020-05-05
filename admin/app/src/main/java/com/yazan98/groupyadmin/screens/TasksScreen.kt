package com.yazan98.groupyadmin.screens


import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.adapter.TaskAdapter
import com.yazan98.groupyadmin.models.Task
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.screen_tasks.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksScreen : VortexBaseFragment() {

    private val list = TaskAdapter()

    override fun getLayoutRes(): Int {
        return R.layout.screen_tasks
    }

    override fun initScreen(view: View) {
        attachmentButton?.setOnClickListener {
            GlobalScope.launch {
                println("SSSTART")
                startScreen<NewTaskScreen>(false)
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
            FirebaseFirestore.getInstance().collection("tasks")
                .whereEqualTo("groupId", groupID)
                .get().addOnCompleteListener {
                    it.result?.let {
                        for (doc in it.documents) {
                            FirebaseFirestore.getInstance().collection("tasks")
                                .document(doc.id).get().addOnCompleteListener {
                                    it.result?.let {
                                        list.add(
                                            Task(
                                                id = it.getString("id"),
                                                userId = it.getString("userId"),
                                                status = it.getString("status"),
                                                groupId = it.getString("groupId"),
                                                name = it.getString("name")
                                            )
                                        )
                                    }
                                }
                        }
                    }
                }
        }
    }


    override fun onStop() {
        super.onStop()
        list.list.clear()
    }
}