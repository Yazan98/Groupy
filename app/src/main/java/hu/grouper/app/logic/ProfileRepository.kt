package hu.grouper.app.logic

import com.google.firebase.auth.FirebaseAuth
import hu.grouper.app.data.models.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 7:02 AM
 */

class ProfileRepository(private val listener: ProfileListener) {

    suspend fun login(email: String, password: String) {
        withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            GlobalScope.launch {
                                it.result?.let {
                                    listener.onOperationSuccess(Profile(
                                            id = it.user?.uid
                                    ))
                                }
                            }
                        } else {
                            GlobalScope.launch {
                                it.exception?.message?.let {
                                    listener.onOperationFailed(it)
                                }
                            }
                        }
                    }
        }
    }

    interface ProfileListener {
        suspend fun onOperationSuccess(profile: Profile)
        suspend fun onOperationFailed(message: String)
    }

}
