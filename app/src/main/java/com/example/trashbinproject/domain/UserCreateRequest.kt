package com.example.trashbinproject.domain

data class UserCreateRequest(
    val username: String,
    val login: String,
    val password: String
)