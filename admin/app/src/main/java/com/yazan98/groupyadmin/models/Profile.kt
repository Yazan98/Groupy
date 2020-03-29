package com.yazan98.groupyadmin.models


data class Profile(
    var id: String? = "",
    var email: String? = "",
    var name: String? = "",
    var image: String? = "",
    var accountType: String? = "",
    var bio: String? = "",
    var lat: Double? = 0.0,
    var lng: Double? = 0.0,
    var groupID: String? = ""
)