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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

fun Routing.root() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    get("/") {
        coroutineScope {
            launch {
                delay(2_000)
                logger.info("end")
            }

            logger.info("out")

        }
        call.respond(HttpStatusCode.NoContent)
    }
}

fun Application.mainModule() {
    install(CallLogging)

    routing {
        root()
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(
            factory = Netty,
            port = port,
            module = Application::mainModule
    ).start(wait = true)
}
