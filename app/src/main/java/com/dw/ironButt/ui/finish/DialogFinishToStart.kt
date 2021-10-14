package com.dw.ironButt.ui.finish

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.dw.ironButt.R
import com.dw.ironButt.ui.finish.FinishViewModel

class DialogFinishToStart() : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity)
            //setTitle(R.string.dialog_title_delete)
            .setTitle("Ваши данные переданы на сервер")
            .setMessage("Перейти к началу")
            .setPositiveButton("OK") { dialog, _ ->
                try {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.startFragment)
                } catch (e: java.lang.Exception) {
                    Log.e("DialogReturnRoot", "exception", e)
                }
                dialog.cancel()
            }
            .create()
    }

}