package com.notkamui.kourrier.core

import com.notkamui.kourrier.imap.KourrierFolderType
import com.notkamui.kourrier.imap.KourrierIMAPSession
import com.notkamui.kourrier.search.KourrierSortTerm

/**
 * Is thrown when an inconsistency in the [KourrierIMAPSession] status happens.
 */
class KourrierIMAPSessionStateException
internal constructor(message: String) : IllegalStateException(message)

/**
 * Is thrown when a [KourrierFolderType] is unknown or invalid.
 */
class UnknownFolderTypeException : Exception()

/**
 * Is thrown when a [KourrierSortTerm] is unknown or invalid.
 */
class UnknownSortTermException
internal constructor() : Exception()