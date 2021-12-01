package com.cablemc.pokemoncobbled.common.api.reactive.pipes

import com.cablemc.pokemoncobbled.common.api.reactive.Transform

/**
 * A transform that transforms the emitted values from one value to another using the given mapping function.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
class MapTransform<I, O>(private val mapping: (I) -> O) : Transform<I, O> {
    override fun invoke(input: I): O {
        return mapping(input)
    }
}