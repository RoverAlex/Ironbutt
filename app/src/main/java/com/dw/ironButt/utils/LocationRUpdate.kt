package com.dw.ironButt.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.denisovdw.ironbutt.utils.myLog
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices



class LocationRUpdate(
    private val context: Context,
    private val timeSec: Long = 10L,
    private val distance: Float = 100F
) {

    val locationNow = MutableLiveData<Location>()

    private var locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            locationNow.value = result.lastLocation
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply {
            //fastestInterval = (LOCATION_REFRESH_TIME / LOCATION_REFRESH_TIME * 1000)
            interval = (timeSec * 1000)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = distance
            isWaitForAccurateLocation = true
        }

        return locationRequest
    }



    private fun updateLocation() {
        LocationServices.getFusedLocationProviderClient(context)
            .let { fusedLocationProviderClient ->

                if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    createLocationRequest(),
                    locationCallback,
                    null
                )
                //myLog("Start Location $LOCATION_REFRESH_TIME  sek ")


            }

    }

    fun start() = updateLocation()


    fun stop() {
        LocationServices.getFusedLocationProviderClient(context)
            .removeLocationUpdates(locationCallback)
        myLog("Stop Location")
    }

}