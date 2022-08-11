package me.exerro.channels

import java.util.concurrent.ArrayBlockingQueue

/**
 * Create a channel that feeds back to itself. The channel can pull values that
 * have previously been pushed to the same channel.
 *
 *     val channel = Channel.createLoopback<Int>()
 *
 *     channel.push(1)
 *     channel.push(2)
 *
 *     assert(channel.pull() == 1)
 *     assert(channel.pull() == 2)
 *
 * @see createPair
 * @see Channel
 * @see Channel.push
 * @see Channel.pullOrThrow
 */
fun <T: Any> Channel.Companion.createLoopback(
    bufferCapacity: Int = DEFAULT_BUFFER_SIZE
): Channel<T, T> {
    val buffer = ArrayBlockingQueue<T>(bufferCapacity)
    return createFromBuffers(buffer, buffer)
}
