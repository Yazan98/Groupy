package hu.grouper.app.fragment

import android.view.View
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import hu.grouper.app.logic.ProfileRepository
import io.vortex.android.ui.VortexMessageDelegation
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                                repo
                            }
                        }
                    }
                }
            }
        }
    }

    private val listener: ProfileRepository.ProfileListener = object : ProfileRepository.ProfileListener {
        override suspend fun onOperationFailed(message: String) {

        }

        override suspend fun onOperationSuccess(profile: Profile) {

        }
    }
}