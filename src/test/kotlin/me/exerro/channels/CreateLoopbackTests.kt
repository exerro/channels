package me.exerro.channels

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class CreateLoopbackTests: BaseChannelTests() {
    @Test
    override fun `test tryPush and tryPull`() {
        val defaultChannel = Channel.createLoopback<Int>()
        `test tryPush and tryPull default helper`(defaultChannel, defaultChannel, 1, 2)

        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `test tryPush and tryPull small helper`(channel, channel, 1, 2)
    }

    @Test
    override fun testPushOrElse(): Unit = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPushOrElse helper`(channel, channel, 1, 2, 3, 4)
    }

    @Test
    override fun testTryPush(): Unit = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testTryPush helper`(channel, channel, 1, 2, 3, 4)
    }

    @Test
    override fun testPush(): Unit = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPush helper`(channel, channel, 1, 2, 3, 4)
    }

    @Test
    override fun testPushOrElseSync() {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPushOrElseSync helper`(channel, channel, 1, 2, 3, 4)
    }

    @Test
    override fun testPushOrThrowSync() {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPushSync helper`(channel, channel, 1, 2, 3, 4)
    }

    @Test
    override fun testPullOrElse() = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullOrElse helper`(channel, channel, 42, 1, 2, 3)
    }

    @Test
    override fun testPull() = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPull helper`(channel, channel, 42)
    }

    @Test
    override fun testPullOrDefault() = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullOrDefault helper`(channel, channel, 42, 1, 2, 3)
    }

    @Test
    override fun testPullOrThrow() = runBlocking {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullOrThrow helper`(channel, channel, 42)
    }

    @Test
    override fun testPullOrElseSync() {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullOrElseSync helper`(channel, channel, 42, 1, 2, 3)
    }

    @Test
    override fun testPullSync() {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullSync helper`(channel, channel, 42)
    }

    @Test
    override fun testPullOrDefaultSync() {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullOrDefaultSync helper`(channel, channel, 42, 1, 2, 3)
    }

    @Test
    override fun testPullOrThrowSync() {
        val channel = Channel.createLoopback<Int>(bufferCapacity = 1)
        `testPullOrThrowSync helper`(channel, channel, 42)
    }
}
