package com.yazan98.groupyadmin.screens

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.adapter.HomeMembersAdapter
import com.yazan98.groupyadmin.models.Profile
import com.yazan98.groupyadmin.models.Task
import io.vortex.android.prefs.VortexPrefs
import kotlinx.android.synthetic.main.screen_home_group.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreen : AppCompatActivity() {

    var length = 1
    private var tasks = ArrayList<Task>()
    private val homeAdapter: HomeMembersAdapter by lazy {
        HomeMembersAdapter(object : HomeMembersAdapter.Listener {
            override fun onClick(profile: Profile) = Unit
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_home_group)

        HomeToolbar?.let {
            this.setSupportActionBar(it)
        }

        GlobalScope.launch {

            val groupID = intent?.extras?.getString("ID")
            val userId = VortexPrefs.get("UserID", "") as String
            if (groupID.equals("")) {
                hideContent()
                showAcceptStatus()
                FirebaseFirestore.getInstance().collection("users").document(userId)
                    .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot?.getString("groupID")!!.isNotEmpty()) {
                            GlobalScope.launch {
                                VortexPrefs.put("GroupID", documentSnapshot?.getString("groupID")!!)
                                restart()
                            }
                        }
                    }
            } else {
                showLoading()
                if (groupID != null) {
                    FirebaseFirestore.getInstance().collection("groups").document(groupID)
                        .get().addOnCompleteListener {
                            it.result?.let {
                                GlobalScope.launch {
                                    getAdminName(it.getString("name"), it.getString("adminID"))
                                    showMembersByIds(it.get("members") as List<String>)
                                    groupID?.let { it1 -> showProgressByGroupId(it1) }
                                }
                            }
                        }
                }
            }
        }

        MembersGroup?.apply {
            this.layoutManager = LinearLayoutManager(this@HomeScreen, LinearLayoutManager.VERTICAL, false)
            this.adapter = homeAdapter
            (this.adapter as HomeMembersAdapter).context = this@HomeScreen
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, TasksScreen::class.java)
        intent.putExtra("ID", intent?.extras?.getString("ID"))
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    private suspend fun restart() {
        withContext(Dispatchers.Main) {
            recreate()
        }
    }

    private suspend fun getAdminName(name: String?, id: String?) {
        withContext(Dispatchers.IO) {
            id?.let {
                FirebaseFirestore.getInstance().collection("users")
                    .document(it).get().addOnCompleteListener {
                        it.result?.let {
                            GlobalScope.launch {
                                showGroupHeader(it.getString("name"), name)
                            }
                        }
                    }
            }
        }
    }

    private suspend fun showMembersByIds(ids: List<String>) {
        withContext(Dispatchers.IO) {
            ids.forEach {
                FirebaseFirestore.getInstance().collection("users")
                    .document(it).get().addOnCompleteListener {
                        it.result?.let {
                            if (homeAdapter.items.size <= 3) {
                                homeAdapter.add(Profile(
                                    id = it.getString("id"),
                                    name = it.getString("name"),
                                    bio = it.getString("bio"),
                                    lng = it.getDouble("lng"),
                                    lat = it.getDouble("lat"),
                                    email = it.getString("email"),
                                    accountType = it.getString("accountType")
                                ))
                            }
                        }
                    }
            }
            hideLoading()
        }
    }

    private suspend fun showProgressByGroupId(groupId: String) {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("tasks")
                .whereEqualTo("groupId", groupId)
                .get().addOnCompleteListener {
                    it.result?.let {
                        for (doc in it.documents) {
                            FirebaseFirestore.getInstance().collection("tasks")
                                .document(doc.id).get().addOnCompleteListener {
                                    it.result?.let {
                                        tasks.add(Task(
                                            id = it.getString("id"),
                                            name = it.getString("name"),
                                            groupId = it.getString("groupId"),
                                            status = it.getString("status"),
                                            userId = it.getString("userId")
                                        ))

                                        if (it.getString("status").equals("DONE")) {
                                            length += 1
                                        }

                                        progress?.apply {
                                            this.max = tasks.size
                                            this.progress = length
                                        }
                                    }
                                }
                        }

                    }
                }
        }
    }

    private suspend fun showGroupHeader(adminName: String?, groupName: String?) {
        withContext(Dispatchers.Main) {
            groupName?.let { res ->
                textView4?.let {
                    it.text = res
                }
            }

            adminName?.let { res ->
                AdminName?.let {
                    it.text = "Group Admin : $res"
                }
            }
        }
    }

    private suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            Loading?.let {
                it.visibility = View.VISIBLE
            }

            ScreenContent?.let {
                it.visibility = View.GONE
            }
        }
    }

    private suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            Loading?.let {
                it.visibility = View.GONE
            }

            ScreenContent?.let {
                it.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun hideContent() {
        withContext(Dispatchers.Main) {
            ScreenContent?.let {
                it.visibility = View.GONE
            }
        }
    }

    private suspend fun showAcceptStatus() {
        withContext(Dispatchers.Main) {
            ErrorView?.let {
                it.visibility = View.VISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        homeAdapter.items.clear()
        tasks.clear()
    }
}