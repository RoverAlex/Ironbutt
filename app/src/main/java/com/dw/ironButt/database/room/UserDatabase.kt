package com.denisovdw.ironbutt.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.denisovdw.ironbutt.utils.myLog


@Database(entities = [UserList::class,PointLocation::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDatabaseDao :UserDatabaseDao
    abstract val pointDatabaseDao :PointLocationDao

    companion object {

        @Volatile
        private var INSTANCE_USER: UserDatabase? = null

        fun getInstanceUser(context: Context):UserDatabase {

            synchronized(this){
                var instace = INSTANCE_USER
                if (instace == null){
                    myLog("INSTANCE Dao")
                    instace = Room.databaseBuilder(context.applicationContext,UserDatabase::class.java,
                        "user_app_database"
                    )
                        //.allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE_USER = instace
                }
                return instace
            }
        }


    }
}