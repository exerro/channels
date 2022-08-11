package me.exerro.channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

internal fun <Push: Any, Pull: Any> createFromBuffers(
    pushBuffer: ArrayBlockingQueue<Push>,
    pullBuffer: ArrayBlockingQueue<Pull>,
) = object: Channel<Push, Pull> {
    override fun hasCapacity() =
        pushBuffer.remainingCapacity() > 0

    override fun getRemainingCapacity() =
        pushBuffer.remainingCapacity()

    context(CoroutineScope)
    override suspend fun pushOrElse(value: Push, timeout: Duration?, orElse: suspend context(CoroutineScope) (Push) -> Boolean) =
        if (timeout == null) {
            pushBuffer.offer(value) || orElse(this@CoroutineScope, value)
        }
        else {
            val wasOffered = withContext(Dispatchers.IO) {
                pushBuffer.offer(value, timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS)
            }
            wasOffered || orElse(this@CoroutineScope, value)
        }

    override fun pushOrElseSync(value: Push, timeout: Duration?, orElse: (Push) -> Boolean) =
        if (timeout == null) {
            pushBuffer.offer(value) || orElse(value)
        }
        else {
            pushBuffer.offer(value, timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS) || orElse(value)
        }

    override fun hasItem() =
        pullBuffer.isNotEmpty()

    override fun countItems() =
        pullBuffer.size

    context(CoroutineScope)
    override suspend fun pullOrElse(timeout: Duration?, orElse: suspend context(CoroutineScope) () -> Pull) =
        withContext(Dispatchers.IO) {
            if (timeout == null) {
                pullBuffer.poll() ?: orElse(this@CoroutineScope)
            }
            else {
                pullBuffer.poll(timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS) ?: orElse(this@CoroutineScope)
            }
        }

    context(CoroutineScope)
    override suspend fun pull(timeout: Duration?) =
        withContext(Dispatchers.IO) {
            if (timeout == null) {
                pullBuffer.poll() ?: null
            }
            else {
                pullBuffer.poll(timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS) ?: null
            }
        }

    override fun pullOrElseSync(timeout: Duration?, orElse: () -> Pull) =
        if (timeout == null) {
            pullBuffer.poll() ?: orElse()
        }
        else {
            pullBuffer.poll(timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS) ?: orElse()
        }

    override fun pullSync(timeout: Duration?) =
        if (timeout == null) {
            pullBuffer.poll() ?: null
        }
        else {
            pullBuffer.poll(timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS) ?: null
        }
}
