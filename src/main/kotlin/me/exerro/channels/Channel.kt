package me.exerro.channels

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration

/**
 * A channel is a bidirectional communication object capable of pushing and
 * pulling values. The type of values pushed ([Push]) can differ from the
 * type of values pulled ([Pull].)
 *
 * Channels can be created using factory functions such as [createLoopback] and
 * [createPair].
 *
 * @see tryPush
 * @see tryPull
 * @see push
 * @see pullOrThrow
 */
interface Channel<Push: Any, Pull: Any>: PullOnlyChannel<Pull> {
    /**
     * Return whether the channel currently has capacity to push items to.
     *
     * @see getRemainingCapacity
     * @see push
     * @see pushOrElse
     */
    fun hasCapacity(): Boolean

    /**
     * Return the current number of items that may be pushed before the channel
     * reaches its capacity, assuming none of the channel's items are pulled.
     *
     * @see hasCapacity
     * @see push
     * @see pullOrThrow
     */
    fun getRemainingCapacity(): Int

    ////////////////////////////////////////////////////////////

    /**
     * Attempt to push [value] to this channel. If the operation succeeds,
     * `true` will be returned. Otherwise, [orElse] will be called with [value],
     * and that return value will be returned. If a [timeout] is given, the
     * operation will fail if the value cannot be pushed within the specified
     * time. Otherwise, the operation will fail if the value cannot be pushed
     * with minimal delay.
     *
     * Note: minimal delay accounts for latency across the link. For example,
     * if the channel is communicating over a network socket, having no timeout
     * will only fail once it has been confirmed that the remote push has
     * failed.
     *
     * @see pushOrElseSync
     * @see tryPush
     * @see push
     */
    context (CoroutineScope)
    suspend fun pushOrElse(
        value: Push,
        timeout: Duration? = null,
        orElse: suspend context(CoroutineScope) (Push) -> Boolean,
    ): Boolean

    /**
     * Similar to [pushOrElse], but just returns `false` if [value] cannot be
     * pushed.
     *
     * @see tryPushSync
     * @see pushOrElse
     * @see push
     */
    context (CoroutineScope)
    suspend fun tryPush(
        value: Push,
        timeout: Duration? = null,
    ): Boolean = pushOrElse(value, timeout = timeout) { false }

    /**
     * Similar to [pushOrElse], but throws a [PushException] if [value] cannot
     * be pushed.
     *
     * @throws PushException if [value] cannot be pushed.
     * @see pushSync
     * @see pushOrElse
     * @see tryPush
     */
    context (CoroutineScope)
    @Throws(PushException::class)
    suspend fun push(
        value: Push,
        timeout: Duration? = null,
    ) {
        pushOrElse(value = value, timeout = timeout) { throwPushTimeout(timeout) }
    }

    ////////////////////////////////////////////////////////////

    /**
     * Synchronous version of [pushOrElse].
     *
     * @see pushOrElse
     * @see tryPushSync
     * @see pushSync
     */
    fun pushOrElseSync(
        value: Push,
        timeout: Duration? = null,
        orElse: (Push) -> Boolean,
    ): Boolean

    /**
     * Synchronous version of [tryPush].
     *
     * @see tryPush
     * @see pushOrElseSync
     * @see pushSync
     */
    fun tryPushSync(
        value: Push,
        timeout: Duration? = null,
    ): Boolean = pushOrElseSync(value, timeout = timeout) { false }

    /**
     * Synchronous version of [push].
     *
     * @throws PushException if [value] cannot be pushed.
     * @see push
     * @see pushOrElseSync
     * @see tryPushSync
     */
    @Throws(PushException::class)
    fun pushSync(
        value: Push,
        timeout: Duration? = null,
    ) {
        pushOrElseSync(value = value, timeout = timeout) { throwPushTimeout(timeout) }
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Thrown when an item cannot be pushed to a channel.
     */
    class PushException(override val message: String): Exception(message)

    /**
     * Thrown when an item cannot be pulled from a channel.
     */
    class PullException(override val message: String): Exception(message)

    ////////////////////////////////////////////////////////////////////////////

    /** @see Channel */
    companion object {
        /**
         * Default number of items that may be pushed to a channel without any
         * being pulled.
         */
        const val DEFAULT_BUFFER_SIZE = 1024

        private fun throwPushTimeout(timeout: Duration?): Nothing {
            throw PushException("Failed to push${timeoutText(timeout)}")
        }

        internal fun timeoutText(timeout: Duration?) =
            if (timeout == null) "" else
                " within the $timeout timeout"
    }
}
