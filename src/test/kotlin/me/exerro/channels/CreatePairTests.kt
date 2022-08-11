package me.exerro.channels

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class CreatePairTests: BaseChannelTests() {
    @Test
    override fun `test tryPush and tryPull`() {
        val (defaultChannelA, defaultChannelB) = Channel.createPair<Int, String>()
        `test tryPush and tryPull default helper`(defaultChannelA, defaultChannelB, 1, 2)
        `test tryPush and tryPull default helper`(defaultChannelB, defaultChannelA, "1", "2")

        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `test tryPush and tryPull small helper`(channelA, channelB, 1, 2)
        `test tryPush and tryPull small helper`(channelB, channelA, "1", "2")
    }

    @Test
    override fun testPushOrElse(): Unit = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPushOrElse helper`(channelA, channelB, 1, 2, 3, 4)
        `testPushOrElse helper`(channelB, channelA, "1", "2", "3", "4")
    }

    @Test
    override fun testTryPush(): Unit = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testTryPush helper`(channelA, channelB, 1, 2, 3, 4)
        `testTryPush helper`(channelB, channelA, "1", "2", "3", "4")
    }

    @Test
    override fun testPush(): Unit = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPush helper`(channelA, channelB, 1, 2, 3, 4)
        `testPush helper`(channelB, channelA, "1", "2", "3", "4")
    }

    @Test
    override fun testPushOrElseSync() {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPushOrElseSync helper`(channelA, channelB, 1, 2, 3, 4)
        `testPushOrElseSync helper`(channelB, channelA, "1", "2", "3", "4")
    }

    @Test
    override fun testPushOrThrowSync() {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPushSync helper`(channelA, channelB, 1, 2, 3, 4)
        `testPushSync helper`(channelB, channelA, "1", "2", "3", "4")
    }

    @Test
    override fun testPullOrElse() = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullOrElse helper`(channelA, channelB, 42, 1, 2, 3)
        `testPullOrElse helper`(channelB, channelA, "42", "2", "3", "4")
    }

    @Test
    override fun testPull() = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPull helper`(channelA, channelB, 42)
        `testPull helper`(channelB, channelA, "42")
    }

    @Test
    override fun testPullOrDefault() = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullOrDefault helper`(channelA, channelB, 42, 1, 2, 3)
        `testPullOrDefault helper`(channelB, channelA, "42", "2", "3", "4")
    }

    @Test
    override fun testPullOrThrow() = runBlocking {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullOrThrow helper`(channelA, channelB, 42)
        `testPullOrThrow helper`(channelB, channelA, "42")
    }

    @Test
    override fun testPullOrElseSync() {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullOrElseSync helper`(channelA, channelB, 42, 1, 2, 3)
        `testPullOrElseSync helper`(channelB, channelA, "42", "2", "3", "4")
    }

    @Test
    override fun testPullSync() {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullSync helper`(channelA, channelB, 42)
        `testPullSync helper`(channelB, channelA, "42")
    }

    @Test
    override fun testPullOrDefaultSync() {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullOrDefaultSync helper`(channelA, channelB, 42, 1, 2, 3)
        `testPullOrDefaultSync helper`(channelB, channelA, "42", "2", "3", "4")
    }

    @Test
    override fun testPullOrThrowSync() {
        val (channelA, channelB) = Channel.createPair<Int, String>(bufferCapacity = 1)
        `testPullOrThrowSync helper`(channelA, channelB, 42)
        `testPullOrThrowSync helper`(channelB, channelA, "42")
    }
}
