package com.gapps.usuarios.addModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gapps.usuarios.addModule.model.AddInteractor
import com.gapps.usuarios.common.entities.UserEntity

class AddViewModel : ViewModel() {
    private val userSelected = MutableLiveData<UserEntity>()
    private val result = MutableLiveData<Any>()
    private val interactor: AddInteractor

    init {
        interactor = AddInteractor()
    }

    fun setUserSelected(storeEntity: UserEntity){
        userSelected.value = storeEntity
    }

    fun getUserSelected(): LiveData<UserEntity>{
        return userSelected
    }

    fun setResult(value: Any){
        result.value = value
    }

    fun getResult(): LiveData<Any> {
        return result
    }

    fun saveUser(userEntity: UserEntity){
        interactor.addUser(userEntity) { newId ->
            result.value = newId
        }
    }
}