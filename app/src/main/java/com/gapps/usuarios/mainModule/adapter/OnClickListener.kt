package com.gapps.usuarios.mainModule.adapter

import com.gapps.usuarios.common.entities.UserEntity

interface OnClickListener {
    fun onClick(userEntity: UserEntity)
}