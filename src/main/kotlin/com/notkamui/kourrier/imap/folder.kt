package com.notkamui.kourrier.imap

import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage
import javax.mail.FetchProfile
import javax.mail.Folder
import javax.mail.Message

/**
 * Wrapper around the standard [IMAPFolder].
 */
class KourrierFolder(private val imapFolder: IMAPFolder) {
    private var profile = FetchProfile()

    /**
     * Amount of messages in the current folder.
     */
    val messageCount: Int
        get() = imapFolder.messageCount

    /**
     * Amount of unread messages in the current folder.
     */
    val unreadCount: Int
        get() = imapFolder.unreadMessageCount

    /**
     * Amount of new messages in the current folder.
     */
    val newCount: Int
        get() = imapFolder.newMessageCount

    /**
     * Is true if the current folder contains at least one new message.
     */
    val hasNewMessages: Boolean
        get() = imapFolder.hasNewMessages()

    /**
     * The [KourrierFolderType] of the current folder.
     */
    val folderType: KourrierFolderType
        get() = KourrierFolderType.fromRawFolderType(imapFolder.type)

    /**
     * Closes the current [IMAPFolder], and [expunge]s it or not (defaults to false).
     */
    fun close(expunge: Boolean = false) {
        imapFolder.close(expunge)
    }

    /**
     * Obtains the message at the given [index],
     * or null if it doesn't exist.
     */
    operator fun get(index: Int): KourrierMessage? = imapFolder[index]

    /**
     * Obtains the list of message from the given [range] of index,
     * or null if one of them doesn't exist.
     * May [prefetch] the results (defaults to true).
     */
    operator fun get(range: IntRange, prefetch: Boolean = true): List<KourrierMessage>? {
        val messages: Array<out Message> = try {
            imapFolder.getMessages(range.toList().toIntArray())
        } catch (e: IndexOutOfBoundsException) {
            null
        } ?: return null
        if (prefetch) imapFolder.fetch(messages, profile)
        return messages.map { KourrierMessage(it as IMAPMessage) }
    }
}

/**
 * Represents the different access modes to a [KourrierFolder].
 */
enum class KourrierFolderMode(private val rawMode: Int) {
    /**
     * Restrictive read permission.
     */
    ReadOnly(Folder.READ_ONLY),

    /**
     * Permissive write permission (+read).
     */
    ReadWrite(Folder.READ_WRITE),
    ;

    internal fun toRawFolderMode(): Int = rawMode
}

/**
 * Represents the different types of [KourrierFolder]s.
 */
enum class KourrierFolderType(private val rawType: Int) {
    /**
     * Holds only folders.
     */
    HoldsFolders(Folder.HOLDS_FOLDERS),

    /**
     * Holds only messages.
     */
    HoldsMessages(Folder.HOLDS_MESSAGES),

    /**
     * Holds both folders and messages.
     */
    HoldsAll(Folder.HOLDS_FOLDERS and Folder.HOLDS_MESSAGES),
    ;

    companion object {
        /**
         * Obtains a [KourrierFolderType] from the given [type].
         */
        fun fromRawFolderType(type: Int): KourrierFolderType {
            val holdsFolders = type and Folder.HOLDS_FOLDERS
            val holdsMessages = type and Folder.HOLDS_MESSAGES
            return when {
                holdsFolders != 0 && holdsMessages != 0 -> HoldsAll
                holdsFolders != 0 -> HoldsFolders
                holdsMessages != 0 -> HoldsMessages
                else -> throw UnknownFolderTypeException()
            }
        }
    }

    internal fun toRawFolderType(): Int = rawType
}

/**
 * Is thrown when a [KourrierFolderType] is unknown or invalid.
 */
class UnknownFolderTypeException : Exception()

private operator fun IMAPFolder.get(index: Int): KourrierMessage? =
    (getMessage(index) as IMAPMessage?)?.let {
        KourrierMessage(it)
    }
