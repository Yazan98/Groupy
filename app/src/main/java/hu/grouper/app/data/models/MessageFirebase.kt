package hu.grouper.app.data.models

import java.util.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/17/2019
 * Time : 12:53 PM
 */

data class MessageFirebase(
    var name: String? = "",
    var message: String? = "",
    var date: Date? = Date(),
    var id: String? = "",
    var senderId: String? = ""
)
