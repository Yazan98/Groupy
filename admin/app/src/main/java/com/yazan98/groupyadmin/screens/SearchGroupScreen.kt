package com.yazan98.groupyadmin.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.adapter.GroupsAdapter
import com.yazan98.groupyadmin.models.Group
import kotlinx.android.synthetic.main.screen_search_group.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchGroupScreen : AppCompatActivity() {

    private val mainAdapter: GroupsAdapter by lazy {
        GroupsAdapter(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_search_group)

        GroupsRecycler?.apply {
            this.layoutManager =
                LinearLayoutManager(this@SearchGroupScreen, LinearLayoutManager.VERTICAL, false)
            this.adapter = mainAdapter
            (this.adapter as GroupsAdapter).context = this@SearchGroupScreen
        }

        SearchButton?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    search(GroupName?.text.toString())
                }
            }
        }
    }

    private val listener = object : GroupsAdapter.GroupListener {
        override fun onGroupSuccess(group: Group) {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@SearchGroupScreen, HomeScreen::class.java)
                intent.putExtra("ID", group.id)
                startActivity(intent)
            }
        }
    }

    private suspend fun search(name: String) {
        withContext(Dispatchers.IO) {
            GlobalScope.launch(Dispatchers.Main) {
                LoadingSearch?.visibility = View.VISIBLE
            }
            when (name) {
                "" -> getAll()
                else -> getByName(name)
            }
        }
    }

    private suspend fun getAll() {
        println("Start : All")
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

    private suspend fun getByName(name: String) {
        println("Start : Name : ${name}")
        FirebaseFirestore.getInstance().collection("groups")
            .whereEqualTo("name", name)
            .get().addOnCompleteListener {
                println("Start : Item : ${it.result}")
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

    private suspend fun getDocumentById(id: String) {
        withContext(Dispatchers.IO) {
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

                        Handler(Looper.getMainLooper()).postDelayed({
                            LoadingSearch?.visibility = View.GONE
                        }, 2000)
                    }

                }
        }
    }

    private suspend fun showMessage(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@SearchGroupScreen, message, Toast.LENGTH_SHORT).show()
        }
    }

}
