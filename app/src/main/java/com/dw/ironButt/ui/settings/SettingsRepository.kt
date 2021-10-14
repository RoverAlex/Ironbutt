package com.dw.ironButt.ui.settings

import com.denisovdw.ironbutt.database.room.PointLocationDao
import com.denisovdw.ironbutt.database.room.UserDatabaseDao
import com.denisovdw.ironbutt.database.room.UserList
import com.denisovdw.ironbutt.utils.SharedPrefsManager
import com.denisovdw.ironbutt.utils.myLog
import kotlinx.coroutines.*


class SettingsRepository(
    private val userDatabaseDao: UserDatabaseDao,
    private val pointDatabaseDao: PointLocationDao,
    private val pref: SharedPrefsManager
) {
    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)

    fun getUserList(userList: (UserList)->Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                userList( userDatabaseDao.getUserList())
            }
        }

    }

    fun uppDataBase(userList: UserList, result: (Boolean) -> Unit) {

        try {
            mUiScope.launch {
                withContext(Dispatchers.IO) {
                    userDatabaseDao.update(userList)
                    result(true)
                }
            }
        } catch (e: Exception) {
            result(false)
            myLog("RecordService exception $e")
        }


    }





}