package com.dw.ironButt.ui.finish

import android.content.Context
import androidx.lifecycle.LiveData
import com.dw.ironButt.database.api.ServerResponse
import com.dw.ironButt.database.api.ApiService
import com.dw.ironButt.database.room.PointLocation
import com.dw.ironButt.database.room.PointLocationDao
import com.dw.ironButt.database.room.UserDatabaseDao
import com.dw.ironButt.utils.IronButtConstant.Companion.REQUEST_SERVER_ERROR
import com.dw.ironButt.utils.IronUtils
import com.dw.ironButt.utils.myLog
import com.dw.ironButt.App
import com.dw.ironButt.database.file.FileHelper
import com.dw.ironButt.utils.TokenConstant.Companion.TOKEN
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class FinishRepository(
    private val apiService: ApiService,
    private val pointDatabaseDao: PointLocationDao,
    private val userDatabaseDao: UserDatabaseDao
) {

    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)
    private val gson = Gson()

    fun requestToServerUserList(result: (String) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                val userList = userDatabaseDao.getUserList()
                val gsonUserList = gson.toJson(userList)

                val call = apiService.requestUserList(TOKEN, gsonUserList)
                call.enqueue(object : retrofit2.Callback<ServerResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<ServerResponse>,
                        response: retrofit2.Response<ServerResponse>
                    ) {
                        if (response.isSuccessful) {
                            result(response.body()!!.success)
                            //myLog("response.body()!!.success " + response.body()!!.success)
                            //myLog("response.body() " + response.body().toString())
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ServerResponse>, t: Throwable) {
                        result(REQUEST_SERVER_ERROR)
                        myLog("onFailure " + call.timeout())
                        myLog("onFailure t: " + t.message)
                    }

                })
            }
        }

    }

    fun requestToServerPointList(result: (String) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                val userList = pointDatabaseDao.getResultAllPoints()
                val gsonPointList = gson.toJson(userList)

                val call = apiService.requestPointList(TOKEN, gsonPointList)
                call.enqueue(object : retrofit2.Callback<ServerResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<ServerResponse>,
                        response: retrofit2.Response<ServerResponse>
                    ) {
                        if (response.isSuccessful) {
                            result(response.body()!!.success)

                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ServerResponse>, t: Throwable) {
                        result(REQUEST_SERVER_ERROR)
                        myLog("onFailure " + call.timeout())
                        myLog("onFailure t: " + t.message.toString())
                    }

                })
            }
        }

    }

    fun requestToServerPhotoList(context:Context, result: (String) -> Unit) {
        val fileHelper = FileHelper(context)
        val dirImages = fileHelper.mDirImages
        val listFile = dirImages.list()
        mUiScope.launch {
            withContext(Dispatchers.IO) {

                val unIRout = App.sharedPrefsManager.getUinRoot()
                val parts = mutableListOf<MultipartBody.Part>()

                listFile?.forEach { nameFile ->
                    val file = File(dirImages, nameFile)
                    val imageBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    parts.add(MultipartBody.Part.createFormData(nameFile, file.path, imageBody))
                }

                val call = apiService.uploadImages(TOKEN,unIRout!!,parts)
                call.enqueue(object : retrofit2.Callback<ServerResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<ServerResponse>,
                        response: retrofit2.Response<ServerResponse>
                    ) {
                        if (response.isSuccessful) {
                            result(response.body()!!.success)
                        }
                    }
                    override fun onFailure(call: retrofit2.Call<ServerResponse>, t: Throwable) {
                        result(REQUEST_SERVER_ERROR)
                    }

                })
            }
        }
    }


    fun totalTime(totalTime: (String) -> Unit) {
        var finishTime: Long
        val startTime = App.sharedPrefsManager.getStartTime()
        getLastTime{
            mUiScope.launch {
                withContext(Dispatchers.Main) {
                    finishTime = it
                    totalTime(IronUtils.timeFormatter(finishTime - startTime))
                }
            }
        }
    }

    fun getListTrack(points: (LiveData<List<PointLocation>>) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                points(pointDatabaseDao.getAllPoints())
            }
        }
    }

    private fun getLastTime(lastTime: (Long) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                lastTime(pointDatabaseDao.getLastTrack().time)
            }
        }
    }


}