package hu.grouper.app.fragment

import android.view.View
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import hu.grouper.app.logic.ProfileRepository
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.VortexMessageDelegation
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 7:49 AM
 */

class ProfileFragment : VortexBaseFragment() {

    private val repository: ProfileRepository by lazy {
        ProfileRepository(listener)
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
                        if (p.accountType.equals("ADMIN")) {
                            it.text = "Admin"
                        } else {
                            it.text = "Team Member"
                        }
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
                activity?.let {
                    VortexMessageDelegation().showShortMessage(message, it)
                }
            }
        }

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun initScreen(view: View) {
        GlobalScope.launch {
            VortexPrefs.get("UserID", "")?.let {
                showLoading()
                repository.getProfileById(it as String)
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
