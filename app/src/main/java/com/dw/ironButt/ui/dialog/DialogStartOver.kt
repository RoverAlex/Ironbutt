package com.dw.ironButt.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.denisovdw.ironbutt.utils.myLog
import com.dw.ironButt.R


class DialogStartOver : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_title_confirm)
            .setMessage(R.string.dialog_text_start_over)
            .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                try {
                    findNavController().navigate(R.id.startFragment)

                } catch (e: java.lang.Exception) {
                    myLog("deleteFileDialog exception $e")
                }
                dialog.cancel()
            }
            .setNegativeButton(R.string.dialog_action_no) { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }



}












