package com.cablemc.pokemoncobbled.common.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.Transform

/**
 * A transform that will continue emitting values for as long as the given predicate is met.
 *
 * This will only unsubscribe the stream once a value is emitted and the predicate is false.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
class TakeWhileTransform<I>(private val predicate: (I) -> Boolean) : Transform<I, I> {
    override fun invoke(input: I): I {
        if (predicate(input)) {
            return input
        } else {
            noTransform(terminate = true)
        }
    }
}