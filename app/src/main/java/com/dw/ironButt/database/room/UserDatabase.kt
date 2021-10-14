package com.dw.ironButt.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dw.ironButt.utils.myLog


@Database(entities = [UserList::class, PointLocation::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDatabaseDao : UserDatabaseDao
    abstract val pointDatabaseDao : PointLocationDao

    companion object {

        @Volatile
        private var INSTANCE_USER: UserDatabase? = null

        fun getInstanceUser(context: Context): UserDatabase {

            synchronized(this){
                var instance = INSTANCE_USER
                if (instance == null){
                    myLog("INSTANCE Dao")
                    instance = Room.databaseBuilder(context.applicationContext, UserDatabase::class.java,
                        "user_app_database"
                    )
                        //.allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE_USER = instance
                }
                return instance
            }
        }


    }
}