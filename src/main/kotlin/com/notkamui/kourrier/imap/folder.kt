package com.notkamui.kourrier.imap

import com.notkamui.kourrier.core.KourrierFlag
import com.notkamui.kourrier.core.KourrierFlags
import com.notkamui.kourrier.core.KourrierIMAPFolderStateException
import com.notkamui.kourrier.core.KourrierIMAPSessionStateException
import com.notkamui.kourrier.core.UnknownFolderTypeException
import com.notkamui.kourrier.search.KourrierSearch
import com.notkamui.kourrier.search.KourrierSort
import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage
import com.sun.mail.imap.IdleManager
import javax.mail.FetchProfile
import javax.mail.Folder
import javax.mail.FolderClosedException
import javax.mail.Message
import javax.mail.StoreClosedException

/**
 * Wrapper around the standard [IMAPFolder].
 */
class KourrierFolder internal constructor(
    internal val imapFolder: IMAPFolder,
    private var mode: KourrierFolderMode,
    private val keepAlive: Boolean,
    private val idleManager: IdleManager,
    private val debugMode: Boolean
) {
    private var profile = FetchProfile()

    private val job = Thread {
        fun reIdle() {
            imapFolder.doCommand {
                it.simpleCommand("NOOP", null)
                null
            }
            idleManager.watch(imapFolder)
        }
        while (true) {
            Thread.sleep(60_000L)
            try {
                reIdle()
            } catch (e: FolderClosedException) {
                setConnection(mode)
            } catch (e: StoreClosedException) {
                throw KourrierIMAPSessionStateException("Couldn't keep ${imapFolder.name} open. Session was forcefully closed.")
            }
        }
    }

    /**
     * The current state of the folder.
     */
    val isOpen: Boolean
        get() = imapFolder.isOpen

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

    init {
        open(mode)
    }

    /**
     * Closes the current [IMAPFolder], and [expunge]s it or not (defaults to false).
     *
     * @throws KourrierIMAPFolderStateException if the folder is already closed.
     */
    fun close(expunge: Boolean = false) {
        if (!imapFolder.isOpen)
            throw KourrierIMAPFolderStateException("Cannot close a folder that is already closed.")

        try {
            job.interrupt()
        } catch (e: InterruptedException) {
            if (debugMode)
                println("Kourrier Notice: Successfully interrupted keep alive")
        }
        imapFolder.close(expunge)
    }

    private fun setConnection(mode: KourrierFolderMode) {
        imapFolder.open(mode.toRawFolderMode())
        this.mode = mode
    }

    /**
     * Opens the current [IMAPFolder] with the given [mode].
     *
     * @throws KourrierIMAPFolderStateException if the folder is already open.
     */
    fun open(mode: KourrierFolderMode) {
        if (imapFolder.isOpen)
            throw KourrierIMAPFolderStateException("Cannot open a folder that is already open.")

        setConnection(mode)
        if (keepAlive) {
            job.start()
        }
    }

    /**
     * Obtains the message at the given [index],
     * or null if it doesn't exist.
     */
    operator fun get(index: Int): KourrierIMAPMessage? = imapFolder[index]

    /**
     * Obtains the list of message from the given [range] of index,
     * or null if one of them doesn't exist.
     * May [prefetch] the results (defaults to true).
     */
    operator fun get(range: IntRange, prefetch: Boolean = true): List<KourrierIMAPMessage>? {
        val messages: Array<out Message> = try {
            imapFolder.getMessages(range.toList().toIntArray())
        } catch (e: IndexOutOfBoundsException) {
            null
        } ?: return null
        if (prefetch) imapFolder.fetch(messages, profile)
        return messages.map { KourrierIMAPMessage(it as IMAPMessage) }
    }

    /**
     * Searches messages in the current [KourrierFolder]
     * using the [callback] [KourrierSearch] DSL builder
     * and returns a list of [KourrierIMAPMessage].
     */
    fun search(callback: KourrierSearch.() -> Unit): List<KourrierIMAPMessage> {
        val builder = KourrierSearch()
        builder.callback()
        val search = builder.build()

        val rawMessages: Array<out Message>? = if (builder.hasSortTerms) {
            val sort = builder.sortTerms.map { it.toRawSortTerm() }.toTypedArray()
            search?.let { imapFolder.getSortedMessages(sort, it) }
        } else {
            search?.let { imapFolder.search(it) }
        }

        rawMessages?.let { imapFolder.fetch(it, profile) }
        val messages = rawMessages?.map { KourrierIMAPMessage(it as IMAPMessage) }
            ?: listOf()

        if (builder.markAsRead && rawMessages != null) {
            imapFolder.setFlags(
                rawMessages,
                KourrierFlags(KourrierFlag.Seen).rawFlags,
                true
            )
        }

        return messages
    }

    /**
     * Sorts the messages in the current [KourrierFolder]
     * using the [callback] [KourrierSort] DSL builder
     * and returns a list of [KourrierIMAPMessage]
     */
    fun sortedBy(callback: KourrierSort.() -> Unit): List<KourrierIMAPMessage> {
        val builder = KourrierSort()
        builder.callback()

        val sort = builder.build().map {
            it.toRawSortTerm()
        }.toTypedArray()
        val rawMessages = imapFolder.getSortedMessages(sort)

        imapFolder.fetch(rawMessages, profile)

        return rawMessages.map { KourrierIMAPMessage(it as IMAPMessage) }
    }

    /**
     * Sets the [fetchProfile] for the next fetch.
     */
    fun prefetchBy(fetchProfile: FetchProfile) {
        profile = fetchProfile
    }

    /**
     * Sets the fetch profile for the next fetch using the given [fetchProfileItems].
     */
    fun prefetchBy(vararg fetchProfileItems: FetchProfile.Item) {
        FetchProfile().apply {
            for (item in fetchProfileItems) {
                add(item)
            }
            profile = this
        }
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
         * Obtains a [KourrierFolderType] from the given [rawType].
         */
        fun fromRawFolderType(rawType: Int): KourrierFolderType {
            val holdsFolders = rawType and Folder.HOLDS_FOLDERS
            val holdsMessages = rawType and Folder.HOLDS_MESSAGES
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

private operator fun IMAPFolder.get(index: Int): KourrierIMAPMessage? =
    (getMessage(index) as IMAPMessage?)?.let {
        KourrierIMAPMessage(it)
    }
