package com.notkamui.kourrier.core

import javax.mail.Flags

/**
 * Wrapper around the standard [Flags.Flag].
 */
enum class KourrierFlag(val rawFlag: Flags.Flag) {
    Answered(Flags.Flag.ANSWERED),
    Deleted(Flags.Flag.DELETED),
    Draft(Flags.Flag.DRAFT),
    Flagged(Flags.Flag.FLAGGED),
    Recent(Flags.Flag.RECENT),
    Seen(Flags.Flag.SEEN),
    User(Flags.Flag.USER),
    ;
}

/**
 * Wrapper around the standard [Flags].
 */
class KourrierFlags(vararg flags: KourrierFlag) : MutableSet<KourrierFlag> {
    private val flags = flags.toMutableSet()

    /**
     * The combinator of raw [Flags] contained in this instance of [KourrierFlags].
     */
    val rawFlags: Flags
        get() = flags.fold(Flags()) { acc, next ->
            acc.add(next.rawFlag)
            acc
        }

    override val size: Int
        get() = flags.size

    override fun add(element: KourrierFlag): Boolean =
        flags.add(element)

    override fun addAll(elements: Collection<KourrierFlag>): Boolean =
        flags.addAll(elements)

    override fun clear() {
        flags.clear()
    }

    override fun iterator(): MutableIterator<KourrierFlag> =
        flags.iterator()

    override fun remove(element: KourrierFlag): Boolean =
        flags.remove(element)

    override fun removeAll(elements: Collection<KourrierFlag>): Boolean =
        flags.removeAll(elements)

    override fun retainAll(elements: Collection<KourrierFlag>): Boolean =
        flags.retainAll(elements)

    override fun contains(element: KourrierFlag): Boolean =
        flags.contains(element)

    override fun containsAll(elements: Collection<KourrierFlag>): Boolean =
        flags.containsAll(elements)

    override fun isEmpty(): Boolean =
        flags.isEmpty()
}
