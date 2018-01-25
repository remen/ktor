package io.ktor.client.cio

import kotlinx.coroutines.experimental.*
import java.util.*
import java.util.concurrent.locks.*

class Semaphore(val limit: Int) {
    private var lock = ReentrantLock()
    private var visitors = 0
    private val waiters: Queue<CancellableContinuation<Unit>> = LinkedList()

    init {
        assert(limit > 0) { "Semaphore limit should be > 0" }
    }

    suspend fun enter() {
        while (true) {
            lock.lock()
            if (visitors < limit) {
                ++visitors
                lock.unlock()
                return
            }

            suspendCancellableCoroutine<Unit> {
                waiters.offer(it)
                lock.unlock()
            }
        }
    }

    fun leave() {
        lock.lock()
        if (visitors == 0) {
            lock.unlock()
            throw IllegalStateException("Semaphore is empty")
        }

        visitors--
        val waiter = waiters.poll()
        lock.unlock()
        waiter?.resume(Unit)
    }
}
