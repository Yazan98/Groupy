package hu.grouper.app

import com.google.firebase.FirebaseApp
import io.vortex.android.models.VortexPrefsDetails
import io.vortex.android.utils.VortexApplication
import io.vortex.android.utils.VortexConfiguration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 6:49 AM
 */

class GrouperApp : VortexApplication() {
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            FirebaseApp.initializeApp(this@GrouperApp)
            VortexConfiguration.registerApplicationClass(this@GrouperApp)
                .registerApplicationState(BuildConfig.DEBUG)
                .registerCompatVector()
                .registerVortexPrefsConfiguration(VortexPrefsDetails(packageName =this@GrouperApp.packageName))
        }
    }
}