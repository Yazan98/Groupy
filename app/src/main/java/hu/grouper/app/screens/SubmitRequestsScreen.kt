package hu.grouper.app.screens

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.GroupsAdapter
import hu.grouper.app.data.models.Group
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_submit_requests.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubmitRequestsScreen : AppCompatActivity() {

    private val mainAdapter: GroupsAdapter by lazy {
        GroupsAdapter(object : GroupsAdapter.GroupListener {
            override suspend fun onGroupSuccess() {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SubmitRequestsScreen,
                        "The Request Sent Perfectly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    fun getLayoutRes(): Int {
        return R.layout.screen_submit_requests
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        GlobalScope.launch {
            println("Groups Screen : onCreate : getAllGroups")
            getAllGroups()
        }

        GroupsListR?.apply {
            this.layoutManager = LinearLayoutManager(this@SubmitRequestsScreen, LinearLayoutManager.VERTICAL, false)
            this.adapter = mainAdapter
            (this.adapter as GroupsAdapter).context = this@SubmitRequestsScreen
        }
    }

    private suspend fun getAllGroups() {
        println("Groups Screen : getAllGroups inside")
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("groups")
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.let {
                            for (doc in it.documents) {
                                GlobalScope.launch {
                                    getDocumentById(doc.id)
                                }
                            }
                        }
                    } else {
                        GlobalScope.launch {
                            it.exception?.let {
                                it.message?.let {
                                    showMessage(it)
                                }
                            }
                        }
                    }
                }
        }
    }

    private suspend fun getDocumentById(id: String) {
        withContext(Dispatchers.IO) {
            println("Groups Screen : getDocumentById : $id")
            FirebaseFirestore.getInstance().collection("groups")
                .document(id).get()
                .addOnCompleteListener {
                    it.result?.let {
                        mainAdapter.addGroup(
                            Group(
                                id = it.getString("id"),
                                name = it.getString("name"),
                                adminID = it.getString("adminID"),
                                members = it.get("members") as List<String>
                            )
                        )
                    }
                }
        }
    }

    private suspend fun showMessage(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@SubmitRequestsScreen, message, Toast.LENGTH_SHORT).show()
        }
    }
}