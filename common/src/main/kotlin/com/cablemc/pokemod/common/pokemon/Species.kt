/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.abilities.AbilityPool
import com.cablemc.pokemod.common.api.drop.DropTable
import com.cablemc.pokemod.common.api.pokemon.Learnset
import com.cablemc.pokemod.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemod.common.api.pokemon.egg.EggGroup
import com.cablemc.pokemod.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemod.common.api.pokemon.stats.Stat
import com.cablemc.pokemod.common.api.types.ElementalType
import com.cablemc.pokemod.common.api.types.ElementalTypes
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.pokemon.ai.PokemonBehaviour
import com.cablemc.pokemod.common.util.lang
import net.minecraft.entity.EntityDimensions
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class Species {
    var name: String = "bulbasaur"
    val translatedName: MutableText
        get() = lang("species.$name.name")
    var nationalPokedexNumber = 1

    val baseStats = mapOf<Stat, Int>()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio: Float = 0.5F
    val catchRate = 45
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var baseExperienceYield = 10
    var experienceGroup = ExperienceGroups.first()
    var hitbox = EntityDimensions(1F, 1F, false)
    val primaryType = ElementalTypes.GRASS
    // Technically incorrect for bulbasaur but Mr. Bossman said so
    val secondaryType: ElementalType? = null
    val abilities = AbilityPool()
    val shoulderMountable: Boolean = false
    val shoulderEffects = mutableListOf<ShoulderEffect>()
    val moves = Learnset()
    val features = mutableSetOf<String>()
    private val standingEyeHeight: Float? = null
    private val swimmingEyeHeight: Float? = null
    private val flyingEyeHeight: Float? = null
    val behaviour = PokemonBehaviour()
    val pokedex = mutableListOf<String>()
    val drops = DropTable()
    val eggCycles = 120
    val eggGroups = setOf<EggGroup>()
    val dynamaxBlocked = false
    // In metric
    val height = 1F
    // In metric
    val weight = 1F

    var forms = mutableListOf<FormData>()

    val standardForm by lazy { FormData().initialize(this) }

    internal val labels = emptySet<String>()

    // Only exists for use of the field in Pokémon do not expose to end user due to how the species/form data is structured
    internal val evolutions: MutableSet<Evolution> = hashSetOf()

    internal val preEvolution: PreEvolution? = null

    @Transient
    lateinit var resourceIdentifier: Identifier

    fun initialize() {
        for (form in forms) {
            form.initialize(this)
        }
    }

    fun create(level: Int = 5) = Pokemon().apply {
        species = this@Species
        this.level = level
        initialize()
    }

    fun getForm(aspects: Set<String>) = forms.firstOrNull { it.aspects.all { it in aspects } } ?: standardForm

    fun eyeHeight(entity: PokemonEntity): Float {
        val multiplier = this.resolveEyeHeight(entity) ?: VANILLA_DEFAULT_EYE_HEIGHT
        return entity.height * multiplier
    }

    private fun resolveEyeHeight(entity: PokemonEntity): Float? = when {
        entity.isSwimming || entity.isSubmergedInWater -> this.swimmingEyeHeight ?: standingEyeHeight
        entity.isFallFlying -> this.flyingEyeHeight ?: standingEyeHeight
        else -> this.standingEyeHeight
    }

    override fun toString() = name

    companion object {
        private const val VANILLA_DEFAULT_EYE_HEIGHT = .85F
    }
}