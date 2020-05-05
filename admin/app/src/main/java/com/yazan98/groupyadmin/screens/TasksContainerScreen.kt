package com.yazan98.groupyadmin.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yazan98.groupyadmin.R

class TasksContainerScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_tasks_container)
        supportFragmentManager?.beginTransaction().add(R.id.TasksContainer, TasksScreen())
            .commit()
    }
}