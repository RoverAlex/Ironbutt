package com.dw.ironButt.ui.finish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.denisovdw.ironbutt.database.room.PointLocation
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR_SEVE
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR_TOKEN
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.REQUEST_SERVER_IS_SUCCESSFUL
import com.denisovdw.ironbutt.utils.IronUtils
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_SEND_PHOTO_LIST
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_SEND_POINT_LIST
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_SEND_USER_LIST
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

    private val _totalDistance = MutableLiveData<String>("0")
    val totalDistance: LiveData<String> get() = _totalDistance

    val visibilityBtnSend = MutableLiveData(false)

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
        repository.getListTrack() { point ->
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

    fun onClickCancel() {
        visibilityBtnSend.value = false
    }

    fun onClickSendToServer() = stateSend()

    private fun toServerUserList() {
        repository.requestToServerUserList {
            when (it) {
                REQUEST_SERVER_IS_SUCCESSFUL -> {
                    textInfoSendRequest.value =
                        app.getString(R.string.success_send_server_user_list)
                    progressBarRequest.value = 100
                    pref.setStateSend(STATE_SEND_POINT_LIST)
                    stateSend()
                }
                REQUEST_SERVER_ERROR_SEVE -> {
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
        repository.requestToServerPointList {
            progressBarRequest.value = 0
            when (it) {
                REQUEST_SERVER_IS_SUCCESSFUL -> {
                    textInfoSendRequest.value =
                        app.getString(R.string.success_send_server_point_list)
                    progressBarRequest.value = 100
                    pref.setStateSend(STATE_SEND_PHOTO_LIST)
                    stateSend()
                }
                REQUEST_SERVER_ERROR_SEVE -> {
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
        repository.requestToServerPhotoList(app) {
            progressBarRequest.value = 0
            when (it) {
                REQUEST_SERVER_IS_SUCCESSFUL -> {
                    textInfoSendRequest.value =
                        app.getString(R.string.success_send_server_phote_list)
                    progressBarRequest.value = 100
                    pref.setStateSend(STATE_SEND_USER_LIST)
                    visibilityBtnSend.value = false
                    newStart.value = true
                }
                REQUEST_SERVER_ERROR_SEVE -> {
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
        textInfoSendRequest.value = "error save"
    }

    private fun tokenError() {
        textInfoSendRequest.value = "error token"
    }

    private fun serverError() {
        textInfoSendRequest.value = "server error "
    }

    private fun stateSend() {
        if (IronUtils.isNetworkConnected(app)) {
            visibilityBtnSend.value = true
            when (pref.getStateSend()) {
                STATE_SEND_USER_LIST -> toServerUserList()
                STATE_SEND_POINT_LIST -> toServerPointList()
                STATE_SEND_PHOTO_LIST -> toServerPhotoList()
            }
        } else IronUtils.showToast(app, app.getString(R.string.no_Internet))
    }
}
