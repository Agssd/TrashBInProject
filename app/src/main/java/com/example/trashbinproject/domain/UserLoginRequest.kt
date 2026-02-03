package com.example.trashbinproject.domain

data class UserLoginRequest(
    val login: String,
    val password: String
)