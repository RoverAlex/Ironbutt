package com.dw.ironButt.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private var pref: SharedPreferences

    private val settingsReg = "settings_reg"
    private val motorcycleOrScooter = "motorcycle_or_scooter"
    private val driverOrPassenger = "driver_or_passenger"
    private val powerScooter = "power_scooter"
    private val uinRoot = "uin_root"
    private val username = "name_user"
    private val countryCityUser = "country_City_user"
    private val brandMoto = "brandMoto"
    private val startTime = "startTime"
    private val sendSE = "sendState"
    private val startOdometer = "start_odometer"
    private val lastOdometer = "last_odometer"
    private val stateApplication = "stateApplication"

    companion object {
        const val STATE_PRESENTER_LOGIN = "state_p_login"
        const val STATE_PRESENTER_SETTINGS = "state_p_settings"
        const val STATE_PRESENTER_START = "state_p_start"
        const val STATE_PRESENTER_ROOT = "state_p_root"
        const val STATE_PRESENTER_FINISH = "state_p_finish"

        const val STATE_SEND_USER_LIST  = "STATE_SEND_USER_LIST"
        const val STATE_SEND_POINT_LIST = "STATE_SEND_POINT_LIST"
        const val STATE_SEND_PHOTO_LIST = "STATE_SEND_PHOTO_LIST"
    }

    init {
        pref = context.getSharedPreferences(settingsReg, Context.MODE_PRIVATE)
    }

    fun getLastOdometer():Int {
        return pref.getInt(lastOdometer,0)
    }

    fun setLastOdometer(od:Int) {
        pref.edit().putInt(lastOdometer,od).apply()
    }

    fun getStartOdometer():Int {
        return pref.getInt(startOdometer,0)
    }

    fun setStartOdometer(od:Int) {
         pref.edit().putInt(startOdometer,od).apply()
    }

    fun setStateApplication(state:String){
        pref.edit().putString(stateApplication,state).apply()
    }
    fun getStateApplication():String? {
        return pref.getString(stateApplication, STATE_PRESENTER_LOGIN)
    }

    fun setStateSend(state:String){
        pref.edit().putString(sendSE,state).apply()
    }
    fun getStateSend():String? {
        return pref.getString(sendSE, STATE_SEND_USER_LIST)
    }

    fun getDriverOrPassenger():Boolean {
        return pref.getBoolean(driverOrPassenger,true)
    }
    fun setDriverOrPassenger(flag: Boolean){
        pref.edit().putBoolean(driverOrPassenger,flag).apply()
    }

    fun getMotorcycleOrScooter():Boolean {
        return pref.getBoolean(motorcycleOrScooter,true)
    }
    fun setMotorcycleOrScooter(flag: Boolean){
        pref.edit().putBoolean(motorcycleOrScooter,flag).apply()
    }

    fun getPowerScooter():Boolean {
        return pref.getBoolean(powerScooter,true)
    }
    fun setPowerScooter(flag: Boolean){
        pref.edit().putBoolean(powerScooter,flag).apply()
    }

    fun getCountryCity(): String? {
        return pref.getString(countryCityUser,"")
    }
    fun setCountryCity(name: String) {
        pref.edit().putString(countryCityUser, name).apply()
    }

    fun getBrandMoto(): String? {
        return pref.getString(brandMoto,"")
    }
    fun setBrandMoto(name: String) {
        pref.edit().putString(brandMoto, name).apply()
    }

    fun getUserName(): String? {
        return pref.getString(username,"")
    }
    fun setUserName(name: String) {
        pref.edit().putString(username, name).apply()
    }

    fun getStartTime(): Long {
        return pref.getLong(startTime,0L)
    }
    fun setStartTime(time: Long) {
        pref.edit().putLong(startTime, time).apply()
    }

    fun setUinRoot(uin: String) {
        pref.edit().putString(uinRoot, uin).apply()
    }
    fun getUinRoot(): String? {
        return pref.getString(uinRoot, "")
    }
}