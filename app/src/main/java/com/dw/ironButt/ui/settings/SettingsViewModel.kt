package com.dw.ironButt.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dw.ironButt.database.room.UserList
import com.dw.ironButt.App
import com.dw.ironButt.R
import kotlinx.coroutines.*

class SettingsViewModel : ViewModel() {

    private val interact = App.settingsRepository
    private val pref = App.sharedPrefsManager
    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)
    private val _successSaveUserList= MutableLiveData(false)
    val successSaveUserList :LiveData<Boolean> get() = _successSaveUserList

    private lateinit var userList: UserList

    var driver = MutableLiveData(pref.getDriverOrPassenger())
    var moto = MutableLiveData(pref.getMotorcycleOrScooter())
    var power = MutableLiveData(pref.getPowerScooter())
    var countryCity = MutableLiveData<String>(pref.getCountryCity())
    var nameUser = MutableLiveData<String>(pref.getUserName())
    var brandMoto = MutableLiveData<String>()

    private val _error = MutableLiveData<Int>()
    val error: LiveData<Int> get() = _error


    init {
        brandMoto.value = pref.getBrandMoto()

        interact.getUserList {
            userList = it
        }
    }


    fun onClickSave() {
        var flag = true
        pref.setDriverOrPassenger(driver.value!!)
        pref.setMotorcycleOrScooter(moto.value!!)
        pref.setPowerScooter(power.value!!)
        countryCity.observeForever {
            if (it.isNotEmpty()) {
                userList.countryCity = it
                pref.setCountryCity(it)
            } else flag = false
        }
        nameUser.observeForever {
            if (it.isNotEmpty()) {
                userList.fullName = it
                pref.setUserName(it)
            } else flag = false
        }
        brandMoto.observeForever {
            if (it.isNotEmpty()) {
                userList.brand = it
                pref.setBrandMoto(it)
            } else flag = false
        }
        if (flag) {
            saveRoom {
                if (it)
                    _successSaveUserList.value = true
            }
       // } else _error.value = App.appResources.getString(R.string.not_all_fields_are_filled)
        } else _error.value = R.string.not_all_fields_are_filled
    }



    private fun saveRoom(result: (Boolean) -> Unit) {
        interact.uppDataBase(userList) { resultSaveBase ->
            mUiScope.launch {
                withContext(Dispatchers.Main) {
                    if (resultSaveBase) {
                        result(resultSaveBase)
                    } else _error.value = R.string.error_save_base_user
                }
            }
        }
    }


}