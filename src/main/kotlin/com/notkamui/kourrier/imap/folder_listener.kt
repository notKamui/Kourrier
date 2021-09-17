package com.notkamui.kourrier.imap

import java.util.EventListener

interface KourrierFolderListener : EventListener {
    /**
     * Is launched when a [message] is received.
     */
    fun onMessageReceived(message: KourrierIMAPMessage)

    /**
     * Is launched when a [message] is **expunged** (this doesn't always mean "deleted").
     */
    fun onMessageRemoved(message: KourrierIMAPMessage)

    /**
     * Is launched when a [message]'s flags changed.
     */
    fun onMessageFlagsChanged(message: KourrierIMAPMessage)

    /**
     * Is launched when a [message]'s envelope changed.
     */
    fun onMessageEnvelopeChanged(message: KourrierIMAPMessage)
}

open class KourrierFolderAdapter : KourrierFolderListener {
    override fun onMessageReceived(message: KourrierIMAPMessage) {
        /* Default */
    }

    override fun onMessageRemoved(message: KourrierIMAPMessage) {
        /* Default */
    }

    override fun onMessageFlagsChanged(message: KourrierIMAPMessage) {
        /* Default */
    }

    override fun onMessageEnvelopeChanged(message: KourrierIMAPMessage) {
        /* Default */
    }
}
