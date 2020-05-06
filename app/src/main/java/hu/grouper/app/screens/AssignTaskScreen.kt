package hu.grouper.app.screens

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import io.vortex.android.prefs.VortexPrefs
import kotlinx.android.synthetic.main.screen_assign_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.HashMap

class AssignTaskScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_assign_task)

        AssignButton?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    save()
                }
            }
        }
    }

    private suspend fun save() {
        withContext(Dispatchers.IO) {
            val obj = HashMap<String, Any>()
            obj.put("adminID", TeamMemberId?.text.toString())
            (VortexPrefs.get("GroupID", "") as String).let {
                FirebaseFirestore.getInstance().collection("groups").document(it)
                    .update(obj).addOnCompleteListener {
                        GlobalScope.launch(Dispatchers.Main) {
                            tet()
                        }
                    }
            }
        }
    }

    private suspend fun tet() {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@AssignTaskScreen, "Group Admin Assigned Successfully", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

}
