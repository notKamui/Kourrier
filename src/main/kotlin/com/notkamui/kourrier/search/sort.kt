package com.notkamui.kourrier.search

import com.sun.mail.imap.SortTerm

/**
 * Wrapper around the standard [SortTerm]
 */
enum class KourrierSortTerm(private val rawSortTerm: SortTerm) {
    From(SortTerm.FROM),
    To(SortTerm.TO),
    Subject(SortTerm.SUBJECT),
    Arrival(SortTerm.ARRIVAL),
    Sent(SortTerm.DATE),
    CC(SortTerm.CC),
    Reverse(SortTerm.REVERSE),
    Size(SortTerm.SIZE),
    ;

    companion object {
        fun fromRawSortTerm(rawSortTerm: SortTerm): KourrierSortTerm =
            values().find { it.rawSortTerm == rawSortTerm }
                ?: throw UnknownSortTermException()
    }

    fun toRawSortTerm(): SortTerm = rawSortTerm
}

/**
 * Is thrown when a [KourrierSortTerm] is unknown or invalid.
 */
class UnknownSortTermException : Exception()
