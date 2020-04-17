package com.yazan98.groupyadmin.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.logic.ProfileRepository
import com.yazan98.groupyadmin.models.Profile
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.VortexMessageDelegation
import kotlinx.android.synthetic.main.screen_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileScreen : AppCompatActivity() {

    private val repository: ProfileRepository by lazy {
        ProfileRepository(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_profile)

        GlobalScope.launch {
            VortexPrefs.get("UserID", "")?.let {
                showLoading()
                repository.getProfileById(it as String)
            }
        }

        OutButton?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    VortexPrefs.put("UserStatus", false)
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@ProfileScreen, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private val listener = object : ProfileRepository.ProfileListener {
        override suspend fun onOperationSuccess(profile: Profile) {
            withContext(Dispatchers.Main) {
                hideLoading()
                profile.let { p ->
                    Name?.let {
                        it.text = p.name
                    }

                    Email?.let {
                        it.text = p.email
                    }

                    AccountType?.let {
                        it.setText(p.accountType)
                    }

                    Bio?.let {
                        it.text = p.bio
                    }

                    ID?.let {
                        it.text = p.id
                    }
                }
            }
        }

        override suspend fun onOperationFailed(message: String) {
            withContext(Dispatchers.Main) {
                VortexMessageDelegation().showShortMessage(message, this@ProfileScreen)
            }
        }

    }

    private suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            linearLayout2?.let {
                it.visibility = View.GONE
            }

            ProfileLoader?.let {
                it.visibility = View.VISIBLE
            }

            Icon?.let {
                it.visibility = View.GONE
            }
        }
    }

    private suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            linearLayout2?.let {
                it.visibility = View.VISIBLE
            }

            ProfileLoader?.let {
                it.visibility = View.GONE
            }

            Icon?.let {
                it.visibility = View.VISIBLE
            }
        }
    }

}
