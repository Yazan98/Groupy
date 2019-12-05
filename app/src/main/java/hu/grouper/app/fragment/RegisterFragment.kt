package hu.grouper.app.fragment

import android.app.ProgressDialog
import android.view.View
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import hu.grouper.app.logic.ProfileRepository
import hu.grouper.app.screens.MainScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.VortexMessageDelegation
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_reg.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 7:14 AM
 */

class RegisterFragment : VortexBaseFragment() {

    private val loader: ProgressDialog by lazy {
        ProgressDialog(activity)
    }

    private val repository: ProfileRepository by lazy {
        ProfileRepository(listener)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_reg
    }

    override fun initScreen(view: View) {
        loader.setMessage("Loading ...")
        btn_signup?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    activity?.let {
                        when {
                            input_name?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage("Name Required", it)
                            input_password?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage("Password Required", it)
                            input_email?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage("Email Required", it)
                            input_bio?.text.toString().isEmpty() -> VortexMessageDelegation().showShortMessage("Bio Required", it)
                            else -> {
                                loader.show()
                                GroupAdmin?.let {
                                    if (it.isChecked) {
                                        repository.register(Profile(
                                                name = input_name?.text.toString(),
                                                email = input_email?.text.toString(),
                                                bio = input_bio?.text.toString(),
                                                accountType = "ADMIN"
                                        ), input_password?.text.toString())
                                    } else {
                                        repository.register(Profile(
                                                name = input_name?.text.toString(),
                                                email = input_email?.text.toString(),
                                                bio = input_bio?.text.toString(),
                                                accountType = "MEMBER"
                                        ), input_password?.text.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        link_login?.apply {
            this.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    private val listener = object : ProfileRepository.ProfileListener {
        override suspend fun onOperationSuccess(profile: Profile) {
            profile.id?.let { VortexPrefs.put("UserID" , it) }
            profile.accountType?.let { VortexPrefs.put("AccountType" , it) }
            activity?.let {
                VortexMessageDelegation().showShortMessage("Welcome To Grouper" , it)
            }
            startScreen<MainScreen>(true)
        }

        override suspend fun onOperationFailed(message: String) {
            loader.dismiss()
            activity?.let {
                VortexMessageDelegation().showShortMessage(message , it)
            }
        }
    }

}
