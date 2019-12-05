package hu.grouper.app.data.models

import java.io.Serializable

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 6:38 AM
 */

data class LoginModel(
    var email: String,
    var password: String
): Serializable
