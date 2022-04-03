package com.cablemc.pokemoncobbled.common.net

/**
 * Convenient breakdown of different sizes of integer for use in (de)serializing from byte buffers.
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
enum class IntSize {
    INT,
    SHORT,
    U_SHORT,
    BYTE,
    U_BYTE
}