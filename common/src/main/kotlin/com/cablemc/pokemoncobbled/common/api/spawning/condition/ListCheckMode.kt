package com.cablemc.pokemoncobbled.common.api.spawning.condition

/**
 * The way a list of condition-type properties are checked.
 *
 * ALL represents the need for every element of the list to be present.
 * ANY represents the need for only a single elemt of the list to be present.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
enum class ListCheckMode {
    /** Represents the need for every element of the list to be present. */
    ALL,
    /** Represents the need for only a single elemt of the list to be present. */
    ANY
}