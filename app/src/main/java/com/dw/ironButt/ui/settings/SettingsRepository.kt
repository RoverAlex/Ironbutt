package com.dw.ironButt.ui.settings

import com.dw.ironButt.utils.myLog
import com.dw.ironButt.database.room.UserDatabaseDao
import com.dw.ironButt.database.room.UserList
import kotlinx.coroutines.*


class SettingsRepository(
    private val userDatabaseDao: UserDatabaseDao
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