package com.dw.ironButt

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter


@BindingAdapter("app:btnIsClickable")
fun btnIsClickable(view: View, flag: Boolean) {
    view.isClickable = flag
}

@BindingAdapter("app:hideLoading")
fun hideLoading(view: View, flag: Boolean) {
    view.visibility = if (!flag) View.VISIBLE else View.GONE

}

@BindingAdapter("app:visibleView")
fun visibleView(view: View, flag: Boolean) {
    view.visibility = if (flag) View.VISIBLE  else View.GONE
}

@BindingAdapter("app:visionRadioGroupIfSelect")
fun visionRadioGroupIfSelect(view: View, flag: Boolean) {
    view.visibility = if (flag) View.GONE else View.VISIBLE
}


@BindingAdapter("app:editTextError")
fun editTextError(editText: EditText,resultText:String){
    if (resultText.isEmpty())
        editText.error = "Поле не должно быть пустым!.."
    else {
        editText.error = null
    }
}





