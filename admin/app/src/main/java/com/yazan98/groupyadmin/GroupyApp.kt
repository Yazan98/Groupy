package com.yazan98.groupyadmin

import android.content.Context
import com.google.firebase.FirebaseApp
import io.vortex.android.models.VortexPrefsDetails
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.prefs.VortexPrefsConfig
import io.vortex.android.utils.VortexApplication
import io.vortex.android.utils.VortexConfiguration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroupyApp : VortexApplication() {
    override fun onCreate() {
        super.onCreate()
        VortexPrefsConfig.prefs = getSharedPreferences("App", Context.MODE_PRIVATE)
        FirebaseApp.initializeApp(this)
        GlobalScope.launch {
            FirebaseApp.initializeApp(this@GroupyApp)
            VortexConfiguration.registerApplicationClass(this@GroupyApp)
                .registerApplicationState(true)
                .registerCompatVector()
                .registerVortexPermissionsSettings()
                .registerVortexPrefsConfiguration(VortexPrefsDetails(packageName = this@GroupyApp.packageName))
        }
    }
}
