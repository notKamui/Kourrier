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
 * Builder wrapper around the standard [ReceivedDateTerm].
 */
object KourrierReceivedDate {
    fun eq(date: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.EQ, date)
    fun ne(date: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.NE, date)
    fun lt(date: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.LT, date)
    fun le(date: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.LE, date)
    fun gt(date: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.GT, date)
    fun ge(date: Date): ReceivedDateTerm = ReceivedDateTerm(ComparisonTerm.GE, date)
    fun inRange(dateRange: ClosedRange<Date>): AndTerm =
        ge(dateRange.start) and le(dateRange.endInclusive)
}

/**
 * Builder wrapper around the standard [SentDateTerm].
 */
object KourrierSentDate {
    fun eq(date: Date): SentDateTerm = SentDateTerm(ComparisonTerm.EQ, date)
    fun ne(date: Date): SentDateTerm = SentDateTerm(ComparisonTerm.NE, date)
    fun lt(date: Date): SentDateTerm = SentDateTerm(ComparisonTerm.LT, date)
    fun le(date: Date): SentDateTerm = SentDateTerm(ComparisonTerm.LE, date)
    fun gt(date: Date): SentDateTerm = SentDateTerm(ComparisonTerm.GT, date)
    fun ge(date: Date): SentDateTerm = SentDateTerm(ComparisonTerm.GE, date)
    fun inRange(dateRange: ClosedRange<Date>): AndTerm =
        ge(dateRange.start) and le(dateRange.endInclusive)
}

/**
 * Builder wrapper around the standard [SizeTerm].
 */
object KourrierSize {
    fun eq(size: Int): SizeTerm = SizeTerm(ComparisonTerm.EQ, size)
    fun ne(size: Int): SizeTerm = SizeTerm(ComparisonTerm.NE, size)
    fun lt(size: Int): SizeTerm = SizeTerm(ComparisonTerm.LT, size)
    fun le(size: Int): SizeTerm = SizeTerm(ComparisonTerm.LE, size)
    fun gt(size: Int): SizeTerm = SizeTerm(ComparisonTerm.GT, size)
    fun ge(size: Int): SizeTerm = SizeTerm(ComparisonTerm.GE, size)
    fun inRange(sizeRange: IntRange): AndTerm =
        ge(sizeRange.first) and le(sizeRange.last)
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
