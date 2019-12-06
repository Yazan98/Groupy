package hu.grouper.app.fragment

import android.Manifest
import android.content.*
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import hu.grouper.app.LocationUpdatesService
import hu.grouper.app.R
import hu.grouper.app.data.models.Profile
import hu.grouper.app.logic.ProfileRepository
import io.vortex.android.permissions.VortexPermissionCallback
import io.vortex.android.permissions.VortexPermissions
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 12:15 PM
 */

class LocationTrackerFragment : VortexBaseFragment(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var locationManager: LocationManager? = null
    private var lastLocationTaken: Location? = null
    private var lastDetailsTaken: LatLng? = null
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var locationService: LocationUpdatesService
    private var isLocationServiceActive: Boolean = false
    private var GpsStatus: Boolean = false
    private val permission: VortexPermissions by lazy {
        VortexPermissions()
    }
    private val repo: ProfileRepository by lazy {
        ProfileRepository(object : ProfileRepository.ProfileListener {
            override suspend fun onOperationSuccess(profile: Profile) {
                withContext(Dispatchers.Main) {
                    showMessage("Location Updated Successfully")
                    findNavController().navigate(R.id.action_locationTrackerFragment_to_groupsFragment)
                }
            }

            override suspend fun onOperationFailed(message: String) = Unit
        })
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_location
    }

    override fun initScreen(view: View) {
        mapFragment = (childFragmentManager).findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        GlobalScope.launch {
            permission.registerPermissionCallback(object : VortexPermissionCallback {
                override suspend fun onPermissionDenied(requestCode: Int, permissions: List<String>) {
                    showMessage("Permission Not Generated")
                }

                override suspend fun onPermissionGenerated(requestCode: Int, permissions: List<String>) {
                    getTheCurrentLocation()
                }
            })
        }

        SelectLocation?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    if (permission.isPermissionGenerated(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        locationService.setCallback(locationCallbackListener)
                        locationService.requestLocationUpdates()
                    } else {
                        permission.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION , 123)
                    }
                }
            }
        }

        GlobalScope.launch {
            if (permission.isPermissionGenerated(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getTheCurrentLocation()
            } else {
                permission.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION , 123)
            }
        }

        SelectThisAddress?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    if (permission.isPermissionGenerated(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        lastDetailsTaken?.let {
                            repo.addLocationInfo(it)
                        }
                    } else {
                        permission.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION , 123)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        GlobalScope.launch {
            permission.onRequestPermissionsResult(requestCode , permissions as Array<String>, grantResults.toTypedArray())
        }
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
        activity?.let {
            Toast.makeText(it, "Connection Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
//        googleMap.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                        activity!!, R.raw.silver_style
//                )
//        )
        mMap = googleMap
        buildGoogleApiClient()
        mGoogleApiClient.connect()
    }

    private suspend fun getTheCurrentLocation() {
        withContext(Dispatchers.IO) {
            activity?.let {
                locationManager = (it.getSystemService(Context.LOCATION_SERVICE)) as LocationManager
                GpsStatus = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
                broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) = Unit
                }
                activity?.let {
                    it.registerReceiver(broadcastReceiver, IntentFilter("location_update"))
                }
                val serviceIntent = Intent(activity!!.applicationContext, LocationUpdatesService::class.java)
                activity?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.startService(serviceIntent)
                        it.bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)
                    } else {
                        it.startService(serviceIntent)
                    }
                }
            }
        }
    }

    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isLocationServiceActive = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: LocationUpdatesService.LocalBinder = (service as LocationUpdatesService.LocalBinder)
            locationService = binder.service
            isLocationServiceActive = true
            locationService.requestLocationUpdates()
            locationService.setCallback(locationCallbackListener)
        }

    }

    override fun onDestroy() {
        locationManager = null
        lastLocationTaken = null
        if (isLocationServiceActive) {
            activity?.unregisterReceiver(broadcastReceiver)
            activity?.let {
                locationService.setCallback(null)
                it.stopService(Intent(it, LocationUpdatesService::class.java))
            }
        }
        super.onDestroy()
    }

    private suspend fun addCurrentLocationMarker(location: Location?) {
        println("Location Screen : $location")
        withContext(Dispatchers.Main) {
            location?.let { loc ->
                lastLocationTaken = loc
                lastDetailsTaken = LatLng(loc.latitude, loc.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastDetailsTaken, 15f))
                val newLoc = CameraUpdateFactory.newLatLngZoom(lastDetailsTaken, 15f) as CameraUpdate
                mMap.animateCamera(newLoc)
                locationService.setCallback(null)
            }
        }
    }

    private val locationCallbackListener: LocationUpdatesService.Callback? = object : LocationUpdatesService.Callback {
        override fun onProviderStoped() {
            GlobalScope.launch {
                showMessage("Gps Disabled")
            }
        }

        override fun onProviderEnabled() {
            GlobalScope.launch {
                showMessage("Gps Enabled")
            }
        }

        override fun onNewLocation(location: Location) {
            GlobalScope.launch {
                addCurrentLocationMarker(location)
            }
        }
    }

    private suspend fun showMessage(message: String) {
        withContext(Dispatchers.Main) {
            activity?.let {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onConnected(p0: Bundle?) = Unit
    override fun onConnectionSuspended(p0: Int) = Unit
}