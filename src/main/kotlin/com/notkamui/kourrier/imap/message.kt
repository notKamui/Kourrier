package com.notkamui.kourrier.imap

import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage
import javax.mail.Address
import javax.mail.Header
import javax.mail.Part
import javax.mail.internet.MimeMultipart

/**
 * Wrapper around the standard [IMAPMessage].
 */
data class KourrierIMAPMessage(private val message: IMAPMessage) : Part by message {
    /**
     * UID of the message.
     */
    val uid: Long by lazy {
        (message.folder as IMAPFolder).getUID(message)
    }

    /**
     * Sender of the message.
     */
    val from: Array<out Address> by lazy {
        message.from
    }

    /**
     * List of [KourrierMessageHeader] of the message.
     */
    val headers: List<KourrierMessageHeader> by lazy {
        message.allHeaders.toList().map { KourrierMessageHeader(it.name, it.value) }
    }

    /**
     * Subject of the message.
     */
    val subject: String by lazy {
        message.subject
    }

    /**
     * Body of the message. Joined in one [String] even if multipart.
     */
    val body: String by lazy {
        bodyParts.joinToString("\n")
    }

    /**
     * Body of the message, with each part being a list entry.
     * (If the message is not multipart, the length will be 1)
     */
    val bodyParts: List<String> by lazy {
        val content = message.content
        if (content is MimeMultipart) {
            (0 until content.count).map { part ->
                content.getBodyPart(part).content as String
            }
        } else {
            listOf(content as String)
        }
    }
}

/**
 * Wrapper around the standard [Header].
 */
data class KourrierMessageHeader internal constructor(
    /**
     * Name of the header.
     */
    val name: String,

    /**
     * Value of the header.
     */
    val value: String,
)
