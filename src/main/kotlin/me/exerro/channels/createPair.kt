package me.exerro.channels

import java.util.concurrent.ArrayBlockingQueue

/**
 * Create a pair of channels. The first channel can pull [Pull] values that the
 * second has pushed. The second channel can pull [Push] values that the first
 * has pushed.
 *
 *     val (channelA, channelB) = Channel.createPair<Int, String>()
 *
 *     channelA.push(1)
 *     assert(channelB.pull() == 1)
 *
 *     channelB.push("1")
 *     assert(channelA.pull() == "1")
 *
 * @see createLoopback
 * @see Channel
 * @see Channel.push
 * @see Channel.pullOrThrow
 */
fun <Push: Any, Pull: Any> Channel.Companion.createPair(
    firstPushBufferCapacity: Int,
    secondPushBufferCapacity: Int,
): Pair<Channel<Push, Pull>, Channel<Pull, Push>> {
    val firstPushBuffer = ArrayBlockingQueue<Push>(firstPushBufferCapacity)
    val secondPushBuffer = ArrayBlockingQueue<Pull>(secondPushBufferCapacity)

    return createFromBuffers(pushBuffer = firstPushBuffer, pullBuffer = secondPushBuffer) to
            createFromBuffers(pushBuffer = secondPushBuffer, pullBuffer = firstPushBuffer)
}

/**
 * Overload of [createPair] where buffer capacities of both channels are equal.
 *
 * @see createPair
 */
fun <Push: Any, Pull: Any> Channel.Companion.createPair(
    bufferCapacity: Int = DEFAULT_BUFFER_SIZE,
) = createPair<Push, Pull>(
    firstPushBufferCapacity = bufferCapacity,
    secondPushBufferCapacity = bufferCapacity
)
