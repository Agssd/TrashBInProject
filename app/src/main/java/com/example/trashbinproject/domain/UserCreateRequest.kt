package com.example.trashbinproject.domain

data class UserCreateRequest(
    val username: String,
    val password: String,
    val name: String
)