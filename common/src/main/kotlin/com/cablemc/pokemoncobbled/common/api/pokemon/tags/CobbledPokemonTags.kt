package com.cablemc.pokemoncobbled.common.api.pokemon.tags

/**
 * A collection of commonly used tags in the mod.
 *
 * @author Licious
 * @since August 8th, 2022
 */
object CobbledPokemonTags {

    /**
     * Represents a legendary Pokémon.
     */
    const val LEGENDARY = "legendary"

    /**
     * Represents a mythical Pokémon.
     * In Cobbled terms they do not exist since we do not share the concept of timed event only Pokémon but the official ones are still tagged.
     */
    const val MYTHICAL = "mythical"

    /**
     * Represents Pokémon that originate from Ultra Space.
     */
    const val ULTRA_BEAST = "ultra_beast"

    /**
     * Represents the pseudo legendary Pokémon.
     */
    const val PSEUDO_LEGENDARY = "pseudo_legendary"

    /**
     * Represents a Pokémon that has multiple forms depending on the region they're from.
     * In Cobbled/Minecraft terms there are no regions, but we follow the official concept.
     */
    const val REGIONAL = "regional"

    /**
     * Represents an unofficial Pokémon that isn't necessarily a brand-new form but just a visual variation of an existing one.
     */
    const val TEXTURED = "textured"

}