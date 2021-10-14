package com.dw.ironButt.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.denisovdw.ironbutt.utils.myLog
import com.dw.ironButt.R
import com.dw.ironButt.ui.way.root.RootViewModel

class DialogFinish(val viewModel: RootViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity)
            .setTitle(R.string.btn_finish)
            .setMessage(R.string.dialog_description_finish)
            .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                try {
                    viewModel.pointName = R.string.finish
                    viewModel.takePhoto(true)
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
