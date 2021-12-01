package com.gapps.usuarios.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gapps.usuarios.common.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}