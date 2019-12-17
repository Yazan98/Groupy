package hu.grouper.app.data.models

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.sql.Date

/**
 * Created By : Yazan Tarifi
 * Date : 12/17/2019
 * Time : 12:53 PM
 */

data class Message(
    var messageId: String = "",
    var name: String = "",
    var message: String = "",
    var date: Date,
    var senderId: String = ""
): IMessage {
    override fun getId(): String {
        return messageId
    }

    override fun getCreatedAt(): Date {
        return date
    }

    override fun getUser(): IUser {
        return object : IUser {
            override fun getAvatar(): String {
                return ""
            }

            override fun getName(): String {
                return name
            }

            override fun getId(): String {
                return senderId
            }

        }
    }

    override fun getText(): String {
        return message
    }

}