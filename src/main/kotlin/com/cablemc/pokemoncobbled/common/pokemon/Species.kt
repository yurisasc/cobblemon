package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import net.minecraft.world.entity.EntityDimensions

class Species {
    var name: String = "bulbasaur"
    val translatedName: String
        get() = "pokemoncobbled.species.$name.name"
    var nationalPokedexNumber = 1

    val baseStats: MutableMap<Stat, Int> = mutableMapOf()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio = 0.5F
    val catchRate = 45
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var hitbox = EntityDimensions(1F, 1F, false)
    val primaryType = ElementalTypes.GRASS
    // Technically incorrect for bulbasaur but Mr. Bossman said so
    val secondaryType: ElementalType? = null
    val standardAbilities = listOf<AbilityTemplate>()
    val hiddenAbility: AbilityTemplate? = null
    val shoulderMountable: Boolean = false
    val shoulderEffect = mutableListOf<ShoulderEffect>()

    var forms = mutableListOf(FormData())

    fun types(form: Int): Iterable<ElementalType> = forms[form].types
}