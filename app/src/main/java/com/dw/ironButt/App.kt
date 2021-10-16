package com.dw.ironButt

import android.app.Application
import android.content.res.Resources
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dw.ironButt.database.api.ApiService
import com.dw.ironButt.database.room.PointLocationDao
import com.dw.ironButt.database.room.UserDatabase
import com.dw.ironButt.database.room.UserDatabaseDao
import com.dw.ironButt.ui.finish.FinishRepository
import com.dw.ironButt.ui.login.LoginRepository
import com.dw.ironButt.ui.settings.SettingsRepository
import com.dw.ironButt.ui.way.WayRepository
import com.dw.ironButt.utils.LocationRUpdate
import com.dw.ironButt.utils.SharedPrefsManager
import com.dw.ironButt.utils.TokenConstant.Companion.SERVER_URL
import com.dw.ironButt.utils.myLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        lateinit var loginRepository: LoginRepository
        lateinit var settingsRepository: SettingsRepository
        lateinit var finishRepository: FinishRepository
        lateinit var wayRepository: WayRepository

        lateinit var sharedPrefsManager: SharedPrefsManager

        lateinit var appResources: Resources

        lateinit var apiService: ApiService
        lateinit var okHttpClient: OkHttpClient

        lateinit var userDatabaseDao: UserDatabaseDao
        lateinit var pointDatabaseDao: PointLocationDao

        private val _currentLocation = MutableLiveData<Location>()
        val currentLocation: LiveData<Location> get() = _currentLocation
        val startStopLocation = MutableLiveData<Boolean>()

    }

    private lateinit var locationRUpdate: LocationRUpdate

    override fun onCreate() {
        super.onCreate()
        //FirebaseApp.initializeApp(this@App)
        locationRUpdate = LocationRUpdate(this)
        myLog("onCreate App")
        sharedPrefsManager = SharedPrefsManager(this)



        startStopLocation.observeForever {
            if (it) {
                locationRUpdate.start()
            } else {
                locationRUpdate.stop()
            }
        }
        initDAo()
        initLocation()
        appResources = resources

        initRetrofit()
        initOkHttp()

        initLoginInteract()
        initMapInteract()
        initUserInteract()
        initFinishInteract()

    }


    private fun initLocation() {
        locationRUpdate.locationNow.observeForever {
            //myLog("App locationNow ")
            _currentLocation.value = it
        }
    }

    private fun initUserInteract() {
        settingsRepository = SettingsRepository(userDatabaseDao)
    }


    private fun initLoginInteract() {
        loginRepository = LoginRepository(apiService, userDatabaseDao, sharedPrefsManager)
    }

    private fun initMapInteract() {
        wayRepository = WayRepository(pointDatabaseDao, userDatabaseDao)
    }

    private fun initFinishInteract() {
        finishRepository = FinishRepository(
            apiService,
            pointDatabaseDao,
            userDatabaseDao
        )

    }

    private fun initOkHttp() {
        val httpLongIterator = HttpLoggingInterceptor()
        httpLongIterator.level = HttpLoggingInterceptor.Level.BODY

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLongIterator)
//            .connectTimeout("", TimeUnit.SECONDS)
//            .readTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    private fun initRetrofit() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        apiService = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
        myLog("initRetrofit")

    }

    private fun initDAo() {
        userDatabaseDao = UserDatabase.getInstanceUser(this).userDatabaseDao
        pointDatabaseDao = UserDatabase.getInstanceUser(this).pointDatabaseDao

    }


}