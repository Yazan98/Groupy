package hu.grouper.app.adapter

import java.io.Serializable

/**
 * Created By : Yazan Tarifi
 * Date : 12/7/2019
 * Time : 8:55 AM
 */

data class JoinRequest(
        var id: String? = "",
        var name: String? = "",
        var userId: String? = "",
        var groupID: String? = ""
): Serializable
