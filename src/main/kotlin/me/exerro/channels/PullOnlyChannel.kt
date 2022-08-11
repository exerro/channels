package me.exerro.channels

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration

/**
 * A pull only channel is a variation of [Channel] where items cannot be pushed.
 *
 * @see Channel
 * @see pull
 * @see pullOrElse
 * @see pullOrThrow
 */
interface PullOnlyChannel<Pull: Any> {
    /**
     * Return whether a value can currently be pulled from the channel.
     *
     * @see countItems
     * @see pull
     * @see pullOrElse
     */
    fun hasItem(): Boolean

    /**
     * Return the current number of items that may be pulled before the channel
     * runs out of items, assuming no further items are pushed to the channel.
     *
     * @see hasItem
     * @see pull
     * @see pullOrElse
     */
    fun countItems(): Int

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Attempt to pull a value from this channel. If the operation succeeds, the
     * value will be returned. Otherwise, [orElse] will be called and that
     * return value will be returned. If a [timeout] is given, the operation
     * will fail if a value cannot be pulled within the specified time.
     * Otherwise, the operation will fail only if the value cannot be pulled
     * with minimal delay.
     *
     * Note: minimal delay accounts for latency across the link. For example,
     * if the channel is communicating over a network socket, having no timeout
     * will only fail once it has been confirmed that the remote pull has
     * failed.
     *
     * @see pullOrElseSync
     * @see pull
     * @see pullOrDefault
     * @see pullOrThrow
     */
    context (CoroutineScope)
    suspend fun pullOrElse(
        timeout: Duration? = null,
        orElse: suspend context(CoroutineScope) () -> Pull,
    ): Pull

    /**
     * Similar to [pullOrElse] but returns `null` if an item cannot be pulled
     * within the specified [timeout].
     *
     * @see pullSync
     * @see pullOrElse
     * @see pullOrDefault
     * @see pullOrThrow
     */
    context (CoroutineScope)
    suspend fun pull(
        timeout: Duration? = null,
    ): Pull?

    /**
     * Similar to [pullOrElse] but returns [defaultValue] if an item cannot be
     * pulled within the specified [timeout].
     *
     * @see pullOrDefaultSync
     * @see pullOrElse
     * @see pull
     * @see pullOrThrow
     */
    context (CoroutineScope)
    suspend fun pullOrDefault(
        defaultValue: Pull,
        timeout: Duration? = null,
    ): Pull = pullOrElse(timeout = timeout) { defaultValue }

    /**
     * Similar to [pullOrElse] but throws a
     * [PullException][Channel.PullException] if an item cannot be pulled within
     * the specified [timeout].
     *
     * @see pullOrThrowSync
     * @see pullOrElse
     * @see pull
     * @see pullOrDefault
     */
    context (CoroutineScope)
    @Throws(Channel.PullException::class)
    suspend fun pullOrThrow(
        timeout: Duration? = null,
    ): Pull = pullOrElse(timeout = timeout) { throwPullTimeout(timeout) }

    ////////////////////////////////////////////////////////////

    /**
     * Synchronous version of [pullOrElse].
     *
     * @see pullOrElse
     * @see pullSync
     * @see pullOrDefaultSync
     * @see pullOrThrowSync
     */
    fun pullOrElseSync(
        timeout: Duration? = null,
        orElse: () -> Pull,
    ): Pull

    /**
     * Synchronous version of [pull].
     *
     * @see pull
     * @see pullOrElseSync
     * @see pullOrDefaultSync
     * @see pullOrThrowSync
     */
    fun pullSync(
        timeout: Duration? = null,
    ): Pull?

    /**
     * Synchronous version of [pullOrDefault].
     *
     * @see pullOrDefault
     * @see pullOrElseSync
     * @see pullSync
     * @see pullOrThrowSync
     */
    fun pullOrDefaultSync(
        default: Pull,
        timeout: Duration? = null,
    ): Pull = pullOrElseSync(timeout = timeout) { default }

    /**
     * Synchronous version of [pullOrThrow].
     *
     * @see pullOrThrow
     * @see pullOrElseSync
     * @see pullSync
     * @see pullOrDefaultSync
     */
    @Throws(Channel.PullException::class)
    fun pullOrThrowSync(
        timeout: Duration? = null,
    ): Pull = pullOrElseSync(timeout = timeout) { throwPullTimeout(timeout) }

    /** @see PullOnlyChannel */
    companion object {
        private fun throwPullTimeout(timeout: Duration?): Nothing {
            throw Channel.PullException("Failed to pull${Channel.timeoutText(timeout)}")
        }
    }
}
