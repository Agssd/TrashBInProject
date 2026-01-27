package com.example.trashbinproject.domain

data class UserResponse(
    val id: Int,
    val username: String,
    val points: Int,
    val name: String
)