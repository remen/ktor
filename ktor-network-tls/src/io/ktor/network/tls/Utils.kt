package io.ktor.network.tls

import kotlinx.io.core.*

internal fun ByteReadPacket.duplicate(): Pair<ByteReadPacket, ByteReadPacket> {
    return this to copy()
}
