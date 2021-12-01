package com.gapps.usuarios

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gapps.usuarios.common.database.UserDatabase

class UserApplication : Application(){

    companion object {
        lateinit var database: UserDatabase
    }

    override fun onCreate() {
        super.onCreate()

//        val MIGRATION_1_2 = object : Migration(1, 2){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE UserEntity ADD COLUMN photoImg TEXT NOT NULL DEFAULT ''")
//            }
//        }

        database = Room.databaseBuilder(this,
            UserDatabase::class.java,
            "UserDatabase")
            .build()
    }

}