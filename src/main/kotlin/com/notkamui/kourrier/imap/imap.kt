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
import javax.mail.event.MessageCountAdapter
import javax.mail.event.MessageCountEvent

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

    /**
     * Opends the current [KourrierIMAPSession].
     *
     * @throws KourrierIMAPSessionStateException if the session is already open.
     */
    fun open() {
        if (store.isConnected)
            throw KourrierIMAPSessionStateException("Cannot open a session that is already open.")
        idleManager = IdleManager(session, Executors.newCachedThreadPool())
        with(connectionInfo) {
            store.connect(hostname, port, username, password)
        }
    }

    /**
     * Applies the given [callback] to the current [KourrierIMAPSession].
     */
    operator fun invoke(callback: KourrierIMAPSession.() -> Unit) {
        callback()
    }

    /**
     * Opens a folder in the current session by its [name],
     * with the given [mode],
     * and applies the [callback] lambda with itself as the receiver.
     *
     * Automatically closes the folder without expunging it on exit or not
     * depending on the value of [expunge].
     *
     * @throws KourrierIMAPSessionStateException if the session is closed.
     */
    fun folder(
        name: String,
        mode: KourrierFolderMode,
        expunge: Boolean = false,
        callback: KourrierFolder.() -> Unit
    ) {
        if (!store.isConnected)
            throw KourrierIMAPSessionStateException("Cannot interact with a closed session.")

        (store.getFolder(name) as IMAPFolder).apply {
            open(mode.toRawFolderMode())
            KourrierFolder(this).apply {
                callback()
                close(expunge)
            }
        }
    }

    /**
     * Opens a [KourrierFolder] by its [name], returns it, and listens to it
     * to apply [onReceive] on each [KourrierIMAPMessage] received.
     *
     * **Note that the returned folder should be closed at some point**
     *
     * @throws KourrierIMAPSessionStateException if the session is closed.
     */
    fun listenFolder(
        name: String,
        onReceive: KourrierIMAPMessage.() -> Unit
    ): KourrierFolder {
        if (!store.isConnected)
            throw KourrierIMAPSessionStateException("Cannot interact with a closed session.")

        val folder = store.getFolder(name) as IMAPFolder
        folder.open(KourrierFolderMode.ReadWrite.toRawFolderMode())
        folder.addMessageCountListener(object : MessageCountAdapter() {
            override fun messagesAdded(event: MessageCountEvent) {
                event.messages.forEach {
                    KourrierIMAPMessage(it as IMAPMessage).onReceive()
                }
                idleManager.watch(folder)
            }
        })
        idleManager.watch(folder)
        return KourrierFolder(folder)
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
