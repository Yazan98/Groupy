package com.yazan98.groupyadmin.screens

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.R
import kotlinx.android.synthetic.main.screen_idea.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SubmitIdeaScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_idea)
        SubmitButton?.apply {
            this.setOnClickListener {
                when {
                    IdeaTitle?.text.toString().isEmpty() -> Toast.makeText(this@SubmitIdeaScreen, "Idea Title Required", Toast.LENGTH_SHORT).show()
                    ProjectType?.text.toString().isEmpty() -> Toast.makeText(this@SubmitIdeaScreen, "Project Type Required", Toast.LENGTH_SHORT).show()
                    ProjectSection?.text.toString().isEmpty() -> Toast.makeText(this@SubmitIdeaScreen, "Project Section Required", Toast.LENGTH_SHORT).show()
                    ProjectDesc?.text.toString().isEmpty() -> Toast.makeText(this@SubmitIdeaScreen, "Project Description Required", Toast.LENGTH_SHORT).show()
                    else -> {
                        FirebaseFirestore.getInstance().collection("ideas")
                            .document().set(Idea(
                                IdeaTitle?.text.toString(),
                                ProjectType?.text.toString(),
                                ProjectSection?.text.toString(),
                                ProjectDesc?.text.toString()
                            )).addOnCompleteListener {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(this@SubmitIdeaScreen, "Idea Submitted Successfully", Toast.LENGTH_SHORT).show()
                                    onBackPressed()
                                }
                            }
                    }
                }
            }
        }
    }

    data class Idea(
        val title: String,
        val type: String,
        val section: String,
        val description: String
    )
}