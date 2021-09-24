package com.notkamui.kourrier.imap

import com.notkamui.kourrier.core.Kourrier
import com.notkamui.kourrier.core.KourrierConnectionInfo
import com.notkamui.kourrier.core.KourrierIMAPSessionStateException
import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage
import com.sun.mail.imap.IdleManager
import java.io.Closeable
import java.util.Properties
import java.util.concurrent.Executors
import javax.mail.Session
import javax.mail.Store
import javax.mail.event.MessageChangedEvent
import javax.mail.event.MessageCountEvent
import javax.mail.event.MessageCountListener

/**
 * Wrapper around JavaMail [Store] session with IMAP helpers
 */
class KourrierIMAPSession internal constructor(
    private val connectionInfo: KourrierConnectionInfo,
    properties: Properties,
) : Closeable {
    private val session: Session
    private val store: Store
    private lateinit var idleManager: IdleManager

    val isOpen: Boolean
        get() = store.isConnected

    init {
        properties["mail.imap.ssl.enable"] = connectionInfo.enableSSL
        properties["mail.imaps.usesocketchannels"] = true
        session = Session.getInstance(properties)
        session.debug = connectionInfo.debugMode
        store = session.getStore(
            "imap${
            if (connectionInfo.enableSSL) "s"
            else ""
            }"
        )
        open()
    }

    /**
     * Closes the current [KourrierIMAPSession].
     *
     * @throws KourrierIMAPSessionStateException if the session is already closed.
     */
    override fun close() {
        if (!store.isConnected)
            throw KourrierIMAPSessionStateException("Cannot close a session that is already closed.")

        idleManager.stop()
        store.close()
    }

    private fun setConnection() {
        idleManager = IdleManager(session, Executors.newCachedThreadPool())
        with(connectionInfo) {
            store.connect(hostname, port, username, password)
        }
    }

    /**
     * Opends the current [KourrierIMAPSession].
     *
     * @throws KourrierIMAPSessionStateException if the session is already open.
     */
    fun open() {
        if (store.isConnected)
            throw KourrierIMAPSessionStateException("Cannot open a session that is already open.")

        setConnection()
    }

    /**
     * Applies the given [callback] to the current [KourrierIMAPSession].
     */
    operator fun invoke(callback: KourrierIMAPSession.() -> Unit) {
        callback()
    }

    /**
     * Opens and returns a [KourrierFolder] in the current session by its [name],
     * with the given [mode],
     * and applies the [callback] lambda with itself as the receiver.
     *
     * A [listener] can be given.
     *
     * **Note that the returned folder should be closed at some point.**
     *
     * **Note that some mail servers need ReadWrite permissions for a folder to be listenable.**
     *
     * [keepAlive] is an optional parameter that indicates whether
     * the connection should be kept alive at all cost (until being close manually).
     * (defaults to false)
     *
     * @throws KourrierIMAPSessionStateException if the session is closed.
     */
    fun folder(
        name: String,
        mode: KourrierFolderMode,
        listener: KourrierFolderListener? = null,
        keepAlive: Boolean = false,
        callback: KourrierFolder.() -> Unit = {}
    ): KourrierFolder {
        if (!store.isConnected)
            throw KourrierIMAPSessionStateException("Cannot interact with a closed session.")

        val folder = store.getFolder(name) as IMAPFolder
        val kfolder = KourrierFolder(
            folder,
            mode,
            keepAlive,
            idleManager,
            connectionInfo.debugMode
        )
        listener?.let { kfolder.addListener(it) }
        kfolder.callback()
        return kfolder
    }

    /**
     * Adds a [listener] to a [KourrierFolder].
     */
    fun KourrierFolder.addListener(listener: KourrierFolderListener) {
        imapFolder.addMessageCountListener(object : MessageCountListener {
            override fun messagesAdded(event: MessageCountEvent) {
                event.messages.forEach {
                    listener.onMessageReceived(KourrierIMAPMessage(it as IMAPMessage))
                }
                idleManager.watch(imapFolder)
            }

            override fun messagesRemoved(event: MessageCountEvent) {
                event.messages.forEach {
                    listener.onMessageRemoved(KourrierIMAPMessage(it as IMAPMessage))
                }
                idleManager.watch(imapFolder)
            }
        })

        imapFolder.addMessageChangedListener { event: MessageChangedEvent ->
            val message = KourrierIMAPMessage(event.message as IMAPMessage)
            when (event.messageChangeType) {
                MessageChangedEvent.FLAGS_CHANGED -> listener.onMessageFlagsChanged(message)
                MessageChangedEvent.ENVELOPE_CHANGED -> listener.onMessageEnvelopeChanged(message)
            }
            idleManager.watch(imapFolder)
        }

        idleManager.watch(imapFolder)
    }
}

/**
 * Opens an IMAP session using the [connectionInfo] and [properties].
 */
fun Kourrier.imap(
    connectionInfo: KourrierConnectionInfo,
    properties: Properties = Properties(),
): KourrierIMAPSession =
    KourrierIMAPSession(connectionInfo, properties)

/**
 * Opens an IMAP session using the specified credentials
 * ([hostname], [port], [username], [password]) and [properties].
 *
 * - Debug mode can be enabled with [debugMode] (defaults to false).
 * - SSL can be enabled with [enableSSL] (defaults to true).
 *
 */
fun Kourrier.imap(
    hostname: String,
    port: Int,
    username: String,
    password: String,
    debugMode: Boolean = false,
    enableSSL: Boolean = true,
    properties: Properties = Properties()
): KourrierIMAPSession =
    KourrierConnectionInfo(
        hostname,
        port,
        username,
        password,
        debugMode,
        enableSSL,
    ).run {
        imap(this, properties)
    }
