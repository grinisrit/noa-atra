package com.grinisrit.crypto

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.test.Test

class BasicLoopActor(
    val coroutineScope: CoroutineScope,
    private val onStartActor: () -> Unit,
    private val blockingOperation: () -> Unit,
    private val onStopActor: () -> Unit
) {

    val actorStatus: Channel<Boolean> = Channel()

    private val messageQueue: Channel<Boolean> = Channel()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun startActor(): Unit {
        onStartActor()
        coroutineScope.launch { messageQueue.send(true) }
        for (status in messageQueue) {
            if (status)
                try {
                    println("Running blocking operation ...")
                    delay(500L)
                    blockingOperation()
                    coroutineScope.launch {
                            if(!messageQueue.isClosedForSend) messageQueue.send(true)
                    }
                } catch (e: Throwable) {
                    println("Operation failed")
                }
            else {
                println("Received stop")
            }
        }

        onStopActor()
        actorStatus.send(true)
        actorStatus.close()
    }

    suspend fun stopActor(): Unit {
        messageQueue.send(false)
        messageQueue.close()
    }



}

class TestActor {

    @Test
    fun testBasicLoop(): Unit = runBlocking {
        println("Start test")

        val actor = BasicLoopActor(
            this,
            {
                println("FriendlyChap: Hey I am starting!")
            },
            {
                println("FriendlyChap: Got some hard work to do ...")
            },
            {
                println("FriendlyChap: Ok stopping it all!")
            }
        )

        val failedActor = BasicLoopActor(
            this,
            {
                println("FailedDude: Hey I think I am going to fail!")
            },
            {
                println("FailedDude: Right, will try to lift that ... oh no!")
                throw RuntimeException()
            },
            {
                println("FailedDude: I told you I will fail!")
            }
        )

        launch { actor.startActor() }
        launch { failedActor.startActor() }
        println("Launched all actors ...")
        delay(2000L)
        println("Going to stop them all!")
        launch {  actor.stopActor() }
        launch {  failedActor.stopActor() }

        for(actorStatus in actor.actorStatus)
            println("FriendlyChap done: $actorStatus")

        for(actorStatus in failedActor.actorStatus)
            println("FailedDude done: $actorStatus")

        println("All good")
    }

}