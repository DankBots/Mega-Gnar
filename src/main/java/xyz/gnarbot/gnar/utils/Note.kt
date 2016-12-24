package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.handlers.members.User
import xyz.gnarbot.gnar.handlers.servers.Host
import java.awt.Color

/**
 * Gnar's wrapper class for JDA's [Message].
 *
 * @see Message
 */
class Note(val host : Host, private val message : Message) : Message by message
{
    /**
     * The author of this Message as a [User] instance.
     *
     * @return Message author as User.
     */
    override fun getAuthor() : User? = host.userHandler.asUser(message.author)
    
    /**
     * Get mentioned users of this Message as [User] instances.
     *
     * @return Immutable list of mentioned [User] instances.
     */
    override fun getMentionedUsers() : List<User> = message.mentionedUsers.map { host.userHandler.asUser(it) }
    
    /**
     * Stylized quick-reply to a message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun reply(msg : String) = Note(host, channel.sendMessage("__**${message.author.name}**__ \u279c $msg").block())
    
    /**
     * Quick-reply to a message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyRaw(msg : String) = Note(host, channel.sendMessage(msg).block())
    
    /**
     * Send an embeded message..
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyEmbed(title : String, msg : String, color : Color)
    {
        replyEmbed(title, msg, color, null)
    }
    
    /**
     * Send an embeded message..
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyEmbed(title : String, msg : String, color : Color, thumb : String?)
    {
        replyEmbed(title, msg, color, thumb, null)
    }
    
    /**
     * Send an embeded message..
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyEmbed(title : String, msg : String, color : Color, thumb : String?, img : String?)
    {
        val embed = EmbedBuilder().run {
            //setDescription("${message.author.name}" + msg)
            setDescription(msg)
            setTitle(title)
            setColor(color)
            
            if (!thumb.isNullOrBlank())
                setThumbnail(thumb)
            
            if (!img.isNullOrBlank())
                setImage(img)
            
            build()
        }
        channel.sendMessage(embed).queue()
    }

    fun replyEmbed(title: String, msg : String, color: Color, image: String, imageNotThumb: Boolean)
    {
        val eb = EmbedBuilder()
        eb.setDescription("__**${message.author.name}**__ \u279c" + msg)
        eb.setTitle(title)
        eb.setColor(color)
        if (imageNotThumb) {
            eb.setImage(image)
        }else{
            eb.setThumbnail(image)
        }
        val embed = eb.build()
        channel.sendMessage(embed).queue()
    }

    /**
     * Send an embeded message..
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyEmbedRaw(title: String, msg : String, color: Color)
    {
        val eb = EmbedBuilder()
        eb.setDescription(msg)
        eb.setTitle(title)
        eb.setColor(color)
        val embed = eb.build()
        channel.sendMessage(embed).queue()
    }

    /**
     * Send an embeded message..
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyEmbedRaw(title: String, msg : String, color: Color, thumbnail: String)
    {
        val eb = EmbedBuilder()
        eb.setDescription(msg)
        eb.setTitle(title)
        eb.setColor(color)
        eb.setThumbnail(thumbnail)
        val embed = eb.build()
        channel.sendMessage(embed).queue()
    }

    /**
     * Send an embeded message..
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyEmbedRaw(title: String, msg : String, color: Color, thumbnail: String, image: String)
    {
        val eb = EmbedBuilder()
        eb.setDescription( msg)
        eb.setTitle(title)
        eb.setColor(color)
        if (thumbnail != null){
            eb.setThumbnail(thumbnail)
        }
        eb.setImage(image)
        val embed = eb.build()
        channel.sendMessage(embed).queue()
    }

    fun replyEmbedRaw(title: String, msg : String, color: Color, image: String, imageNotThumb: Boolean)
    {
        val eb = EmbedBuilder()
        eb.setDescription(msg)
        eb.setTitle(title)
        eb.setColor(color)
        if (imageNotThumb) {
            eb.setImage(image)
        }else{
            eb.setThumbnail(image)
        }
        val embed = eb.build()
        channel.sendMessage(embed).queue()
    }

    fun delete() : Boolean
    {
        try
        {
            deleteMessage().block()
            return true
        }
        catch(e : PermissionException)
        {
            return false
        }
    }
    
    /**
     * @return String representation of the note.
     */
    override fun toString() = "Note(id=$id, author=${author?.name}, content=\"$content\")"
}
