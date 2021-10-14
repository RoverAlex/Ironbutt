package com.dw.ironButt.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dw.ironButt.R

class DialogFragmentInfo (private val message: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            //.setTitle(R.string.dialog_title_delete)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_action_ok) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

}
