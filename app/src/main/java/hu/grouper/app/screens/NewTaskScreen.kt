package hu.grouper.app.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.data.models.Task
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_new_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 5:45 PM
 */

class NewTaskScreen : VortexScreen() {
    override fun getLayoutRes(): Int {
        return R.layout.screen_new_task
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CreateButton?.apply {
            this.setOnClickListener {
                when {
                    TaskNameField?.text.toString().isEmpty() -> Toast.makeText(this@NewTaskScreen, "Task Name Required", Toast.LENGTH_SHORT).show()
                    ID?.text.toString().isEmpty() -> Toast.makeText(this@NewTaskScreen, "Team Member ID", Toast.LENGTH_SHORT).show()
                    else -> {
                        GlobalScope.launch {
                            show()
                            createNewTask()
                        }
                    }
                }
            }
        }
    }

    private suspend fun createNewTask() {
        withContext(Dispatchers.IO) {
            val groupID = VortexPrefs.get("GroupID", "") as String
            val id = UUID.randomUUID().toString()
            val myId = VortexPrefs.get("UserID", "") as String
            FirebaseFirestore.getInstance().collection("tasks").document()
                    .set(Task(
                            id = id,
                            name = TaskNameField?.text.toString(),
                            userId = myId,
                            groupId = groupID,
                            status = "NEW"
                    )).addOnCompleteListener {
                        GlobalScope.launch {
                            hide()
                            back()
                        }
                    }
        }
    }

    private suspend fun back() {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@NewTaskScreen, "Task Created ... ", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    private suspend fun show() {
        withContext(Dispatchers.Main) {
            LoaderNew?.let {
                it.visibility = View.VISIBLE
            }

            ContentNew?.let {
                it.visibility = View.GONE
            }
        }
    }

    private suspend fun hide() {
        withContext(Dispatchers.Main) {
            LoaderNew?.let {
                it.visibility = View.GONE
            }

            ContentNew?.let {
                it.visibility = View.VISIBLE
            }
        }
    }
}