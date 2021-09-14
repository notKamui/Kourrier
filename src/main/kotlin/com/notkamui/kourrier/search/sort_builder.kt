package com.notkamui.kourrier.search

import com.sun.mail.imap.SortTerm

class KourrierSort {
    private val sortTerms = mutableListOf<KourrierSortTerm>()

    fun build(): List<KourrierSortTerm> = sortTerms

    private fun add(sortTerm: KourrierSortTerm) {
        sortTerms.add(sortTerm)
    }

    private fun add(rawSortTerm: SortTerm) {
        val sortTerm = KourrierSortTerm.fromRawSortTerm(rawSortTerm)
        sortTerms.add(sortTerm)
    }

    operator fun KourrierSortTerm.unaryPlus() {
        add(this)
    }

    operator fun KourrierSortTerm.not() {
        add(KourrierSortTerm.Reverse)
        add(this)
    }

    operator fun SortTerm.unaryPlus() {
        add(this)
    }

    operator fun SortTerm.not() {
        add(KourrierSortTerm.Reverse)
        add(this)
    }
}
