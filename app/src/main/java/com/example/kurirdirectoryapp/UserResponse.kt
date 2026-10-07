package com.example.kurirdirectoryapp

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("company")
    val company: CompanyInfo
)

data class CompanyInfo(
    @SerializedName("name")
    val companyName: String
)