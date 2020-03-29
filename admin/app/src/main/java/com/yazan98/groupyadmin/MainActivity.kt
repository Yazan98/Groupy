package com.yazan98.groupyadmin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSharedPreferences("App", Context.MODE_PRIVATE)?.let {
            it.getBoolean("UserStatus", false)?.let {
                when (it) {
                    true -> {
                        startActivity(Intent(this, MainScreen::class.java))
                        finish()
                    }

                    false -> {
                        startActivity(Intent(this, LoginScreen::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
