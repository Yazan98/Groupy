package com.yazan98.groupyadmin.screens

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.logic.ProfileRepository
import com.yazan98.groupyadmin.models.Profile
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.VortexMessageDelegation
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.fragment_reg.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterScreen : VortexScreen() {

    private val loader: ProgressDialog by lazy {
        ProgressDialog(this)
    }

    private val repository: ProfileRepository by lazy {
        ProfileRepository(listener)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_reg
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader.setMessage("Loading ...")
        btn_signup?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    when {
                        input_name?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage(
                            "Name Required",
                            this@RegisterScreen
                        )
                        input_password?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage(
                            "Password Required",
                            this@RegisterScreen
                        )
                        input_email?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage(
                            "Email Required",
                            this@RegisterScreen
                        )
                        input_bio?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage(
                            "Bio Required",
                            this@RegisterScreen
                        )
                        else -> {
                            showLoading()
                            repository.register(
                                Profile(
                                    name = input_name?.text.toString(),
                                    email = input_email?.text.toString(),
                                    bio = input_bio?.text.toString(),
                                    accountType = "PROFESSOR"
                                ), input_password?.text.toString()
                            )
                        }
                    }
                }
            }
        }

        link_login?.apply {
            this.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private val listener = object : ProfileRepository.ProfileListener {
        override suspend fun onOperationSuccess(profile: Profile) {
            withContext(Dispatchers.Main) {
                loader.dismiss()
            }

            profile.id?.let { VortexPrefs.put("UserID", it) }
            profile.accountType?.let { VortexPrefs.put("AccountType", it) }
            profile.name?.let {
                VortexPrefs.put("Name", it)
            }
            VortexPrefs.put("UserStatus", true)
//            VortexPrefs.put("UserStatus" , true)
            VortexMessageDelegation().showShortMessage("Welcome To Grouper", this@RegisterScreen)

            withContext(Dispatchers.Main) {
                VortexPrefs.put("UserStatus", true)
                startActivity(Intent(this@RegisterScreen, MainScreen::class.java))
                finish()
            }
        }

        override suspend fun onOperationFailed(message: String) {
            withContext(Dispatchers.Main) {
                loader.dismiss()
            }
            VortexMessageDelegation().showShortMessage(message, this@RegisterScreen)
        }
    }

    private suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            loader.show()
        }
    }
}