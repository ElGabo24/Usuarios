package com.gapps.usuarios.mainModule.model

import com.gapps.usuarios.UserApplication
import com.gapps.usuarios.common.entities.UserEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {

    fun getUsers(callback: (MutableList<UserEntity>) -> Unit){
        doAsync{
            val userList = UserApplication.database.userDao().getAllUsers()
            uiThread{
                callback(userList)
            }
        }
    }
}