package hu.grouper.app.data.models

import java.io.Serializable

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 6:36 AM
 */

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
) : Serializable
