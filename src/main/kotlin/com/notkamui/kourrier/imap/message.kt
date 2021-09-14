package com.notkamui.kourrier.imap

import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage

/**
 * Wrapper around the standard [IMAPMessage].
 */
class KourrierMessage(private val message: IMAPMessage) {
    val uid: Long by lazy {
        message.messageUID
    }

    val from: String by lazy {
        message.from[0].toString()
    }

    val headers: List<KourrierMessageHeader> by lazy {
        message.allHeaders.toList().map { KourrierMessageHeader(it.name, it.value) }
    }

    val body: String by lazy {
        message.content as String
    }
}

data class KourrierMessageHeader(val name: String, val value: String)

private val IMAPMessage.messageUID: Long
    get() = (folder as IMAPFolder).getUID(this)
