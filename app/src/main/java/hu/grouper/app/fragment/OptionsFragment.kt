package hu.grouper.app.fragment

import android.view.View
import hu.grouper.app.R
import hu.grouper.app.screens.AboutUsScreen
import hu.grouper.app.screens.ChatScreen
import hu.grouper.app.screens.MeetingRoomScreen
import hu.grouper.app.screens.RequestsScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_options.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/14/2019
 * Time : 1:30 PM
 */

class OptionsFragment : VortexBaseFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_options
    }

    override fun initScreen(view: View) {
        MeetingRoom?.let {
            it.setOnClickListener {
                GlobalScope.launch {
                    startScreen<MeetingRoomScreen>(false)
                }
            }
        }

        AboutApp?.let {
            it.setOnClickListener {
                GlobalScope.launch {
                    startScreen<AboutUsScreen>(false)
                }
            }
        }

        CharRoom?.let {
            it.setOnClickListener {
                GlobalScope.launch {
                    startScreen<ChatScreen>(false)
                }
            }
        }

        GroupRequests?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    startScreen<RequestsScreen>(false)
                }
            }
        }

        GlobalScope.launch {
            VortexPrefs.get("AccountType", "")?.let {
                if (it.equals("ADMIN")) {
                    show()
                }
            }
        }
    }

    private suspend fun show() {
        withContext(Dispatchers.Main) {
            GroupRequests?.let {
                it.visibility = View.VISIBLE
            }
        }
    }

}
