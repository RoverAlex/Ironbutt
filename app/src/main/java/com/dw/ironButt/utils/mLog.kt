package com.dw.ironButt.utils

import android.util.Log

var MY_LOG_LINE_NUM: Int = 1
fun myLog(msg: String) {

    Log.d("mLog", "L $MY_LOG_LINE_NUM: $msg")
    MY_LOG_LINE_NUM += 1

}