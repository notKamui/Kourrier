package com.notkamui.kourrier.search

import java.util.Date
import javax.mail.search.AndTerm
import javax.mail.search.ComparisonTerm
import javax.mail.search.NotTerm
import javax.mail.search.OrTerm
import javax.mail.search.ReceivedDateTerm
import javax.mail.search.SearchTerm
import javax.mail.search.SentDateTerm
import javax.mail.search.SizeTerm

/**
 * Interface wrapper to build comparable JavaMail search terms.
 *
 * [T] is a [Comparable].
 * [R] is a JavaMail [SearchTerm].
 */
sealed interface KourrierComparableTerm<T : Comparable<T>, R : SearchTerm> {
    /**
     * Equal to [it].
     */
    fun eq(it: T): R

    /**
     * Not equal to [it].
     */
    fun ne(it: T): R

    /**
     * Less than [it].
     */
    fun lt(it: T): R

    /**
     * Less than or equal to [it].
     */
    fun le(it: T): R

    /**
     * Greater than [it].
     */
    fun gt(it: T): R

    /**
     * Greater than or equal to [it].
     */
    fun ge(it: T): R

    /**
     * In closed [range].
     */
    fun inRange(range: ClosedRange<T>): AndTerm
}

/**
 * Builder wrapper around the standard [ReceivedDateTerm].
 */
object KourrierReceivedDate : KourrierComparableTerm<Date, ReceivedDateTerm> {
    override fun eq(it: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.EQ, it)
    override fun ne(it: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.NE, it)
    override fun lt(it: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.LT, it)
    override fun le(it: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.LE, it)
    override fun gt(it: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.GT, it)
    override fun ge(it: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.GE, it)
    override fun inRange(range: ClosedRange<Date>): AndTerm =
        ge(range.start) and le(range.endInclusive)
}

/**
 * Builder wrapper around the standard [SentDateTerm].
 */
object KourrierSentDate : KourrierComparableTerm<Date, SentDateTerm> {
    override fun eq(it: Date): SentDateTerm = SentDateTerm(ComparisonTerm.EQ, it)
    override fun ne(it: Date): SentDateTerm = SentDateTerm(ComparisonTerm.NE, it)
    override fun lt(it: Date): SentDateTerm = SentDateTerm(ComparisonTerm.LT, it)
    override fun le(it: Date): SentDateTerm = SentDateTerm(ComparisonTerm.LE, it)
    override fun gt(it: Date): SentDateTerm = SentDateTerm(ComparisonTerm.GT, it)
    override fun ge(it: Date): SentDateTerm = SentDateTerm(ComparisonTerm.GE, it)
    override fun inRange(range: ClosedRange<Date>): AndTerm =
        ge(range.start) and le(range.endInclusive)
}

/**
 * Builder wrapper around the standard [SizeTerm].
 */
object KourrierSize : KourrierComparableTerm<Int, SizeTerm> {
    override fun eq(it: Int): SizeTerm = SizeTerm(ComparisonTerm.EQ, it)
    override fun ne(it: Int): SizeTerm = SizeTerm(ComparisonTerm.NE, it)
    override fun lt(it: Int): SizeTerm = SizeTerm(ComparisonTerm.LT, it)
    override fun le(it: Int): SizeTerm = SizeTerm(ComparisonTerm.LE, it)
    override fun gt(it: Int): SizeTerm = SizeTerm(ComparisonTerm.GT, it)
    override fun ge(it: Int): SizeTerm = SizeTerm(ComparisonTerm.GE, it)
    override fun inRange(range: ClosedRange<Int>): AndTerm =
        ge(range.start) and le(range.endInclusive)
}

/**
 * Creates an [AndTerm] of [this] received term and [other] term.
 */
infix fun SearchTerm.and(other: SearchTerm): AndTerm = AndTerm(this, other)

/**
 * Creates an [OrTerm] of [this] received term and [other] term.
 */
infix fun SearchTerm.or(other: SearchTerm): OrTerm = OrTerm(this, other)

/**
 * Creates an [NotTerm] of [this] received term.
 */
operator fun SearchTerm.not(): NotTerm = NotTerm(this)
