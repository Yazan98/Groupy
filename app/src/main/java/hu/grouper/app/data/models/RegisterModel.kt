package hu.grouper.app.data.models

import java.io.Serializable

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 6:37 AM
 */

data class RegisterModel(
    var profile: Profile,
    var passwrd: String? = ""
): Serializable
