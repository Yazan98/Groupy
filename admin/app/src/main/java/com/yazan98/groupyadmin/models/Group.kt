package com.yazan98.groupyadmin.models

data class Group(
    var name: String? = "",
    var id: String? = "",
    var adminID: String? = "",
    var members: List<String>
)
