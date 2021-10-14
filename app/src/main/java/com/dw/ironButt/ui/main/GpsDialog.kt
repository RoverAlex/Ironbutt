package com.dw.ironButt.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import com.dw.ironButt.R


class GpsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("ironbutt")
                .setMessage("Чтобы продолжить, включите на устройстве геолокацию ")
                .setIcon(R.drawable.logo)
                .setPositiveButton("ОК") { _, _ -> packageNotFoundGPS() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun packageNotFoundGPS() {
        val gpsOptionsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(gpsOptionsIntent)
    }

}