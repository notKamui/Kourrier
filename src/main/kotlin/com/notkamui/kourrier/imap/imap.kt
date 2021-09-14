package com.notkamui.kourrier.imap

import com.notkamui.kourrier.core.Kourrier
import com.notkamui.kourrier.core.KourrierConnectionInfo
import com.sun.mail.imap.IMAPFolder
import java.io.Closeable
import java.util.Properties
import javax.mail.Session
import javax.mail.Store

/**
 * Wrapper around JavaMail [Store] session with IMAP helpers
 */
class KourrierIMAPSession internal constructor(
    private val store: Store
) : AutoCloseable, Closeable {
    /**
     * Closes the current [Store] session (and subsequently disconnects the IMAP session).
     */
    override fun close() {
        store.close()
    }

    /**
     * Opens a folder in the current session by its [name],
     * with the given [mode],
     * and applies the [callback] lambda with itself as the receiver.
     *
     * Closes the folder without expunging it on exit.
     */
    fun folder(
        name: String,
        mode: KourrierFolderMode,
        expunge: Boolean = false,
        callback: KourrierFolder.() -> Unit
    ) {
        (store.getFolder(name) as IMAPFolder).apply {
            open(mode.toRawFolderMode())
            KourrierFolder(this).apply {
                callback()
                close(expunge)
            }
        }
    }
}

/**
 * Opens an IMAP session using the [connectionInfo] and [properties],
 * and applies the [callback] lambda with itself as the receiver.
 */
fun Kourrier.imap(
    connectionInfo: KourrierConnectionInfo,
    properties: Properties = Properties(),
    callback: KourrierIMAPSession.() -> Unit
) {
    properties["mail.imap.ssl.enable"] = connectionInfo.enableSSL
    val session = Session.getInstance(properties)
    session.debug = connectionInfo.debugMode
    val store = session.getStore(
        "imap${
        if (connectionInfo.enableSSL) "s"
        else ""
        }"
    )
    with(connectionInfo) {
        store.connect(hostname, port, username, password)
    }
    KourrierIMAPSession(store).use(callback)
}

/**
 * Opens an IMAP session using the specified credentials
 * ([hostname], [port], [username], [password]) and [properties],
 * and applies the [callback] lambda with itself as the receiver.
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
    properties: Properties = Properties(),
    callback: KourrierIMAPSession.() -> Unit
) {
    KourrierConnectionInfo(
        hostname,
        port,
        username,
        password,
        debugMode,
        enableSSL,
    ).run {
        imap(this, properties, callback)
    }
}
