package com.notkamui.kourrier.search

import com.notkamui.kourrier.core.KourrierFlags
import com.sun.mail.imap.ModifiedSinceTerm
import com.sun.mail.imap.OlderTerm
import com.sun.mail.imap.YoungerTerm
import java.util.Date
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.search.AndTerm
import javax.mail.search.BodyTerm
import javax.mail.search.FlagTerm
import javax.mail.search.FromStringTerm
import javax.mail.search.FromTerm
import javax.mail.search.HeaderTerm
import javax.mail.search.MessageIDTerm
import javax.mail.search.MessageNumberTerm
import javax.mail.search.NotTerm
import javax.mail.search.OrTerm
import javax.mail.search.ReceivedDateTerm
import javax.mail.search.RecipientStringTerm
import javax.mail.search.RecipientTerm
import javax.mail.search.SearchTerm
import javax.mail.search.SentDateTerm
import javax.mail.search.SizeTerm
import javax.mail.search.SubjectTerm

/**
 * DSL builder for creating a valid [SearchTerm].
 */
class KourrierSearch {
    private val terms = mutableListOf<SearchTerm>()
    private val sortedBy = mutableSetOf<KourrierSortTerm>()

    /**
     * Marks the search results as read (defaults to false)
     */
    var markAsRead = false

    /**
     * Whether sort terms have been applied, or not.
     */
    val hasSortTerms: Boolean
        get() = sortedBy.isNotEmpty()

    /**
     * The list of the sort terms that have been applied.
     */
    val sortTerms: List<KourrierSortTerm>
        get() = sortedBy.toList()

    /**
     * Builds and return the final [SearchTerm] (null if no search terms were added).
     */
    fun build(): SearchTerm? = when (terms.size) {
        0 -> null
        1 -> terms.first()
        else -> terms.reduce { acc, next -> acc and next }
    }

    /**
     * Returns a [FromTerm] of the given [address].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun from(address: InternetAddress): FromTerm = FromTerm(address)

    /**
     * Returns a [FromStringTerm] of the given [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun from(pattern: String): FromStringTerm = FromStringTerm(pattern)

    /**
     * Returns a [RecipientTerm] of the given [type] and [address].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun recipient(
        type: Message.RecipientType,
        address: InternetAddress
    ): RecipientTerm = RecipientTerm(type, address)

    /**
     * Returns a [RecipientStringTerm] of the given [type] and [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun recipient(
        type: Message.RecipientType,
        pattern: String
    ): RecipientStringTerm = RecipientStringTerm(type, pattern)

    /**
     * Returns a [RecipientTerm] ([Message.RecipientType.TO]) of the given [address].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun to(address: InternetAddress): RecipientTerm =
        recipient(Message.RecipientType.TO, address)

    /**
     * Returns a [RecipientStringTerm] ([Message.RecipientType.TO]) of the given [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun to(pattern: String): RecipientStringTerm =
        recipient(Message.RecipientType.TO, pattern)

    /**
     * Returns a [RecipientTerm] ([Message.RecipientType.CC]) of the given [address].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun cc(address: InternetAddress): RecipientTerm =
        recipient(Message.RecipientType.CC, address)

    /**
     * Returns a [RecipientStringTerm] ([Message.RecipientType.CC]) of the given [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun cc(pattern: String): RecipientStringTerm =
        recipient(Message.RecipientType.CC, pattern)

    /**
     * Returns a [RecipientTerm] ([Message.RecipientType.BCC]) of the given [address].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun bcc(address: InternetAddress): RecipientTerm =
        recipient(Message.RecipientType.BCC, address)

    /**
     * Returns a [RecipientStringTerm] ([Message.RecipientType.BCC]) of the given [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun bcc(pattern: String): RecipientStringTerm =
        recipient(Message.RecipientType.BCC, pattern)

    /**
     * Returns a [SubjectTerm] of the given [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun subject(pattern: String): SubjectTerm = SubjectTerm(pattern)

    /**
     * Returns a [BodyTerm] of the given [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun body(pattern: String): BodyTerm = BodyTerm(pattern)

    /**
     * Returns a [HeaderTerm] of the given [name] and [pattern].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun header(name: String, pattern: String): HeaderTerm = HeaderTerm(name, pattern)

    /**
     * Returns an [AndTerm] with the [first] and [second] [SearchTerm]s.
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun and(first: SearchTerm, second: SearchTerm): AndTerm = first and second

    /**
     * Returns an [OrTerm] with the [first] and [second] [SearchTerm]s.
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun or(first: SearchTerm, second: SearchTerm): OrTerm = first or second

    /**
     * Returns a [ReceivedDateTerm] of the given [comparison] type and [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun received(
        comparison: Int,
        date: Date
    ): ReceivedDateTerm = ReceivedDateTerm(comparison, date)

    /**
     * Returns a [ReceivedDateTerm] on the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedOn(date: Date): ReceivedDateTerm = KourrierReceivedDate.eq(date)

    /**
     * Returns a [ReceivedDateTerm] on or after the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedOnOrAfter(date: Date): ReceivedDateTerm = KourrierReceivedDate.ge(date)

    /**
     * Returns a [ReceivedDateTerm] after the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedAfter(date: Date): ReceivedDateTerm = KourrierReceivedDate.gt(date)

    /**
     * Returns a [ReceivedDateTerm] on or before the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedOnOrBefore(date: Date): ReceivedDateTerm = KourrierReceivedDate.le(date)

    /**
     * Returns a [ReceivedDateTerm] before the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedBefore(date: Date): ReceivedDateTerm = KourrierReceivedDate.lt(date)

    /**
     * Returns a [ReceivedDateTerm] not on the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun notReceivedOn(date: Date): ReceivedDateTerm = KourrierReceivedDate.ne(date)

    /**
     * Returns a [ReceivedDateTerm] in the given [dateRange].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedBetween(dateRange: ClosedRange<Date>): AndTerm =
        KourrierReceivedDate.inRange(dateRange)

    /**
     * Returns a [ReceivedDateTerm] between the [start] and [end] [Date]s.
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun receivedBetween(start: Date, end: Date): AndTerm =
        KourrierReceivedDate.inRange(start..end)

    /**
     * Returns a [SentDateTerm] of the given [comparison] type and [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sent(
        comparison: Int,
        date: Date
    ): SentDateTerm = SentDateTerm(comparison, date)

    /**
     * Returns a [SentDateTerm] on the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentOn(date: Date): SentDateTerm = KourrierSentDate.eq(date)

    /**
     * Returns a [SentDateTerm] on or after the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentOnOrAfter(date: Date): SentDateTerm = KourrierSentDate.ge(date)

    /**
     * Returns a [SentDateTerm] after the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentAfter(date: Date): SentDateTerm = KourrierSentDate.gt(date)

    /**
     * Returns a [SentDateTerm] on or before the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentOnOrBefore(date: Date): SentDateTerm = KourrierSentDate.le(date)

    /**
     * Returns a [SentDateTerm] before the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentBefore(date: Date): SentDateTerm = KourrierSentDate.lt(date)

    /**
     * Returns a [SentDateTerm] not on the given [date].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun notSentOn(date: Date): SentDateTerm = KourrierSentDate.ne(date)

    /**
     * Returns a [SentDateTerm] in the given [dateRange].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentBetween(dateRange: ClosedRange<Date>): AndTerm =
        KourrierSentDate.inRange(dateRange)

    /**
     * Returns a [SentDateTerm] between the [start] and [end] [Date]s.
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sentBetween(start: Date, end: Date): AndTerm =
        KourrierSentDate.inRange(start..end)

    /**
     * Returns a [MessageIDTerm] of the given [id].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun messageID(id: String): MessageIDTerm = MessageIDTerm(id)

    /**
     * Returns a [MessageNumberTerm] of the given [number].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun messageNumber(number: Int): MessageNumberTerm = MessageNumberTerm(number)

    /**
     * Returns a [SizeTerm] of the given [comparison] type and [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun size(comparison: Int, size: Int): SizeTerm = SizeTerm(comparison, size)

    /**
     * Returns a [SizeTerm] where its size is of the given [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeIs(size: Int): SizeTerm = KourrierSize.eq(size)

    /**
     * Returns a [SizeTerm] where its size is at least of the given [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeIsAtLeast(size: Int): SizeTerm = KourrierSize.ge(size)

    /**
     * Returns a [SizeTerm] where its size is larger than the given [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeIsLargerThan(size: Int): SizeTerm = KourrierSize.gt(size)

    /**
     * Returns a [SizeTerm] where its size is at most of the given [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeIsAtMost(size: Int): SizeTerm = KourrierSize.le(size)

    /**
     * Returns a [SizeTerm] where its size is smaller than the given [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeIsSmallerThan(size: Int): SizeTerm = KourrierSize.lt(size)

    /**
     * Returns a [SizeTerm] where its size is not of the given [size].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeIsNot(size: Int): SizeTerm = KourrierSize.ne(size)

    /**
     * Returns a [AndTerm] where its size is between the given [sizeRange].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeBetween(sizeRange: IntRange): AndTerm = KourrierSize.inRange(sizeRange)

    /**
     * Returns a [AndTerm] where its size is between the given [smallest] and [largest] sizes.
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun sizeBetween(smallest: Int, largest: Int): AndTerm =
        KourrierSize.inRange(smallest..largest)

    /**
     * Returns a [FlagTerm] of the given [flags] along with the [set] value.
     * - If [set] is true, then the search will be applied to the items that have the [flags].
     * - If [set] is false, then the search will be applied to the items that do not have the [flags].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun flags(flags: KourrierFlags, set: Boolean): FlagTerm = FlagTerm(flags.rawFlags, set)

    /**
     * Returns a [ModifiedSinceTerm] of the given [modificationSequence].
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun modifiedSince(modificationSequence: Long): ModifiedSinceTerm =
        ModifiedSinceTerm(modificationSequence)

    /**
     * Returns an [OlderTerm] of the given [limit].
     * (after the index [limit])
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun older(limit: Int): OlderTerm = OlderTerm(limit)

    /**
     * Returns an [YoungerTerm] of the given [limit].
     * (before the index [limit])
     *
     * **(Must be added manually using `+` or `!`)**
     */
    fun younger(limit: Int): YoungerTerm = YoungerTerm(limit)

    /**
     * Sorts the search results by the terms applied in the [callback]
     * with a [KourrierSort] DSL builder as its receiver.
     */
    fun sortedBy(callback: KourrierSort.() -> Unit) {
        val builder = KourrierSort()
        builder.callback()
        sortedBy.addAll(builder.build())
    }

    /**
     * Adds [this] received [SearchTerm] to the [KourrierSearch].
     */
    operator fun SearchTerm.unaryPlus() {
        terms += this
    }

    /**
     * Adds [this] received reverse [SearchTerm] ([NotTerm]) to the [KourrierSearch].
     */
    operator fun SearchTerm.not() {
        terms.add(NotTerm(this))
    }
}
