package hu.grouper.app.holders

import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders
import hu.grouper.app.data.models.Message
import kotlinx.android.synthetic.main.item_custom_incoming_text_message.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/17/2019
 * Time : 6:01 PM
 */

class CustomIncomingTextMessageViewHolder(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<Message>(itemView) {

    val name = itemView.Name
    val messageText = itemView.MessageInComing

    override fun onBind(message: Message?) {
        println("Message Name : ${message!!.senderName}")
        println("Message Name : ${message!!.message}")
        message?.let { m ->
            println("Message Name : ${m.senderName}")
            println("Message Name : ${m.message}")
            name?.let {
                it.text = m.senderName
            }

            messageText?.let {
                it.text = m.message
            }
        }
    }

}