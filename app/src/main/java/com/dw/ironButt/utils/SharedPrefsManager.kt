package com.denisovdw.ironbutt.utils

import android.content.Context
import android.content.SharedPreferences


class SharedPrefsManager(context: Context) {
    private var pref: SharedPreferences


    private val SETINGS_REG = "settings_reg"
    private val CONST_AUTHORIZE = "authorize_user"
    private val MOTO_SCUTER = "motorcycle_or_scooter"
    private val DRIVER_PASSENGER = "driver_or_passenger"
    private val POWER_SCOOTER = "power_scooter"

    private val CONST_FINISH = "finish_user"
    private val CONST_UIN = "uin_user"
    private val CONST_UIN_ROOT = "uin_root"
    private val CONST_NAME = "name_user"
    private val CONST_BAKER_NAME = "CONST_BAKER_NAME"
    private val CONST_COUNTRY_CITY = "country_City_user"
    private val CONST_RAND_MOTO = "brandMoto"
    private val CONST_REG_NUMBER = "CONST_REG_NUMBER"
    private val START_TIME = "startTime"

    private val SEND_STATE = "SEND_STATE"
    private val START_ODOMETER = "start_odometer"
    private val LAST_ODOMETER = "last_odometer"

    private val APPLICATION_STATE = "stateApplication"
    private val ROOT_STATE = "stateRoot"



    companion object {
        const val STATE_PRESENTER_LOGIN = "state_p_login"
        const val STATE_PRESENTER_SETTINGS = "state_p_settings"
        const val STATE_PRESENTER_START = "state_p_start"
        const val STATE_PRESENTER_ROOT = "state_p_root"
        const val STATE_PRESENTER_FINISH = "state_p_finish"

        //WAY
        const val STATE_WAY_START  = "state_w_start"
        const val STATE_WAY_ROAD   = "state_w_road"
        const val STATE_WAY_FINISH = "state_w_finish"


        const val STATE_SEND_USER_LIST  = "STATE_SEND_USER_LIST"
        const val STATE_SEND_POINT_LIST = "STATE_SEND_POINT_LIST"
        const val STATE_SEND_PHOTO_LIST = "STATE_SEND_PHOTO_LIST"


    }

    init {
        pref = context.getSharedPreferences(SETINGS_REG, Context.MODE_PRIVATE)
    }

    fun getLastOdometer():Int {
        return pref.getInt(LAST_ODOMETER,0)
    }

    fun setLastOdometer(od:Int) {
        pref.edit().putInt(LAST_ODOMETER,od).apply()
    }


    fun getStartOdometer():Int {
        return pref.getInt(START_ODOMETER,0)
    }

    fun setStartOdometer(od:Int) {
         pref.edit().putInt(START_ODOMETER,od).apply()
    }

    fun setStateApplication(state:String){
        pref.edit().putString(APPLICATION_STATE,state).apply()
    }
    fun getStateApplication():String? {
        return pref.getString(APPLICATION_STATE, STATE_PRESENTER_LOGIN)
    }

    fun setStateSend(state:String){
        pref.edit().putString(SEND_STATE,state).apply()
    }
    fun getStateSend():String? {
        return pref.getString(SEND_STATE, STATE_SEND_USER_LIST)
    }



    fun getDriverOrPassenger():Boolean {
        return pref.getBoolean(DRIVER_PASSENGER,true)
    }
    fun setDriverOrPassenger(flag: Boolean){
        pref.edit().putBoolean(DRIVER_PASSENGER,flag).apply()
    }

    fun getMotorcycleOrScooter():Boolean {
        return pref.getBoolean(MOTO_SCUTER,true)
    }
    fun setMotorcycleOrScooter(flag: Boolean){
        pref.edit().putBoolean(MOTO_SCUTER,flag).apply()
    }



    fun getPowerScooter():Boolean {
        return pref.getBoolean(POWER_SCOOTER,true)
    }
    fun setPowerScooter(flag: Boolean){
        pref.edit().putBoolean(POWER_SCOOTER,flag).apply()
    }




    fun getCountryCity(): String? {
        return pref.getString(CONST_COUNTRY_CITY,"")
    }
    fun setCountryCity(name: String) {
        pref.edit().putString(CONST_COUNTRY_CITY, name).apply()
    }


    fun getBrandMoto(): String? {
        return pref.getString(CONST_RAND_MOTO,"")
    }
    fun setBrandMoto(name: String) {
        pref.edit().putString(CONST_RAND_MOTO, name).apply()
    }

    fun getUserName(): String? {
        return pref.getString(CONST_NAME,"")
    }
    fun setUserName(name: String) {
        pref.edit().putString(CONST_NAME, name).apply()
    }

    fun getBikerName(): String? {
        return pref.getString(CONST_BAKER_NAME,"")
    }
    fun setBakerName(name: String) {
        pref.edit().putString(CONST_BAKER_NAME, name).apply()
    }

    fun getStartTime(): Long {
        return pref.getLong(START_TIME,0L)
    }
    fun setStartTime(time: Long) {
        pref.edit().putLong(START_TIME, time).apply()
    }

    fun getFinish(): Boolean {
        return pref.getBoolean(CONST_FINISH, false)
    }
    fun setFinish(flag: Boolean) {
        pref.edit().putBoolean(CONST_FINISH, flag).apply()
    }

    fun setAuthorLk(uin: Int) {
        pref.edit().putInt(CONST_UIN, uin).apply()
    }
    fun getAuthorLk(): Int {
        return pref.getInt(CONST_UIN, -1)
    }

    fun setUinRoot(uin: String) {
        pref.edit().putString(CONST_UIN_ROOT, uin).apply()
    }
    fun getUinRoot(): String? {
        return pref.getString(CONST_UIN_ROOT, "")
    }

}