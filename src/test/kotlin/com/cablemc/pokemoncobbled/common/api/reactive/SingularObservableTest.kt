package com.cablemc.pokemoncobbled.common.api.reactive

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SingularObservableTest {

    @Test
    fun `emitting more than once causes exception`() {
        val observable = SingularObservable<Unit>()
        observable.emit(Unit)
        val exception = assertThrows(IllegalStateException::class.java) { observable.emit(Unit) }
        assertEquals("This observable is already completed!", exception.message)
    }

}