package org.gern.workers

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.slf4j.LoggerFactory

val channel = Channel<String>()

fun Routing.root() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    get("/{message}") {
        coroutineScope {
            launch {
                val message = call.parameters["message"].toString()
                channel.send(message)
                logger.info("Message $message sent")
            }
        }
        call.respond(HttpStatusCode.NoContent)
    }
}

fun CoroutineScope.doWork(id: Int, channel: ReceiveChannel<String>) = launch {
    val logger = LoggerFactory.getLogger(this.javaClass)

    for (msg in channel) {
        delay(1_000)
        logger.info("Worker #$id processed $msg")
    }
}

fun Application.mainModule() {
    install(CallLogging)

    routing {
        root()
    }
}

suspend fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    coroutineScope {

        launch {
            repeat(20) {
                doWork(it, channel)
            }
        }

        embeddedServer(
                factory = Netty,
                port = port,
                module = Application::mainModule
        ).start(wait = true)
    }
}
