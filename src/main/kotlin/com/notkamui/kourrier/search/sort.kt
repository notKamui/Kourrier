package com.notkamui.kourrier.search

import com.notkamui.kourrier.core.UnknownSortTermException
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
        /**
         * Obtain a [KourrierSortTerm] from a raw [SortTerm]
         */
        fun fromRawSortTerm(rawSortTerm: SortTerm): KourrierSortTerm =
            values().find { it.rawSortTerm == rawSortTerm }
                ?: throw UnknownSortTermException()
    }

    /**
     * Obtain a raw [SortTerm] from this [KourrierSortTerm]
     */
    fun toRawSortTerm(): SortTerm = rawSortTerm
}
