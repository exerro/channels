package me.exerro.channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds

abstract class BaseChannelTests {
    // TODO: rename
    abstract fun `test tryPush and tryPull`()
    abstract fun testPushOrElse()
    abstract fun testTryPush()
    abstract fun testPush()
    abstract fun testPushOrElseSync()
    // TODO: tryPushSync
    abstract fun testPushOrThrowSync()
    abstract fun testPullOrElse()
    abstract fun testPull()
    abstract fun testPullOrDefault()
    abstract fun testPullOrThrow()
    abstract fun testPullOrElseSync()
    abstract fun testPullSync()
    abstract fun testPullOrDefaultSync()
    abstract fun testPullOrThrowSync()

    // TODO: async versions of below

    // TODO: rename sync
    protected fun <T: Any> `test tryPush and tryPull default helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        firstValue: T,
        secondValue: T,
    ) {
        assertEquals(expected = true, pushChannel.tryPushSync(firstValue))
        assertEquals(expected = true, pushChannel.tryPushSync(secondValue))
        assertEquals(expected = firstValue, pullChannel.pullSync())
        assertEquals(expected = secondValue, pullChannel.pullSync())
        assertEquals(expected = null, pullChannel.pullSync())
    }

    // TODO: rename sync
    protected fun <T: Any> `test tryPush and tryPull small helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        firstValue: T,
        secondValue: T,
    ) {
        assertEquals(expected = true, pushChannel.tryPushSync(firstValue))
        assertEquals(expected = false, pushChannel.tryPushSync(secondValue))
        assertEquals(expected = firstValue, pullChannel.pullSync())
        assertEquals(expected = null, pullChannel.pullSync())
    }

    /**
     * Test that pushOrElse correctly calls orElse when pushing a value fails.
     * The pushChannel is expected to have capacity for exactly 1 value, and not
     * be modified during the call to this function. The test runs with no
     * timeout, a small timeout, and a large timeout, where the first two cases
     * are expected to fail.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testPushOrElse helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        initialValue: T,
        firstValue: T,
        secondValue: T,
        thirdValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        var firstOrElseCalled = false
        var secondOrElseCalled = false
        var thirdOrElseCalled = false

        assertEquals(expected = true, pushChannel.tryPush(initialValue))

        launch {
            assertEquals(expected = false, pushChannel.pushOrElse(firstValue, timeout = firstTimeout) {
                firstOrElseCalled = true
                false
            })
            assertEquals(expected = true, firstOrElseCalled)
        }

        launch {
            assertEquals(expected = false, pushChannel.pushOrElse(secondValue, timeout = secondTimeout) {
                secondOrElseCalled = true
                false
            })
            assertEquals(expected = true, secondOrElseCalled)
        }

        launch {
            assertEquals(expected = true, pushChannel.pushOrElse(thirdValue, timeout = thirdTimeout) {
                thirdOrElseCalled = true
                false
            })
            assertEquals(expected = false, thirdOrElseCalled)
        }

        delay((secondTimeout + thirdTimeout) / 2)
        assertEquals(expected = initialValue, pullChannel.pull())
        delay(standardTimeout)
        assertEquals(expected = thirdValue, pullChannel.pull())
        delay(standardTimeout)
    }

    /**
     * Test that tryPush correctly calls orElse when pushing a value  fails. The
     * pushChannel is expected to have capacity for exactly 1 value, and not be
     * modified during the call to this function. The test runs with no timeout,
     * a small timeout, and a large timeout, where the first two cases are
     * expected to fail.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testTryPush helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        initialValue: T,
        firstValue: T,
        secondValue: T,
        thirdValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        assertEquals(expected = true, pushChannel.tryPush(initialValue))

        launch {
            assertEquals(expected = false, pushChannel.tryPush(firstValue, timeout = firstTimeout))
        }

        launch {
            assertEquals(expected = false, pushChannel.tryPush(secondValue, timeout = secondTimeout))
        }

        launch {
            assertEquals(expected = true, pushChannel.tryPush(thirdValue, timeout = thirdTimeout))
        }

        delay((secondTimeout + thirdTimeout) / 2)
        assertEquals(expected = initialValue, pullChannel.pull())
        delay(standardTimeout)
        assertEquals(expected = thirdValue, pullChannel.pull())
        delay(standardTimeout)
    }

    /**
     * Test that push correctly throws when pushing a value fails. The
     * pushChannel is expected to have capacity for exactly 1 value, and not be
     * modified during the call to this function. The test runs with no timeout,
     * a small timeout, and a large timeout, where the first two cases are
     * expected to fail.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testPush helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        initialValue: T,
        firstValue: T,
        secondValue: T,
        thirdValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        assertEquals(expected = true, pushChannel.tryPush(initialValue))

        launch {
            assertFailsWith(Channel.PushException::class) {
                pushChannel.push(firstValue, timeout = firstTimeout)
            }
        }

        launch {
            assertFailsWith(Channel.PushException::class) {
                pushChannel.push(secondValue, timeout = secondTimeout)
            }
        }

        launch {
            pushChannel.push(thirdValue, timeout = thirdTimeout)
        }

        delay((secondTimeout + thirdTimeout) / 2)
        assertEquals(expected = initialValue, pullChannel.pull())
        delay(standardTimeout)
        assertEquals(expected = thirdValue, pullChannel.pull())
        delay(standardTimeout)
    }

    /**
     * Test that pushOrElseSync correctly calls orElse when pushing a value
     * fails. The pushChannel is expected to have capacity for exactly 1 value,
     * and not be modified during the call to this function. The test runs with
     * no timeout, a small timeout, and a large timeout, where the first two
     * cases are expected to fail.
     */
     protected fun <T: Any> `testPushOrElseSync helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        initialValue: T,
        firstValue: T,
        secondValue: T,
        thirdValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        var firstOrElseCalled = false
        var secondOrElseCalled = false
        var thirdOrElseCalled = false

        assertEquals(expected = true, pushChannel.tryPushSync(initialValue))

        thread(start = true) {
            assertEquals(expected = false, pushChannel.pushOrElseSync(firstValue, timeout = firstTimeout) {
                firstOrElseCalled = true
                false
            })
            assertEquals(expected = true, firstOrElseCalled)
        }

        thread(start = true) {
            assertEquals(expected = false, pushChannel.pushOrElseSync(secondValue, timeout = secondTimeout) {
                secondOrElseCalled = true
                false
            })
            assertEquals(expected = true, secondOrElseCalled)
        }

        thread(start = true) {
            assertEquals(expected = true, pushChannel.pushOrElseSync(thirdValue, timeout = thirdTimeout) {
                thirdOrElseCalled = true
                false
            })
            assertEquals(expected = false, thirdOrElseCalled)
        }

        sleep(((secondTimeout + thirdTimeout) / 2).inWholeMilliseconds)
        assertEquals(expected = initialValue, pullChannel.pullSync())
        sleep(standardTimeout.inWholeMilliseconds)
        assertEquals(expected = thirdValue, pullChannel.pullSync())
        sleep(standardTimeout.inWholeMilliseconds)
    }

    /**
     * Test that pushSync correctly throws when pushing a value fails.
     * The pushChannel is expected to have capacity for exactly 1 value, and not
     * be modified during the call to this function. The test runs with no
     * timeout, a small timeout, and a large timeout, where the first two cases
     * are expected to fail.
     */
    protected fun <T: Any> `testPushSync helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        initialValue: T,
        firstValue: T,
        secondValue: T,
        thirdValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        assertEquals(expected = true, pushChannel.tryPushSync(initialValue))

        thread(start = true) {
            assertFailsWith(Channel.PushException::class) {
                pushChannel.pushSync(firstValue, timeout = firstTimeout)
            }
        }

        thread(start = true) {
            assertFailsWith(Channel.PushException::class) {
                pushChannel.pushSync(secondValue, timeout = secondTimeout)
            }
        }

        thread(start = true) {
            pushChannel.pushSync(thirdValue, timeout = thirdTimeout)
        }

        sleep(((secondTimeout + thirdTimeout) / 2).inWholeMilliseconds)
        assertEquals(expected = initialValue, pullChannel.pullSync())
        sleep(standardTimeout.inWholeMilliseconds)
        assertEquals(expected = thirdValue, pullChannel.pullSync())
        sleep(standardTimeout.inWholeMilliseconds)
    }

    /**
     * Test that pullOrElse correctly calls orElse when pulling a value fails.
     * The pullChannel is expected to be empty, and not be modified during the
     * call to this function. The test runs with no timeout, a small timeout,
     * and a large timeout, where the first two cases are expected to yield no
     * value.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testPullOrElse helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pushValue: T,
        firstOrElseValue: T,
        secondOrElseValue: T,
        thirdOrElseValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        var firstOrElseCalled = false
        var secondOrElseCalled = false
        var thirdOrElseCalled = false

        launch {
            assertEquals(expected = firstOrElseValue, pullChannel.pullOrElse(timeout = firstTimeout) {
                firstOrElseCalled = true
                firstOrElseValue
            })
            assertEquals(expected = true, firstOrElseCalled)
        }

        launch {
            assertEquals(expected = secondOrElseValue, pullChannel.pullOrElse(timeout = secondTimeout) {
                secondOrElseCalled = true
                secondOrElseValue
            })
            assertEquals(expected = true, secondOrElseCalled)
        }

        launch {
            assertEquals(expected = pushValue, pullChannel.pullOrElse(timeout = thirdTimeout) {
                thirdOrElseCalled = true
                thirdOrElseValue
            })
            assertEquals(expected = false, thirdOrElseCalled)
        }

        delay((secondTimeout + thirdTimeout) / 2)
        pushChannel.push(pushValue)
        delay(standardTimeout)
        assertEquals(expected = null, pullChannel.pull())
    }

    /**
     * Test that pullOrNull correctly returns null when pulling a value fails.
     * The pullChannel is expected to be empty, and not be modified during the
     * call to this function. The test runs with no timeout, a small timeout,
     * and a large timeout, where the first two cases are expected to yield no
     * value.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testPull helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pushValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        for (timeout in listOf(firstTimeout, secondTimeout))
            launch {
                assertEquals(expected = null, pullChannel.pull(timeout = timeout))
            }

        launch {
            assertEquals(expected = pushValue, pullChannel.pull(timeout = thirdTimeout))
        }

        delay((secondTimeout + thirdTimeout) / 2)
        pushChannel.push(pushValue)
        delay(standardTimeout)
        assertEquals(expected = null, pullChannel.pull())
    }

    /**
     * Test that pullOrDefault correctly returns the default value when pulling
     * a value fails. The pullChannel is expected to be empty, and not be
     * modified during the call to this function. The test runs with no timeout,
     * a small timeout, and a large timeout, where the first two cases are
     * expected to yield no value.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testPullOrDefault helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pushValue: T,
        firstDefaultValue: T,
        secondDefaultValue: T,
        thirdDefaultValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        launch {
            assertEquals(expected = firstDefaultValue, pullChannel.pullOrDefault(firstDefaultValue, timeout = firstTimeout))
        }

        launch {
            assertEquals(expected = secondDefaultValue, pullChannel.pullOrDefault(secondDefaultValue, timeout = secondTimeout))
        }

        launch {
            assertEquals(expected = pushValue, pullChannel.pullOrDefault(thirdDefaultValue, timeout = thirdTimeout))
        }

        delay((secondTimeout + thirdTimeout) / 2)
        pushChannel.push(pushValue)
        delay(standardTimeout)
        assertEquals(expected = null, pullChannel.pull())
    }

    /**
     * Test that pullOrThrow correctly throws when pulling a value fails. The
     * pullChannel is expected to be empty, and not be modified during the call
     * to this function. The test runs with no timeout, a small timeout, and a
     * large timeout, where the first two cases are expected to yield no value.
     */
    context (CoroutineScope)
    protected suspend fun <T: Any> `testPullOrThrow helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pushValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        for (timeout in listOf(firstTimeout, secondTimeout))
            launch {
                assertFailsWith(Channel.PullException::class) {
                    pullChannel.pullOrThrow(timeout = timeout)
                }
            }

        launch {
            assertEquals(expected = pushValue, pullChannel.pullOrThrow(timeout = thirdTimeout))
        }

        delay((secondTimeout + thirdTimeout) / 2)
        pushChannel.push(pushValue)
        delay(standardTimeout)
        assertEquals(expected = null, pullChannel.pull())
    }

    /**
     * Test that pullOrElseSync correctly calls orElse when pulling a value
     * fails. The pullChannel is expected to be empty, and not be modified
     * during the call to this function. The test runs with no timeout, a small
     * timeout, and a large timeout, where the first two cases are expected to
     * yield no value.
     */
    protected fun <T: Any> `testPullOrElseSync helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pullValue: T,
        firstOrElseValue: T,
        secondOrElseValue: T,
        thirdOrElseValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        var firstOrElseCalled = false
        var secondOrElseCalled = false
        var thirdOrElseCalled = false

        thread(start = true) {
            assertEquals(expected = firstOrElseValue, pullChannel.pullOrElseSync(timeout = firstTimeout) {
                firstOrElseCalled = true
                firstOrElseValue
            })
            assertEquals(expected = true, firstOrElseCalled)
        }

        thread(start = true) {
            assertEquals(expected = secondOrElseValue, pullChannel.pullOrElseSync(timeout = secondTimeout) {
                secondOrElseCalled = true
                secondOrElseValue
            })
            assertEquals(expected = true, secondOrElseCalled)
        }

        thread(start = true) {
            assertEquals(expected = pullValue, pullChannel.pullOrElseSync(timeout = thirdTimeout) {
                thirdOrElseCalled = true
                thirdOrElseValue
            })
            assertEquals(expected = false, thirdOrElseCalled)
        }

        sleep(((secondTimeout + thirdTimeout) / 2).inWholeMilliseconds)
        pushChannel.pushSync(pullValue)
        sleep(standardTimeout.inWholeMilliseconds)
        assertEquals(expected = null, pullChannel.pullSync())
    }

    /**
     * Test that pullOrNullSync correctly returns null when pulling a value
     * fails. The pullChannel is expected to be empty, and not be modified
     * during the call to this function. The test runs with no timeout, a small
     * timeout, and a large timeout, where the first two cases are expected to
     * yield no value.
     */
    protected fun <T: Any> `testPullSync helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pullValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        for (timeout in listOf(firstTimeout, secondTimeout))
            thread(start = true) {
                assertEquals(expected = null, pullChannel.pullSync(timeout = timeout))
            }

        thread(start = true) {
            assertEquals(expected = pullValue, pullChannel.pullSync(timeout = thirdTimeout))
        }

        sleep(((secondTimeout + thirdTimeout) / 2).inWholeMilliseconds)
        pushChannel.pushSync(pullValue)
        sleep(standardTimeout.inWholeMilliseconds)
        assertEquals(expected = null, pullChannel.pullSync())
    }

    /**
     * Test that pullOrDefaultSync correctly returns the default value when
     * pulling a value fails. The pullChannel is expected to be empty, and not
     * be modified during the call to this function. The test runs with no
     * timeout, a small timeout, and a large timeout, where the first two cases
     * are expected to yield no value.
     */
    protected fun <T: Any> `testPullOrDefaultSync helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pullValue: T,
        firstDefaultValue: T,
        secondDefaultValue: T,
        thirdDefaultValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        thread(start = true) {
            assertEquals(expected = firstDefaultValue, pullChannel.pullOrDefaultSync(firstDefaultValue, timeout = firstTimeout))
        }

        thread(start = true) {
            assertEquals(expected = secondDefaultValue, pullChannel.pullOrDefaultSync(secondDefaultValue, timeout = secondTimeout))
        }

        thread(start = true) {
            assertEquals(expected = pullValue, pullChannel.pullOrDefaultSync(thirdDefaultValue, timeout = thirdTimeout))
        }

        sleep(((secondTimeout + thirdTimeout) / 2).inWholeMilliseconds)
        pushChannel.pushSync(pullValue)
        sleep(standardTimeout.inWholeMilliseconds)
        assertEquals(expected = null, pullChannel.pullSync())
    }

    /**
     * Test that pullOrThrowSync correctly throws when pulling a value fails.
     * The pullChannel is expected to be empty, and not be modified during the
     * call to this function. The test runs with no timeout, a small timeout,
     * and a large timeout, where the first two cases are expected to yield no
     * value.
     */
    protected fun <T: Any> `testPullOrThrowSync helper`(
        pushChannel: Channel<T, *>,
        pullChannel: Channel<*, T>,
        pullValue: T,
    ) {
        val firstTimeout = null
        val secondTimeout = standardTimeout
        val thirdTimeout = secondTimeout + standardTimeout

        for (timeout in listOf(firstTimeout, secondTimeout))
            thread(start = true) {
                assertFailsWith(Channel.PullException::class) {
                    pullChannel.pullOrThrowSync(timeout = timeout)
                }
            }

        thread(start = true) {
            assertEquals(expected = pullValue, pullChannel.pullOrThrowSync(timeout = thirdTimeout))
        }

        sleep(((secondTimeout + thirdTimeout) / 2).inWholeMilliseconds)
        pushChannel.pushSync(pullValue)
        sleep(standardTimeout.inWholeMilliseconds)
        assertEquals(expected = null, pullChannel.pullSync())
    }

    private val standardTimeout = 300.milliseconds
}
