package hu.grouper.app.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import hu.grouper.app.R
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_profile.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 8:15 PM
 */

class ProfileScreen : VortexScreen(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mapFragment: SupportMapFragment

    override fun getLayoutRes(): Int {
        return R.layout.screen_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        intent?.extras?.let { bundle ->
            bundle.getString("name")?.let { res ->
                Name?.let {
                    it.text = res
                }
            }

            bundle.getString("email")?.let { les ->
                Email?.let {
                    it.text = les
                }

                bundle.getString("accountType")?.let { det ->
                    AccountType?.let {
                        it.text = det
                    }
                }

                bundle.getString("bio")?.let { res ->
                    Bio?.let {
                        it.text = res
                    }
                }

                bundle.getString("id")?.let { res ->
                    ID?.let {
                        it.text = res
                    }
                }
            }
        }

        ID?.apply {
            this.setOnClickListener {
                setClipboard(this@ProfileScreen , ID?.text.toString())
                onBackPressed()
            }
        }

    }

    private fun addCurrentLocationMarker(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        val newLoc = CameraUpdateFactory.newLatLngZoom(latLng, 15f) as CameraUpdate
        mMap.animateCamera(newLoc)
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
//        googleMap.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                        activity!!, R.raw.silver_style
//                )
//        )
        mMap = googleMap
        buildGoogleApiClient()
        mGoogleApiClient.connect()

        intent?.extras?.let { bundle ->
            bundle.getDouble("lat")?.let { res ->
                addCurrentLocationMarker(LatLng(res, bundle.getDouble("lng")))
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setClipboard(context: Context, text: String) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", text)
            clipboard.primaryClip = clip
        }
        Toast.makeText(context , "Copy Member ID" , Toast.LENGTH_SHORT).show()
    }

    override fun onConnected(p0: Bundle?) = Unit
    override fun onConnectionSuspended(p0: Int) = Unit
}