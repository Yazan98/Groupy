package com.yazan98.groupyadmin

import android.os.Bundle
import android.view.View
import com.yazan98.groupyadmin.logic.ProfileRepository
import com.yazan98.groupyadmin.models.LoginModel
import com.yazan98.groupyadmin.models.Profile
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.VortexMessageDelegation
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginScreen : VortexScreen() {

    private val repository: ProfileRepository by lazy {
        ProfileRepository(listener)
    }

    override fun getLayoutRes(): Int {
        return R.layout.screen_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginButton?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    when {
                        EmailField?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage(
                            "Email Required",
                            this@LoginScreen
                        )
                        PasswordField?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage(
                            "Password Required",
                            this@LoginScreen
                        )
                        else -> {
                            showLoading()
                            repository.login(
                                LoginModel(
                                    EmailField?.text.toString(),
                                    PasswordField?.text.toString()
                                )
                            )
                        }
                    }
                }
            }
        }

        RegisterAcc?.apply {
            this.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    startScreen<RegisterScreen>(false)
                }
            }
        }
    }

    private val listener: ProfileRepository.ProfileListener =
        object : ProfileRepository.ProfileListener {
            override suspend fun onOperationFailed(message: String) {
                hideLoading()
                VortexMessageDelegation().showShortMessage(message, this@LoginScreen)
            }

            override suspend fun onOperationSuccess(profile: Profile) {
                hideLoading()
                profile.id?.let { VortexPrefs.put("UserID", it) }
                VortexPrefs.put("UserStatus", true)
                profile.accountType?.let { VortexPrefs.put("AccountType", it) }
                VortexMessageDelegation().showShortMessage("Welcome To Grouper", this@LoginScreen)
                profile.groupID?.let {
                    VortexPrefs.put("GroupID", it)
                }

                profile.name?.let {
                    VortexPrefs.put("Name", it)
                }

                startScreen<MainScreen>(true)
            }
        }

    private suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            Loader?.visibility = View.VISIBLE
            linearLayout?.visibility = View.GONE
        }
    }

    private suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            Loader?.visibility = View.GONE
            linearLayout?.visibility = View.VISIBLE
        }
    }

}