package com.gapps.usuarios.addModule.model

import com.gapps.usuarios.UserApplication
import com.gapps.usuarios.common.entities.UserEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddInteractor {

    fun addUser(userEntity: UserEntity, callback: (Long) -> Unit){
        doAsync {
            val newId = UserApplication.database.userDao().addUser(userEntity)
            uiThread {
                callback(newId)
            }
        }
    }
}