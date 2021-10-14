package com.dw.ironButt.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.dw.ironButt.database.room.PointLocation
import com.dw.ironButt.App
import com.dw.ironButt.R
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class IronUtils {
    companion object {

        fun newCurrentMarker(loc: Location): PointLocation {
            val pointLocation = PointLocation()
            pointLocation.longitude = loc.longitude
            pointLocation.latitude = loc.latitude
            pointLocation.time = loc.time
            pointLocation.infoTrack = App.appResources.getString(R.string.are_you_here)
            pointLocation.id = 0
            return pointLocation
        }


        fun formatLastOdometer(): String {
            val startOdo: Int = App.sharedPrefsManager.getStartOdometer()
            val lastOdo: Int = App.sharedPrefsManager.getLastOdometer()
            return if (startOdo < lastOdo)
                (lastOdo - startOdo).toString() + " км."
            else "0"
        }


        val arrayPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        fun negativeTimeFormatter(time: Long): String {
            return String.format(
                "- %01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time) % 60 * -1,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60 * -1
            )
        }

        fun timeFormatter(time: Long): String {
            return String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time) % 60,
                TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60
            )
        }

        fun lastTimeNewPoint(time: Long): String {
            return String.format(
                "  %02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60
                )
        }

        fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities.also {
                if (it != null) {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        return true
                    else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    }
                }
            }
            return false
        }

        fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        fun generateId(): String {
            val longToken: Long = abs(SecureRandom().nextLong())
            return longToken.toString(16)
        }
    }


}