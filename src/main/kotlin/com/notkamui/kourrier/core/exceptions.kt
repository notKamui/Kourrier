package com.notkamui.kourrier.core

import com.notkamui.kourrier.imap.KourrierFolder
import com.notkamui.kourrier.imap.KourrierFolderType
import com.notkamui.kourrier.imap.KourrierIMAPSession
import com.notkamui.kourrier.search.KourrierSortTerm

/**
 * Is thrown when an issue during connection happens.
 */
open class KourrierConnectException
internal constructor(message: String) : Exception(message)

/**
 * Is thrown when an authentication error happens.
 */
class KourrierAuthenticationException
internal constructor(message: String) : KourrierConnectException(message)

/**
 * Is thrown when an inconsistency in the [KourrierIMAPSession] status happens.
 */
class KourrierIMAPSessionStateException
internal constructor(message: String) : IllegalStateException(message)

/**
 * Is thrown when an inconsistent in the [KourrierFolder] status happens.
 */
class KourrierIMAPFolderStateException
internal constructor(message: String) : IllegalStateException(message)

/**
 * Is thrown when a [KourrierFolderType] is unknown or invalid.
 */
class UnknownFolderTypeException
internal constructor() : IllegalArgumentException()

/**
 * Is thrown when a [KourrierSortTerm] is unknown or invalid.
 */
class UnknownSortTermException
internal constructor() : IllegalArgumentException()
