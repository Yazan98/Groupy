package hu.grouper.app

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 12:21 PM
 */

class LocationUpdatesService : Service(), LocationListener {

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    private lateinit var locationManager: LocationManager

    override fun onLocationChanged(location: Location) {
        onNewLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
        callback?.let {
            it.onProviderEnabled()
        }
        Toast.makeText(applicationContext, "Gps Enabeld", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderDisabled(provider: String?) {
        callback?.let {
            it.onProviderStoped()
        }
    }

    private val mBinder = LocalBinder()
    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private var mChangingConfiguration = false

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private lateinit var mLocationRequest: LocationRequest

    /**
     * Provides access to the Fused Location Provider API.
     */
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mChangingLocationCallback: LocationCallback
    private lateinit var mServiceHandler: Handler
    private lateinit var mCurrentLocation: Location

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setCallbackLocation()
        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)

        var criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_HIGH

        val provider = locationManager.getBestProvider(criteria, true)

        if (::locationManager.isInitialized) {
            if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    1000,
                    10f,
                    this
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder {
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun requestLocationUpdates() {
        LocationUtils.setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest,
                    mChangingLocationCallback!!, Looper.myLooper()
            )

            if (::mCurrentLocation.isInitialized) {
                onNewLocation(mCurrentLocation)
            }

            flag = false
        } catch (unlikely: SecurityException) {
            LocationUtils.setRequestingLocationUpdates(this, false)
        }

    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            mCurrentLocation = task.result!!
                        } else {
                            Log.w(TAG, "Failed to get location.")
                        }
                    }
        } catch (unlikely: SecurityException) {

        }

    }

    private fun onNewLocation(location: Location) {
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        callback?.let {
            it.onNewLocation(location)
        }
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    private fun setCallbackLocation() {
        mChangingLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }
    }


    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

    companion object {

        var driverStatus: String = ""
        private val PACKAGE_NAME = "com.google.android.gms.location.sample.locationupdatesforegroundservice"
        val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        const val EXTRA_LOCATION = "com.google.android.gms.location.sample.locationupdatesforegroundservice.location"
        private val TAG = LocationUpdatesService::class.java.simpleName
        /**
         * The title of the channel for notifications.
         */
        private val CHANNEL_ID = "channel_01"
        private val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS
        private val NOTIFICATION_ID = 12345678
        const val REQUEST_CHECK_SETTINGS = 0x1
        var flag: Boolean = false
    }

    interface Callback {
        fun onProviderStoped()
        fun onProviderEnabled()
        fun onNewLocation(location: Location)
    }

}