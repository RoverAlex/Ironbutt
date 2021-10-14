package com.dw.ironButt.ui.finish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dw.ironButt.database.room.PointLocation
import com.dw.ironButt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR
import com.dw.ironButt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR_SAVE
import com.dw.ironButt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR_TOKEN
import com.dw.ironButt.utils.IronButtConstant.Companion.REQUEST_SERVER_IS_SUCCESSFUL
import com.dw.ironButt.utils.IronUtils
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_SEND_PHOTO_LIST
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_SEND_POINT_LIST
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_SEND_USER_LIST
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*
import java.util.*

class FinishViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = App.finishRepository
    val pref = App.sharedPrefsManager
    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)

    private var _hideVisibilityProgressBar = MutableLiveData(false)
    val hideProgressBar: LiveData<Boolean> get() = _hideVisibilityProgressBar

    var startMarkerMap: MutableMap<String, Marker> = HashMap()


    private val _totalTime = MutableLiveData<String>()
    val totalTime: LiveData<String> get() = _totalTime

    private val _totalDistance = MutableLiveData("0")
    val totalDistance: LiveData<String> get() = _totalDistance

    val visibilityBtnSend = MutableLiveData(true)
    val visibilityInfoView = MutableLiveData(false)

    var textInfoSendRequest = MutableLiveData("")
    var progressBarRequest = MutableLiveData(0)

    var newStart = MutableLiveData(false)

    init {
        repository.totalTime {
            _totalTime.value = it
        }
    }

    fun hideProgressBar(flag: Boolean) {
        _hideVisibilityProgressBar.value = flag
    }

    fun getListTrack(points: (LiveData<List<PointLocation>>) -> Unit) {
        repository.getListTrack { point ->
            mUiScope.launch {
                withContext(Dispatchers.Main) {
                    points(point)
                }
            }
        }
    }

    fun newTotalDistance() {
        _totalDistance.value = IronUtils.formatLastOdometer()
    }
    fun onClickSendToServer() = stateSend()

    private fun toServerUserList() {
        textInfoSendRequest.value = app.getString(R.string.send_data_user)
        progressBarRequest.value = 30
        repository.requestToServerUserList {
            when (it) {
                REQUEST_SERVER_IS_SUCCESSFUL -> {
                    textInfoSendRequest.value =
                        app.getString(R.string.success_send_server_user_list)
                    progressBarRequest.value = 50
                    pref.setStateSend(STATE_SEND_POINT_LIST)
                    stateSend()
                }
                REQUEST_SERVER_ERROR_SAVE -> {
                    serverErrorSave()
                }
                REQUEST_SERVER_ERROR_TOKEN -> {
                    tokenError()
                }
                REQUEST_SERVER_ERROR -> {
                    serverError()
                }
            }
        }
    }


    private fun toServerPointList() {
        textInfoSendRequest.value = app.getString(R.string.send_track)
        progressBarRequest.value = 60
        repository.requestToServerPointList {
            when (it) {
                REQUEST_SERVER_IS_SUCCESSFUL -> {
                    textInfoSendRequest.value =
                        app.getString(R.string.success_send_server_point_list)
                    progressBarRequest.value = 70
                    pref.setStateSend(STATE_SEND_PHOTO_LIST)
                    stateSend()
                }
                REQUEST_SERVER_ERROR_SAVE -> {
                    serverErrorSave()
                }
                REQUEST_SERVER_ERROR_TOKEN -> {
                    tokenError()
                }
                REQUEST_SERVER_ERROR -> {
                    serverError()
                }
            }
        }
    }

    private fun toServerPhotoList() {
        textInfoSendRequest.value = app.getString(R.string.send_photo)
        progressBarRequest.value = 80
        repository.requestToServerPhotoList(app) {
            when (it) {
                REQUEST_SERVER_IS_SUCCESSFUL -> {
                    textInfoSendRequest.value =
                        app.getString(R.string.success_send_server_photo_list)
                    progressBarRequest.value = 100
                    pref.setStateSend(STATE_SEND_USER_LIST)
                    visibilityBtnSend.value = false
                    newStart.value = true
                }
                REQUEST_SERVER_ERROR_SAVE -> {
                    serverErrorSave()
                }
                REQUEST_SERVER_ERROR_TOKEN -> {
                    tokenError()
                }
                REQUEST_SERVER_ERROR -> {
                    serverError()
                }
            }
        }
    }

    private fun serverErrorSave() {
        textInfoSendRequest.value = app.getString(R.string.error_save)
    }

    private fun tokenError() {
        textInfoSendRequest.value = app.getString(R.string.error_token)
    }

    private fun serverError() {
        visibilityBtnSend.value = true
        textInfoSendRequest.value = app.getString(R.string.internet_connection)
    }

    private fun stateSend() {
        if (IronUtils.isNetworkConnected(app)) {
            visibilityBtnSend.value = false
            visibilityInfoView.value = true
            when (pref.getStateSend()) {
                STATE_SEND_USER_LIST -> toServerUserList()
                STATE_SEND_POINT_LIST -> toServerPointList()
                STATE_SEND_PHOTO_LIST -> toServerPhotoList()
            }
        } else IronUtils.showToast(app, app.getString(R.string.no_Internet))
    }
}
