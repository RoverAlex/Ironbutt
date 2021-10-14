package com.dw.ironButt.ui.way.root

import android.app.Application
import android.location.Location
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dw.ironButt.database.room.PointLocation
import com.dw.ironButt.utils.IronButtConstant
import com.dw.ironButt.utils.IronUtils
import com.dw.ironButt.utils.SharedPrefsManager
import com.dw.ironButt.utils.myLog
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*

class RootViewModel(val app: Application) : AndroidViewModel(app) {
    val mJob = Job()
    val mUiScope = CoroutineScope(Dispatchers.Main + mJob)
    private val repositoryWay = App.wayRepository
    private val pref = SharedPrefsManager(app)


    var startMarkerMap: MutableMap<String, Marker> = HashMap()

    private var _hideProgressBar = MutableLiveData(false)
    val hideProgressBar: LiveData<Boolean> get() = _hideProgressBar

    private val second: Long = 1_000L
    private lateinit var timer: CountDownTimer

    private val _totalTime = MutableLiveData<String>()
    val totalTime: LiveData<String> get() = _totalTime

    private val _totalDistance = MutableLiveData(IronUtils.formatLastOdometer())
    val totalDistance: LiveData<String> get() = _totalDistance

    private val _finishFragment = MutableLiveData(false)
    val finishFragment: LiveData<Boolean> get() = _finishFragment

    private val _takePhoto = MutableLiveData(false)
    val takePhoto: LiveData<Boolean> get() = _takePhoto

    private val _visibilityBtnPoint = MutableLiveData(false)
    val visibilityBtnPoint: LiveData<Boolean> get() = _visibilityBtnPoint

    private val _visibilityBtnFinish = MutableLiveData(false)
    val visibilityBtnFinish: LiveData<Boolean> get() = _visibilityBtnFinish

    private val _finish = MutableLiveData(false)
    val finish: LiveData<Boolean> get() = _finish


    private val _visibilityTextInfo = MutableLiveData(true)
    val visibilityTextInfo: LiveData<Boolean> get() = _visibilityTextInfo

    private val _visibilityEditText = MutableLiveData(false)
    val visibilityEditText: LiveData<Boolean> get() = _visibilityEditText

    private val _textInfo = MutableLiveData(app.getString(R.string.satellites))
    val textInfo: LiveData<String> get() = _textInfo

    var latestLocationData: Location? = null
    val newPointLocation = PointLocation()
    var flagPoint = false
    var pointName: Int = R.string.point

    var odometer = MutableLiveData("")

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun clickNewPoint() {
        if (hideProgressBar.value!!) {
            if (odometer.value!!.isNotEmpty()) {
                pointName = R.string.point
                takePhoto(true)
            } else _error.value = app.getString(R.string.not_all_fields_are_filled)
        } else _error.value = app.getString(R.string.loading_data)
    }

    fun clickFinishBtn() {
        pointName = R.string.finish
        if (odometer.value!!.isNotEmpty()) {
            _finish.value = true
        } else _error.value = app.getString(R.string.not_all_fields_are_filled)

    }

    fun getListTrack(points: (LiveData<List<PointLocation>>) -> Unit) {
        repositoryWay.getListTrack { point ->
            mUiScope.launch {
                withContext(Dispatchers.Main) {
                    points(point)
                }
            }

        }
    }

    fun saveDate() {
        mUiScope.launch {
            withContext(Dispatchers.Main) {
                savePoints {
                    if (it) {
                        if (pointName == R.string.finish) updateUserList()
                        newTotalDistance()
                        //odometer.value = ""
                    } else _error.value = "Ошибка записи pointBase"
                }
            }
        }
    }

    private fun savePoints(result: (Boolean) -> Unit) {
        pref.setLastOdometer(odometer.value!!.toInt())
        val point = newPointLocation
        point.unIdRoot = pref.getUinRoot()
        point.infoTrack = app.getString(pointName)
        point.odometer = odometer.value!!.toInt()
        repositoryWay.savePoint(point) { resultSavePoint ->
            if (resultSavePoint) {
                result(resultSavePoint)
            } else myLog("track  save error")
        }
    }

    private fun updateUserList() {
        repositoryWay.getUserList { userList ->
            val odometer = odometer.value
            //myLog("odometer $odometer")
            userList.odometerFinish = odometer!!
            userList.totalTime = totalTime.value!!
            repositoryWay.upDataUserList(userList)
            mUiScope.launch {
                withContext(Dispatchers.Main) {
                    _finishFragment.value = true
                }
            }

        }
    }

    fun createTimer() {
        viewModelScope.launch {
            val triggerTime = pref.getStartTime()
            timer = object : CountDownTimer(triggerTime, second) {
                override fun onTick(millisUntilFinished: Long) {
                    checkBtn()
                    val currentTime = System.currentTimeMillis() - triggerTime
                    //myLog("currentTime " +  currentTime / 1000 )
                    if (currentTime > 0)
                        _totalTime.value = IronUtils.timeFormatter(currentTime)
                    else
                        _totalTime.value = IronUtils.negativeTimeFormatter(currentTime)
                }

                override fun onFinish() {
                    resetTimer()
                }
            }
            timer.start()
        }
    }

    private fun checkBtn() {
        repositoryWay.getLastTime { lastTime ->
            mUiScope.launch {
                withContext(Dispatchers.Main) {
                    val pastTime =
                        System.currentTimeMillis() - (lastTime + IronButtConstant.TAG_MAX_PAST_TIME)
                    //myLog("pastTime " + pastTime / 1000)
                    if (pastTime > 0) {
                        _visibilityBtnPoint.value = true
                        _visibilityTextInfo.value = false

                    } else {
                        _visibilityBtnPoint.value = false
                        _visibilityTextInfo.value = true
                        _textInfo.value = app.getString(R.string.you_new_point) +
                                IronUtils.lastTimeNewPoint(-pastTime)
                    }
                }
            }
        }
    }

    private fun resetTimer() {
        _totalTime.value = IronUtils.timeFormatter(0)

    }

    fun hideProgressBar(flag: Boolean) {
        _hideProgressBar.value = flag
        _visibilityBtnPoint.value = flag
        _visibilityBtnFinish.value = flag
        _visibilityEditText.value = flag
        _visibilityTextInfo.value = !flag
    }

    fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        resetTimer()
    }

    private fun newTotalDistance() {
        _totalDistance.value = IronUtils.formatLastOdometer()
    }

    fun takePhoto(flag: Boolean) {
        _takePhoto.value = flag
    }

}