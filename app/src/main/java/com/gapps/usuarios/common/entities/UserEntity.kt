package com.gapps.usuarios.common.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserEntity")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var apellido: String,
    var telefono: Long,
    var correo: String,
    var latitud: String,
    var longitud: String,
    var photoImg: String){

    constructor() : this(name = "", apellido = "", telefono = 0, correo = "", latitud = "", longitud = "", photoImg = "")

    fun getFullName(): String = "$name $apellido"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}




