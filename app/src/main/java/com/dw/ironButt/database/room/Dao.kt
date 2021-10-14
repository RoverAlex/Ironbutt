package com.denisovdw.ironbutt.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDatabaseDao {
    @Insert
    fun insert(userInfoList: UserList):Long

    @Update
    fun update(userInfoList: UserList):Int


    @Query("DELETE from table_user")
    fun clearALL()

//    @Query("SELECT uIdRoot FROM table_user WHERE id = 0")
//    fun getUIdRoot ():String

    @Query("SELECT COUNT(*) FROM table_user")
    fun getCount():Int

    @Query("SELECT * FROM table_user WHERE id = 0")
    fun getUserList ():UserList

}
@Dao
interface PointLocationDao{
    @Insert
    fun insert(point: PointLocation):Long

    @Query("DELETE from table_Track")
    fun clearALL()

    @Query("SELECT * FROM table_Track ORDER BY id DESC")
    fun getListPoints(): List<PointLocation>

    @Query("SELECT * FROM table_Track ORDER BY id DESC")
    fun getAllPoints(): LiveData<List<PointLocation>>

    @Query("SELECT * FROM table_Track ORDER BY id DESC")
    fun getResultAllPoints(): List<PointLocation>


    @Query("SELECT * FROM table_Track ORDER BY id DESC LIMIT 1")
    fun getLastTrack(): PointLocation

    @Query("DELETE  FROM table_Track WHERE infoTrack = :infoTrack")
    fun deleteFinish(infoTrack:String)

}