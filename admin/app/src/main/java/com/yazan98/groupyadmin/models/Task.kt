package com.yazan98.groupyadmin.models

data class Task(
    var id: String? = "",
    var name: String? = "",
    var status: String? = "NEW",
    var userId: String? = "",
    var groupId: String? = ""
)