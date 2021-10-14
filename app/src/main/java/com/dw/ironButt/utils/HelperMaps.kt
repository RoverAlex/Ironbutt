package com.dw.ironButt.utils

import com.denisovdw.ironbutt.database.room.PointLocation
import com.denisovdw.ironbutt.utils.myLog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class HelperMaps {
    companion object{

        fun newMapMarker(
            mMap:GoogleMap,
            startMarkerMap: MutableMap<String, Marker>,
            pointLocation: PointLocation,
            flagColor: Boolean = true
        ) {
            val latLng = LatLng(pointLocation.latitude, pointLocation.longitude)
            val lastLocation = LatLng(latLng.latitude, latLng.longitude)
            val id = pointLocation.id.toString()
            val previousMarker = startMarkerMap[id]
            var mAlfa = 1F
            var bitmapDescriptor =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            if (!flagColor) {
                bitmapDescriptor =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                mAlfa = 0.7f
            }
            val titlePoint: String? = pointLocation.infoTrack

            if (previousMarker != null) {
                previousMarker.position = lastLocation
                newCameraPosition(mMap,startMarkerMap)

            } else {
                val markerOptions = MarkerOptions()
                    .position(lastLocation)
                    .icon(bitmapDescriptor)
                    .title(titlePoint)
                    .alpha(mAlfa)
                val marker: Marker? = mMap.addMarker(markerOptions)
                startMarkerMap[id] = marker!!
                newCameraPosition(mMap,startMarkerMap)

            }
        }


        fun mapMarker(
            mMap:GoogleMap,
            startMarkerMap: MutableMap<String, Marker>,
            pointLocation: PointLocation,
            flagColor: Boolean = true
        ) {
            val latLng = LatLng(pointLocation.latitude, pointLocation.longitude)
            val lastLocation = LatLng(latLng.latitude, latLng.longitude)
            val id = pointLocation.id.toString()
            val previousMarker = startMarkerMap[id]
            var mAlfa = 1F
            var bitmapDescriptor =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            if (!flagColor) {
                bitmapDescriptor =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                mAlfa = 0.7f
            }
            val titlePoint: String? = pointLocation.infoTrack

            if (previousMarker != null) {
                previousMarker.position = lastLocation
                newCameraPosition(mMap,startMarkerMap)

            } else {
                val markerOptions = MarkerOptions()
                    .position(lastLocation)
                    .icon(bitmapDescriptor)
                    .title(titlePoint)
                    .alpha(mAlfa)
                val marker: Marker? = mMap.addMarker(markerOptions)
                startMarkerMap[id] = marker!!
                newCameraPosition(mMap,startMarkerMap)

            }
        }

        private fun newCameraPosition(
            mMap:GoogleMap,
            startMarkerMap: MutableMap<String, Marker>) {
            if (startMarkerMap.size > 1){
                bonds(mMap,startMarkerMap)
            }else{
                cameraUpdate(mMap,startMarkerMap)
            }

        }

        private fun cameraUpdate(mMap: GoogleMap, startMarkerMap: MutableMap<String, Marker>) {
            val zoomLevel = 15f
            var user:LatLng
            startMarkerMap.forEach {
                it.value.position.let { latLng ->
                    user = latLng
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,zoomLevel))
                }
            }
        }

        private fun bonds(mMap:GoogleMap,startMarkerMap: MutableMap<String, Marker>) {
            val boundsBuilder = LatLngBounds.Builder()
            startMarkerMap.forEach {
                it.value.position.let { latLng ->
                    boundsBuilder.include(latLng)
                }
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
        }
    }
}