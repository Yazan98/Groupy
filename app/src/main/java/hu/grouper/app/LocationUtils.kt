package hu.grouper.app

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.preference.PreferenceManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import java.text.DateFormat
import java.util.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 12:21 PM
 */

object LocationUtils {


    /**
     * Priority : LocationRequest.PRIORITY_HIGH_ACCURACY
     */
    fun enableLocationSettings(context: Context, interval: Long, fastInterval: Long, priority: Int) {
        val locationRequest = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(fastInterval)
                .setPriority(priority)

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        LocationServices
                .getSettingsClient(context)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(context as Activity) { response: LocationSettingsResponse ->
                    // startUpdatingLocation(...);
                }
                .addOnFailureListener(context) { ex ->
                    if (ex is ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ex.startResolutionForResult(context, 123)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }

                    }
                }
    }

    val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun requestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun setRequestingLocationUpdates(context: Context, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply()
    }

    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location): String {
        return "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun getLocationTitle(context: Context): String {
        return "Location updated " + DateFormat.getDateTimeInstance().format(Date())
    }
}