package com.dw.ironButt.ui.way.start

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.denisovdw.ironbutt.database.room.PointLocation
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.TAG_AFTER_TIME
import com.denisovdw.ironbutt.utils.IronUtils
import com.denisovdw.ironbutt.utils.SharedPrefsManager
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*

class StartViewModel(val app: Application) : AndroidViewModel(app) {
    val mJob = Job()
    val mUiScope = CoroutineScope(Dispatchers.Main + mJob)
    private val repositoryWay = App.wayRepository
    val pref = SharedPrefsManager(app)

    var startMarkerMap: MutableMap<String, Marker> = HashMap()


    private var _hideProgressBar = MutableLiveData(false)
    val hideProgressBar: LiveData<Boolean> get() = _hideProgressBar

    private val _totalTime = MutableLiveData<String>(app.getString(R.string.start_total_time))
    val totalTime: LiveData<String> get() = _totalTime


    private val _rootFragment = MutableLiveData(false)
    val rootFragment: LiveData<Boolean> get() = _rootFragment

    private val _takePhoto = MutableLiveData(false)
    val takePhoto: LiveData<Boolean> get() = _takePhoto

    private val _visibilityBtnStart = MutableLiveData(false)
    val visibilityBtnStart: LiveData<Boolean> get() = _visibilityBtnStart

    private val _visibilityEditText = MutableLiveData(false)
    val visibilityEditText: LiveData<Boolean> get() = _visibilityEditText


    var latestLocationData: Location? = null
    var newPointLocation = PointLocation()

    var pointName = App.appResources.getString(R.string.start)

    var odometer = MutableLiveData("")

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    fun clickStart() {
        if (hideProgressBar.value!!) {
            if (odometer.value!!.isNotEmpty()) {
                _takePhoto.value = true
            } else _error.value = app.getString(R.string.not_all_fields_are_filled)
        } else _error.value = app.getString(R.string.loading_data)
    }

    fun deleteBaseTrack() {
        repositoryWay.deleteBaseTrack()
    }

    fun saveDate() {
        mUiScope.launch {
            withContext(Dispatchers.Main) {
                updateUserList()
                savePoint()
                _rootFragment.value = true
            }
        }
    }

    private fun savePoint() {
        pref.setStartOdometer(odometer.value!!.toInt())
        val point = newPointLocation
        point.unIdRoot = pref.getUinRoot()
        point.infoTrack = pointName
        point.odometer = odometer.value!!.toInt()
        repositoryWay.savePoint(point) { resultSavePoint ->
            if (resultSavePoint) odometer.value = ""
            else _error.value = app.getString(R.string.error_save_base_point)
        }
    }

    private fun updateUserList() {
        pref.setStartTime(System.currentTimeMillis() + (TAG_AFTER_TIME))
        val uInRoot = IronUtils.generateId()
        pref.setUinRoot(uInRoot)
        repositoryWay.getUserList { userList ->
            userList.uIdRoot = uInRoot
            userList.odometerStart = odometer.value!!.toInt()
            repositoryWay.updateStartUser(userList) { resultUpdate ->
                if (resultUpdate == 0)
                    _error.value = App.appResources.getString(R.string.error_save_base_user)
            }
        }

    }

    fun hideProgressBar(flag: Boolean) {
        _hideProgressBar.value = flag
    }

    fun isSuccessPhoto() {
        _takePhoto.value = false
    }

    fun visibilityEditText(flag: Boolean) {
        _visibilityEditText.value = flag
    }

    fun visibilityBtnStart(flag: Boolean) {
        _visibilityBtnStart.value = flag
    }

}