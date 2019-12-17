package hu.grouper.app.screens

import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import hu.grouper.app.R
import hu.grouper.app.XOR
import hu.grouper.app.data.models.Message
import hu.grouper.app.data.models.MessageFirebase
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
            initAdapter(VortexPrefs.get("userID", "") as String)
            (VortexPrefs.get("groupID", "") as String).let {
                FirebaseFirestore.getInstance().collection("groups")
                    .document(it).collection("messages")
                    .orderBy("date").get().addOnCompleteListener {
                        it.result?.let {
                            for (doc in it.documents) {
                                GlobalScope.launch {
                                    getMessageById(doc.id)
                                }
                            }
                        }
                    }
            }
        }
    }

    private suspend fun initAdapter(id: String) {
        withContext(Dispatchers.Main) {
            adapter = MessagesListAdapter(id, ImageLoader { imageView, url, payload -> })
            messagesList?.setAdapter(adapter)
        }
    }

    private suspend fun getMessageById(id: String) {
        withContext(Dispatchers.IO) {
            (VortexPrefs.get("groupID", "") as String).let {
                FirebaseFirestore.getInstance().collection("groups")
                    .document(it).collection("messages").document(id)
                    .get().addOnCompleteListener {
                        it.result?.let {
                            GlobalScope.launch {
                                showMessage(
                                    Message(
                                        messageId = it.getString("messageId")!!,
                                        senderId = it.getString("senderId")!!,
                                        message = it.getString("message")!!,
                                        date = (it.getDate("date") as java.sql.Date?)!!,
                                        name = it.getString("name")!!
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private suspend fun showMessage(message: Message) {
        withContext(Dispatchers.Main) {
            adapter.addToStart(message , true)
        }
    }

    override fun onAddAttachments() {

    }

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
            (VortexPrefs.get("groupID", "") as String).let {
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