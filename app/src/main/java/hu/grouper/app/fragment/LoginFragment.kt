package hu.grouper.app.fragment

import android.view.View
import androidx.navigation.findNavController
import hu.grouper.app.R
import hu.grouper.app.data.models.LoginModel
import hu.grouper.app.data.models.Profile
import hu.grouper.app.logic.ProfileRepository
import hu.grouper.app.screens.MainScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.VortexMessageDelegation
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 6:54 AM
 */

class LoginFragment : VortexBaseFragment() {

    private val repository: ProfileRepository by lazy {
        ProfileRepository(listener)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_login
    }

    override fun initScreen(view: View) {
        LoginButton?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    activity?.let {
                        when {
                            EmailField?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage("Email Required" , it)
                            PasswordField?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage("Password Required" , it)
                            else -> {
                                showLoading()
                                repository.login(LoginModel(EmailField?.text.toString() , PasswordField?.text.toString()))
                            }
                        }
                    }
                }
            }
        }

        RegisterAcc?.apply {
            this.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }

    }

    private val listener: ProfileRepository.ProfileListener = object : ProfileRepository.ProfileListener {
        override suspend fun onOperationFailed(message: String) {
            hideLoading()
            activity?.let {
                VortexMessageDelegation().showShortMessage(message , it)
            }
        }

        override suspend fun onOperationSuccess(profile: Profile) {
            hideLoading()
            profile.id?.let { VortexPrefs.put("UserID" , it) }
            VortexPrefs.put("UserStatus" , true)
            profile.accountType?.let { VortexPrefs.put("AccountType" , it) }
            activity?.let {
                VortexMessageDelegation().showShortMessage("Welcome To Grouper" , it)
            }
            profile.groupID?.let {
                VortexPrefs.put("GroupID" , it)
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
