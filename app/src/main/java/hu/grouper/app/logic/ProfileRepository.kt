package hu.grouper.app.logic

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.data.models.LoginModel
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

    suspend fun login(model: LoginModel) {
        withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(model.email, model.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            GlobalScope.launch {
                                it.result?.let {
                                    it.user?.uid?.let {
                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(it).get().addOnCompleteListener {
                                                    GlobalScope.launch {
                                                        it.result?.let {
                                                            listener.onOperationSuccess(Profile(
                                                                    id = it.getString("id"),
                                                                    accountType = it.getString("accountType")
                                                            ))
                                                        }
                                                    }
                                                }
                                    }
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

    suspend fun register(profile: Profile, password: String) {
        withContext(Dispatchers.IO) {
            profile.email?.let {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(it, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                GlobalScope.launch {
                                    it.result?.let {
                                        it.user?.uid?.let {
                                            profile.id = it
                                            FirebaseFirestore.getInstance().collection("users")
                                                    .document(it).set(profile).addOnCompleteListener {
                                                        GlobalScope.launch { listener.onOperationSuccess(profile) }
                                                    }
                                        }
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
    }

    interface ProfileListener {
        suspend fun onOperationSuccess(profile: Profile)
        suspend fun onOperationFailed(message: String)
    }

}
