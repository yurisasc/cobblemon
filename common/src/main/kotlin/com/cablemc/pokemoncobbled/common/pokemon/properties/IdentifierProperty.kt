package com.cablemc.pokemoncobbled.common.pokemon.properties

import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonProperty
import net.minecraft.util.Identifier

/**
 * A [CustomPokemonProperty] that uses [Identifier]s for its value.
 *
 * @property key The key of the property.
 * @property value The attached value.
 *
 * @author Licious
 * @since August 9th, 2022
 */
abstract class IdentifierProperty(val key: String, val value: Identifier) : CustomPokemonProperty {

    override fun asString() = "${this.key}=${this.value}"

}