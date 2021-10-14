package com.denisovdw.ironbutt.presrntation.view.login.web


import com.denisovdw.ironbutt.CheckTokenJson
import com.denisovdw.ironbutt.database.api.ApiService
import com.denisovdw.ironbutt.database.room.UserDatabaseDao
import com.denisovdw.ironbutt.database.room.UserList
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.TOKEN
import com.denisovdw.ironbutt.utils.SharedPrefsManager
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_SETTINGS
import com.denisovdw.ironbutt.utils.myLog
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
                myLog("onFailure " + call.toString())
            }

        })

    }

    fun authUser(userList: UserList, callback: (Boolean) -> Unit) {

        mUiScope.launch {
            withContext(Dispatchers.IO) {
                pref.setAuthorLk(userList.authorLk)
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