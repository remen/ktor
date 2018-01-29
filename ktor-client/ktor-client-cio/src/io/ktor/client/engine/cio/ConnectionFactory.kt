package io.ktor.client.engine.cio

import io.ktor.client.cio.*
import io.ktor.network.sockets.*
import io.ktor.network.sockets.Socket
import io.ktor.network.tls.*
import java.net.*
import javax.net.ssl.*

internal class ConnectionFactory(
        maxConnectionsCount: Int,
        val trustManager: X509TrustManager? = null
) {
    private val semaphore = Semaphore(maxConnectionsCount)

    suspend fun connect(
            address: InetSocketAddress,
            secure: Boolean
    ): Socket {
        semaphore.enter()
        val socket = aSocket().tcpNoDelay().tcp().connect(address)
        return if (secure) socket.tls(trustManager, address.hostName) else socket
    }

    fun release() {
        semaphore.leave()
    }
}
