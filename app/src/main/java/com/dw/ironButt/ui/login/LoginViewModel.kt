package com.dw.ironButt.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dw.ironButt.database.api.CheckTokenJson
import com.dw.ironButt.database.room.UserList
import com.dw.ironButt.App


class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val loginInteract = App.loginRepository
    private val _errorLiveData = MutableLiveData<String>()
    private val _authenticated = MutableLiveData<Boolean>()

    private val _flagClickable = MutableLiveData(false)
    val flagClickableAndHide: LiveData<Boolean?> get() = _flagClickable
    var infoToken = MutableLiveData<String>()

    fun getAuthenticated(flag: Boolean) {
        if (flag) {
            _authenticated.value = true
            _flagClickable.value = false
        } else {
            _authenticated.value = false
            _flagClickable.value = true
        }
    }


    fun getError(): LiveData<String?> {
        return _errorLiveData
    }

    fun chekToken(result: (CheckTokenJson) -> Unit ){
        loginInteract.checkToken(result)
    }

    fun authUser(userList: UserList, callback:(Boolean)->Unit){
        loginInteract.authUser(userList){
            callback(it)
        }
    }


}