package com.dw.ironButt.ui.main

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dw.ironButt.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface IOnBackPressed {
    fun onBackPressed(double: Boolean = false)
}

class MainActivity : AppCompatActivity() {
    private var firstClick = false
    private var jop: Job? = null
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)



    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!locationManager.isLocationEnabled) {
                val gpsDialog = GpsDialog()
                val manager = supportFragmentManager
                gpsDialog.show(manager, "gpsDialog")
            }
        }

    }
    override fun onBackPressed() {
        val navHost = this.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        navHost?.let {
            it.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is IOnBackPressed) {
                    if (!firstClick) {
                        firstClick = true
                        jop = lifecycleScope.launch {
                            delay(500)
                            firstClick = false
                            fragment.onBackPressed(false)
                        }
                    } else {
                        fragment.onBackPressed(true)
                        jop?.cancel()
                        firstClick = false
                    }
                } else {
                    super.onBackPressed()
                }
            }
        }
    }
}
