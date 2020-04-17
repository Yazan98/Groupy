package com.yazan98.groupyadmin.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.adapter.IdeasAdapter
import com.yazan98.groupyadmin.models.IdeaModel
import kotlinx.android.synthetic.main.screen_ideas.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdeasScreen : AppCompatActivity() {

    private val docs: ArrayList<String> by lazy { ArrayList<String>() }
    private val Mainadapter: IdeasAdapter by lazy {
        IdeasAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_ideas)

        IdeasRecycler?.apply {
            this.layoutManager =
                LinearLayoutManager(this@IdeasScreen, LinearLayoutManager.VERTICAL, false)
            this.adapter = Mainadapter
            (this.adapter as IdeasAdapter).context = this@IdeasScreen
        }

        Loading?.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection("ideas").get()
            .addOnCompleteListener {
                for (item in it.result?.documents!!) {
                    docs.add(item.id)
                }

                for (doc in docs.iterator()) {
                    FirebaseFirestore.getInstance().collection("ideas")
                        .document(doc).get().addOnCompleteListener {
                            it.result?.let {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Mainadapter.add(
                                        IdeaModel(
                                            title = it.getString("title")!!,
                                            description = it.getString("description")!!,
                                            section = it.getString("section")!!,
                                            type = it.getString("type")!!
                                        )
                                    )
                                }
                            }
                        }
                }

                GlobalScope.launch(Dispatchers.Main) {
                    Loading?.visibility = View.GONE
                }
            }
    }

}