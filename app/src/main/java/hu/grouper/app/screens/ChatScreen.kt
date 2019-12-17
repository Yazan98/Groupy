package hu.grouper.app.screens

import android.os.Bundle
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import hu.grouper.app.R
import hu.grouper.app.XOR
import hu.grouper.app.data.models.Message
import hu.grouper.app.data.models.MessageFirebase
import hu.grouper.app.holders.CustomIncomingTextMessageViewHolder
import hu.grouper.app.holders.CustomOutcomingTextMessageViewHolder
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/14/2019
 * Time : 2:03 PM
 */

class ChatScreen : VortexScreen(), MessageInput.AttachmentsListener, MessageInput.InputListener {

    private lateinit var adapter: MessagesListAdapter<Message>
    override fun getLayoutRes(): Int {
        return R.layout.screen_chat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        input.setInputListener(this)
        GlobalScope.launch {
            initAdapter(VortexPrefs.get("UserID", "") as String)
            val id = VortexPrefs.get("GroupID", "") as String
            println("Vortex ID : $id")
            FirebaseFirestore.getInstance().collection("groups")
                    .document(id).collection("messages").orderBy("date")
                    .get().addOnCompleteListener {
                        it.result?.let {
                            for (doc in it.documents) {
                                GlobalScope.launch {
                                    getMessageById(doc.id)
                                }
                            }
                        }
                    }

            startTriggerMessages(id)
        }
    }

    private suspend fun initAdapter(id: String) {
        withContext(Dispatchers.Main) {

            val holdersConfig = MessageHolders()
                    .setIncomingTextConfig(
                            CustomIncomingTextMessageViewHolder::class.java,
                            R.layout.item_custom_incoming_text_message
                    )
                    .setOutcomingTextConfig(
                            CustomOutcomingTextMessageViewHolder::class.java,
                            R.layout.item_custom_outcoming_text_message
                    )

            adapter = MessagesListAdapter(id, holdersConfig, ImageLoader { imageView, url, payload -> })
            messagesList?.setAdapter(adapter)
        }
    }

    private suspend fun getMessageById(id: String) {
        println("Firebase Doc id : $id")
        withContext(Dispatchers.IO) {
            (VortexPrefs.get("GroupID", "") as String).let {
                FirebaseFirestore.getInstance().collection("groups")
                        .document(it).collection("messages").document(id)
                        .get().addOnCompleteListener {
                            it.result?.let {
                                GlobalScope.launch {
                                    println("Firebase Message is : ${it.getString("message")!!}")
                                    showMessage(
                                            Message(
                                                    messageId = it.getString("id")!!,
                                                    senderId = it.getString("senderId")!!,
                                                    message = XOR.encryptDecrypt(it.getString("message")!!),
                                                    date = it.getDate("date")!!,
                                                    senderName = it.getString("name")!!
                                            )
                                    )
                                }
                            }
                        }
            }
        }
    }

    private suspend fun startTriggerMessages(groupId: String) {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("groups")
                    .document(groupId).collection("messages")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            println("Firebase Listener : Error " + firebaseFirestoreException.message)
                            return@addSnapshotListener
                        } else {
                            println("Firebase Triggered New Collection")
                            querySnapshot?.documentChanges?.let {
                                for (item in it.iterator()) {
                                    if (item.type == DocumentChange.Type.ADDED) {
                                        println("Firebase Triggered New Collection : Add")
                                        println("Firebase Snapshot : ${item.document}")
                                        item.document.id?.let {
                                            GlobalScope.launch {
                                                getMessageById(it)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
        }
    }

    private suspend fun showMessage(message: Message) {
        withContext(Dispatchers.Main) {
            println("Firebase Message :  is : ${message.message}")
            adapter.addToStart(message, true)
        }
    }

    override fun onAddAttachments() = Unit
    override fun onSubmit(input: CharSequence?): Boolean {
        GlobalScope.launch {
            submitMessage(input.toString())
        }
        return true
    }

    private suspend fun submitMessage(message: String) {
        withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            val name = VortexPrefs.get("Name", "") as String
            val userId = VortexPrefs.get("UserID", "") as String
            (VortexPrefs.get("GroupID", "") as String).let {
                FirebaseFirestore.getInstance().collection("groups")
                        .document(it).collection("messages")
                        .document(id).set(
                                MessageFirebase(
                                        id = id,
                                        name = name,
                                        date = Date(),
                                        message = XOR.encryptDecrypt(message),
                                        senderId = userId
                                )
                        )
            }
        }
    }
}
