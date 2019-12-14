package hu.grouper.app.screens

import android.os.Bundle
import hu.grouper.app.R
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created By : Yazan Tarifi
 * Date : 12/14/2019
 * Time : 2:03 PM
 */

class ChatScreen : VortexScreen() {

    override fun getLayoutRes(): Int {
        return R.layout.screen_chat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            (VortexPrefs.get("groupID", "") as String).let {
                
            }
        }
    }
}