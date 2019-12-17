package hu.grouper.app.holders

import android.view.View
import com.stfalcon.chatkit.messages.MessageHolders
import hu.grouper.app.data.models.Message
import kotlinx.android.synthetic.main.item_custom_outcoming_text_message.view.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/17/2019
 * Time : 6:01 PM
 */

class CustomOutcomingTextMessageViewHolder(itemView: View) :
        MessageHolders.OutcomingTextMessageViewHolder<Message>(itemView)  {

    val messageText = itemView.MessageOutComing
    val name = itemView.NameOut

    override fun onBind(message: Message?) {
        println("Message Name : ${message!!.senderName}")
        println("Message Name : ${message!!.message}")
        message?.let { mes ->

            messageText?.let {
                it.text = mes.message
            }

            name?.let {
                it.text = mes.senderName
            }
        }
    }
}