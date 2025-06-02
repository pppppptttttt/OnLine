package ru.hse.online.client.repository.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

class WebsocketClient {
    companion object {
        // FIXME
        private const val BASE_URL: String = "ws://localhost:8080"
    }

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    private val incomingMessagesMutableSF = MutableSharedFlow<String>()
    val incomingMessages: SharedFlow<String> = incomingMessagesMutableSF.asSharedFlow()

    suspend fun connect(username: String) {
        client.webSocket("$BASE_URL/ws") {
            session = this
            send(Frame.Text("/start $username"))

            launch {
                incomingMessages.collect {
                    incomingMessagesMutableSF.emit(it)
                }

            }
        }
    }

    suspend fun sendMessage(message: String) {
        session?.send(Frame.Text(message))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
        client.close()
    }
}