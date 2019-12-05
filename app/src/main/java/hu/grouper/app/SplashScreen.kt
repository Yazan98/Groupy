package hu.grouper.app

import hu.grouper.app.screens.RegisterScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashScreen : VortexScreen() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            VortexPrefs.get("UserStatus", false)?.apply {
                if ((this as Boolean)) {

                } else {
                    startScreen<RegisterScreen>(true)
                }
            }
        }
    }

}
