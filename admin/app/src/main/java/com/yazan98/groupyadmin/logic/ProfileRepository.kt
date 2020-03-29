package com.yazan98.groupyadmin.logic


import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yazan98.groupyadmin.models.LoginModel
import com.yazan98.groupyadmin.models.Profile
import io.vortex.android.prefs.VortexPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                                                    listener.onOperationSuccess(
                                                        Profile(
                                                        id = it.getString("id"),
                                                        accountType = it.getString("accountType"),
                                                        name = it.getString("name"),
                                                        email = it.getString("email"),
                                                        groupID = it.getString("groupID")
                                                    )
                                                    )
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

    suspend fun getProfileById(id: String) {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("users")
                .document(id).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        GlobalScope.launch {
                            it.result?.let {
                                listener.onOperationSuccess(Profile(
                                    id = it.getString("id"),
                                    accountType = it.getString("accountType"),
                                    bio = it.getString("bio"),
                                    email = it.getString("email"),
                                    name = it.getString("name"),
                                    image = ""
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

    suspend fun addLocationInfo(lastDetailsTaken: LatLng) {
        withContext(Dispatchers.IO) {
            VortexPrefs.get("UserID" , "")?.also {
                val details = HashMap<String , Any>()
                details["lat"] = lastDetailsTaken.latitude
                details["lng"] = lastDetailsTaken.longitude
                FirebaseFirestore.getInstance().collection("users").document(it as String)
                    .update(details).addOnCompleteListener {
                        GlobalScope.launch {
                            listener.onOperationSuccess(Profile())
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