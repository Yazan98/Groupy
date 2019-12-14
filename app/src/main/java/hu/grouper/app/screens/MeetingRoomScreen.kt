package hu.grouper.app.screens

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.LocationAdapter
import hu.grouper.app.data.models.Profile
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_meeting_room.*
import kotlinx.coroutines.*


/**
 * Created By : Yazan Tarifi
 * Date : 12/14/2019
 * Time : 12:37 PM
 */

class MeetingRoomScreen : VortexScreen(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mapFragment: SupportMapFragment
    private val mainAdapter: LocationAdapter by lazy {
        LocationAdapter(object : LocationAdapter.LocationListener {
            override fun onCLick(lat: Double?, lng: Double?) {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lat!!,
                            lng!!
                        ), 15f
                    )
                )
            }
        })
    }

    override fun getLayoutRes(): Int {
        return R.layout.screen_meeting_room
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        GlobalScope.launch {
            FirebaseFirestore.getInstance().collection("groups")
                .document(VortexPrefs.get("GroupID", "") as String)
                .get().addOnCompleteListener {
                    it.result?.let {
                        GlobalScope.launch {
                            for (member in it.get("members") as List<String>) {
                                FirebaseFirestore.getInstance().collection("users").document(member)
                                    .get().addOnCompleteListener {
                                        it.result?.let {
                                            mainAdapter.add(
                                                Profile(
                                                    id = it.getString("id"),
                                                    name = it.getString("name"),
                                                    lat = it.getDouble("lat"),
                                                    lng = it.getDouble("lng")
                                                )
                                            )
                                        }
                                    }
                            }

                            delay(2000)
                            for (item in mainAdapter.items) {
                                GlobalScope.launch {
                                    println("Item : $item")
                                    addCurrentLocationMarker(LatLng(item.lat!!, item.lng!!), item.name!!)
                                }
                            }

                            withContext(Dispatchers.Main) {
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            mainAdapter.items[0].lat!!,
                                            mainAdapter.items[0].lng!!
                                        ), 15f
                                    )
                                )
                            }
                        }
                    }
                }
        }

        MeetingsRecycler?.apply {
            this.layoutManager = LinearLayoutManager(this@MeetingRoomScreen, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = mainAdapter
            (this.adapter as LocationAdapter).context = this@MeetingRoomScreen
        }

    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) = Unit
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        buildGoogleApiClient()
        mGoogleApiClient.connect()
    }

    private suspend fun addCurrentLocationMarker(location: LatLng, nane: String) {
        println("Location Screen : $location")
        withContext(Dispatchers.Main) {
            val markerOptions = MarkerOptions()
            markerOptions.position(location)
            markerOptions.title(nane)
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.place))
            markerOptions.position
            mMap.addMarker(markerOptions)
        }
    }

    override fun onConnected(p0: Bundle?) = Unit
    override fun onConnectionSuspended(p0: Int) = Unit


}