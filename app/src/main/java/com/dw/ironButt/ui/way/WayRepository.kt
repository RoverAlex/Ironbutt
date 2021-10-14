package com.dw.ironButt.ui.way


import androidx.lifecycle.LiveData
import com.dw.ironButt.database.room.PointLocation
import com.dw.ironButt.database.room.PointLocationDao
import com.dw.ironButt.database.room.UserDatabaseDao
import com.dw.ironButt.database.room.UserList
import com.dw.ironButt.utils.myLog
import kotlinx.coroutines.*

class WayRepository(
    private val pointDatabaseDao: PointLocationDao,
    private val userDatabaseDao: UserDatabaseDao
) {
    val mJob = Job()
    val mUiScope = CoroutineScope(Dispatchers.Main + mJob)


    fun getLastTime(lastTime: (Long) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                if (pointDatabaseDao.getListPoints().isNotEmpty())
                lastTime(pointDatabaseDao.getLastTrack().time)
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

    fun savePoint(pointLocation: PointLocation, isSuccessSave: (Boolean) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                val result = pointDatabaseDao.insert(pointLocation)
                withContext(Dispatchers.Main) {
                    isSuccessSave(result > 0)
                }
            }
        }
    }

    fun deleteBaseTrack() {
        try {
            mUiScope.launch {
                withContext(Dispatchers.IO) {
                    pointDatabaseDao.clearALL()
                }
            }
        } catch (e: Exception) {
            myLog("deleteBase  $e")
        }

    }

    fun updateStartUser(newUserListData: UserList, resultUpdate :(Int)->Unit){
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                resultUpdate (userDatabaseDao.update(newUserListData))
            }
        }
    }

    fun getUserList(userList: (UserList) -> Unit) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                userList( userDatabaseDao.getUserList())
            }
        }
    }

    fun upDataUserList(userList: UserList) {
        mUiScope.launch {
            withContext(Dispatchers.IO) {
                userDatabaseDao.update(userList)
            }
        }
    }

}