package hu.grouper.app.data.models

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 5:56 PM
 */

data class Task(
        var id: String? = "",
        var name: String? = "",
        var status: String? = "NEW",
        var userId: String? = "",
        var groupId: String? = ""
)