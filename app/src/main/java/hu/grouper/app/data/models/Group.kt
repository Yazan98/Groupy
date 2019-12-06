package hu.grouper.app.data.models

import java.io.Serializable

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 1:34 PM
 */

data class Group(
        var name: String? = "",
        var id: String? = "",
        var adminID: String? = "",
        var members: Array<String>?
): Serializable