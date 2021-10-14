package com.dw.ironButt.ui.login


import com.dw.ironButt.database.api.CheckTokenJson
import com.dw.ironButt.database.api.ApiService
import com.dw.ironButt.database.room.UserDatabaseDao
import com.dw.ironButt.database.room.UserList
import com.dw.ironButt.utils.SharedPrefsManager
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_SETTINGS
import com.dw.ironButt.utils.myLog
import com.dw.ironButt.utils.TokenConstant.Companion.TOKEN
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(
    private val apiService: ApiService,
    private val userDatabaseDao: UserDatabaseDao,
    private val pref: SharedPrefsManager
) {
    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)



    fun checkToken(result: (CheckTokenJson) -> Unit) {
        val call = apiService.checkToken(TOKEN)
        call.enqueue(object : Callback<CheckTokenJson> {
            override fun onResponse(
                call: Call<CheckTokenJson>,
                response: Response<CheckTokenJson>
            ) {
                result(response.body()!!)
            }

            override fun onFailure(call: Call<CheckTokenJson>, t: Throwable) {
                val error = CheckTokenJson()
                result(error)
                myLog("onFailure $call")
            }

        })

    }

    fun authUser(userList: UserList, callback: (Boolean) -> Unit) {

        mUiScope.launch {
            withContext(Dispatchers.IO) {
                pref.setUserName(userList.fullName)
                pref.setStateApplication(STATE_PRESENTER_SETTINGS)

                userDatabaseDao.clearALL()
                val result = userDatabaseDao.insert(userList)
                if (result >= 0) {
                    myLog("authorLk user ${userList.authorLk}")
                    callback(true)
                } else {
                    callback(false)
                }

            }
        }
    }

}