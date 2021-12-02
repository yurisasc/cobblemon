package com.cablemc.pokemoncobbled.common.api.reactive

/**
 * A specific exception that allows canceled transformations to occur in pipes.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
class NoTransformThrowable(val terminate: Boolean) : Throwable()