package com.cobblemon.mod.common.pokemon.types

import com.cobblemon.mod.common.api.resistance.ResistanceMap
import com.cobblemon.mod.common.api.types.ElementalType

/**
 * Showdown type = TypeInfo
 *
 * @property name Doubles up as the ID, represented by us as [ElementalType.showdownId]
 * @property damageTaken The damage taken from various effects, represented by us as [ResistanceMap].
 */
internal data class ShowdownElementalTypeDTO(
    val name: String,
    val damageTaken: Map<String, Int>
)
