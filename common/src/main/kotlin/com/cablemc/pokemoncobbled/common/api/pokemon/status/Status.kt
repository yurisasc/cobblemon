package com.cablemc.pokemoncobbled.common.api.pokemon.status

import net.minecraft.util.Identifier

/**
 * Represents the base of a status
 *
 * @author Deltric
 */
open class Status(
    val name: Identifier,
    val showdownName: String = ""
) {

}