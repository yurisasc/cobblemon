package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.util.asTranslated
import net.minecraft.entity.EntityDimensions
import net.minecraft.text.MutableText

class Species {
    var name: String = "bulbasaur"
    val translatedName: MutableText
        get() = "pokemoncobbled.species.$name.name".asTranslated()
    var nationalPokedexNumber = 1

    val baseStats = mapOf<Stat, Int>()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio = 0.5F
    val catchRate = 45
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var baseExperienceYield = 10
    var experienceGroup = ExperienceGroups.first()
    var hitbox = EntityDimensions(1F, 1F, false)
    val primaryType = ElementalTypes.GRASS
    // Technically incorrect for bulbasaur but Mr. Bossman said so
    val secondaryType: ElementalType? = null
    val standardAbilities = listOf<AbilityTemplate>()
    val hiddenAbility: AbilityTemplate? = null
    val shoulderMountable: Boolean = false
    val shoulderEffects = mutableListOf<ShoulderEffect>()
    val levelUpMoves = LevelUpMoves()

    var forms = mutableListOf(FormData())

    fun types(form: Int): Iterable<ElementalType> = forms[form].types

    fun create(level: Int = 5) = Pokemon().apply {
        species = this@Species
        this.level = level
        initialize()
    }
}