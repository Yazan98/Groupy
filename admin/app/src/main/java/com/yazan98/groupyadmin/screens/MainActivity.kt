package com.yazan98.groupyadmin.screens

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yazan98.groupyadmin.R
import io.vortex.android.prefs.VortexPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       GlobalScope.launch(Dispatchers.Main) {
           (VortexPrefs.get("UserStatus", false) as Boolean)?.let {
               println("THe Status : ${it}")
               when (it) {
                   true -> {
                       startActivity(Intent(this@MainActivity, MainScreen::class.java))
                       finish()
                   }

                   false -> {
                       startActivity(Intent(this@MainActivity, LoginScreen::class.java))
                       finish()
                   }
               }
           }
       }
    }
}
