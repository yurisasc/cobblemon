package com.cablemc.pokemoncobbled.common.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.Transform

/**
 * A transform which will only take some number of emissions before terminating the observable subscription.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
class TakeFirstTransform<I>(private var amount: Int = 1) : Transform<I, I> {
    override fun invoke(input: I): I {
        if (amount > 0) {
            amount--
            return input
        } else {
            noTransform(terminate = true)
        }
    }
}