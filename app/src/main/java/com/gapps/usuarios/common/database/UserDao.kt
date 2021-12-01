package com.gapps.usuarios.common.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gapps.usuarios.common.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity")
    fun getAllUsers() : MutableList<UserEntity>

    @Query("SELECT * FROM UserEntity WHERE id = :id")
    fun getUserById(id: Long) : UserEntity

    @Insert
    fun addUser(userEntity: UserEntity): Long
}