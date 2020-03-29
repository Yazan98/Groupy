package com.yazan98.groupyadmin.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yazan98.groupyadmin.R
import kotlinx.android.synthetic.main.screen_main.*

class MainScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_main)

        SubmitIdeaButton?.apply {
            this.setOnClickListener {
                startActivity(Intent(this@MainScreen, SubmitIdeaScreen::class.java))
            }
        }
    }

}