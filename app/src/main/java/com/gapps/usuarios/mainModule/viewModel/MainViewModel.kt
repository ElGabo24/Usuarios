package com.gapps.usuarios.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gapps.usuarios.common.entities.UserEntity
import com.gapps.usuarios.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {
    private var userList:MutableList<UserEntity>
    private var interactor: MainInteractor

    init {
        userList = mutableListOf()
        interactor = MainInteractor()
    }

    private val users: MutableLiveData<List<UserEntity>> by lazy {
        MutableLiveData<List<UserEntity>>().also {
            loadStores()
        }
    }

    fun getUsers():LiveData<List<UserEntity>>{
        return users
    }

    private fun loadStores() {
        interactor.getUsers {
            users.value = it
            userList = it
        }
    }
}