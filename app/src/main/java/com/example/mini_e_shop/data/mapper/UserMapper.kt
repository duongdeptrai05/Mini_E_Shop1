package com.example.mini_e_shop.data.mapper

import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.domain.model.User

fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        email = this.email,
        passwordHash = this.passwordHash,
        name = this.name,
        role = this.role
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email,
        passwordHash = this.passwordHash,
        name = this.name,
        role = this.role
    )
}
