package com.notkamui.kourrier.search

import com.sun.mail.imap.SortTerm

/**
 * DSL builder for creating a valid list of [KourrierSortTerm]s ([SortTerm]).
 */
class KourrierSort internal constructor() {
    private val sortTerms = mutableListOf<KourrierSortTerm>()

    /**
     * Builds the sort engine to be used to fetch messages.
     */
    fun build(): List<KourrierSortTerm> = sortTerms

    private fun add(sortTerm: KourrierSortTerm) {
        sortTerms.add(sortTerm)
    }

    private fun add(rawSortTerm: SortTerm) {
        val sortTerm = KourrierSortTerm.fromRawSortTerm(rawSortTerm)
        sortTerms.add(sortTerm)
    }

    /**
     * Adds a [KourrierSortTerm] in the natural order direction.
     */
    operator fun KourrierSortTerm.unaryPlus() {
        add(this)
    }

    /**
     * Adds a [KourrierSortTerm] in the reverse order direction.
     */
    operator fun KourrierSortTerm.not() {
        add(KourrierSortTerm.Reverse)
        add(this)
    }

    /**
     * Adds a [SortTerm] in the natural order direction.
     */
    operator fun SortTerm.unaryPlus() {
        add(this)
    }

    /**
     * Adds a [SortTerm] in the reverse order direction.
     */
    operator fun SortTerm.not() {
        add(KourrierSortTerm.Reverse)
        add(this)
    }
}
