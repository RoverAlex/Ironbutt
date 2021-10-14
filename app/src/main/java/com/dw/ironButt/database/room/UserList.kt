package com.denisovdw.ironbutt.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_user")
class UserList {
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L

    @ColumnInfo(name = "authorLk")
    var authorLk: Int = 18978

    @ColumnInfo(name = "uIdRoot")
    var uIdRoot: String? = null

    @ColumnInfo(name = "email")
    var email: String = "alexej.deni@yandex.ru"

    @ColumnInfo(name = "fullName")
    var fullName: String = ""

    @ColumnInfo(name = "driver")
    var driver: Boolean = true

    @ColumnInfo(name = "moto")
    var moto: Boolean = true

    @ColumnInfo(name = "power")
    var power: Boolean = true

    @ColumnInfo(name = "marka")
    var marka: String = ""

    @ColumnInfo(name = "countryStart")
    var countryCity: String = ""

    @ColumnInfo(name = "odometerStart")
    var odometerStart: Int = 0

    @ColumnInfo(name = "odometerFinish")
    var odometerFinish: String = ""

    @ColumnInfo(name = "totalTime")
    var totalTime: String = ""

}

@Entity(tableName = "table_Track")
class PointLocation {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    @ColumnInfo(name = "unIdRoot")
    var unIdRoot: String? = null
    @ColumnInfo(name = "infoTrack")
    var infoTrack: String? = null
    @ColumnInfo(name = "time")
    var time: Long = 0
    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0
    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0
    @ColumnInfo(name = "odometer")
    var odometer: Int = 0

}




