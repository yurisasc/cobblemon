/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.abilities.AbilityPool
import com.cablemc.pokemoncobbled.common.api.drop.DropTable
import com.cablemc.pokemoncobbled.common.api.pokemon.Learnset
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.egg.EggGroup
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.ai.PokemonBehaviour
import com.cablemc.pokemoncobbled.common.util.lang
import net.minecraft.entity.EntityDimensions
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class Species {
    var name: String = "bulbasaur"
    val translatedName: MutableText
        get() = lang("species.$name.name")
    var nationalPokedexNumber = 1

    val baseStats = hashMapOf<Stat, Int>()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio: Float = 0.5F
    val catchRate = 45
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var baseExperienceYield = 10
    var experienceGroup = ExperienceGroups.first()
    var hitbox = EntityDimensions(1F, 1F, false)
    var primaryType = ElementalTypes.GRASS
        internal set
    // Technically incorrect for bulbasaur but Mr. Bossman said so
    var secondaryType: ElementalType? = null
        internal set
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
    var dynamaxBlocked = false

    /**
     * The height in decimeters
     */
    var height = 1F
        private set

    /**
     * The weight in hectograms
     */
    var weight = 1F
        private set

    var forms = mutableListOf<FormData>()

    val standardForm by lazy { FormData().initialize(this) }

    internal val labels = emptySet<String>()

    // Only exists for use of the field in Pokémon do not expose to end user due to how the species/form data is structured
    internal val evolutions: MutableSet<Evolution> = hashSetOf()

    internal val preEvolution: PreEvolution? = null

    @Transient
    lateinit var resourceIdentifier: Identifier

    fun initialize() {
        Stats.mainStats.forEach { stat ->
            this.baseStats.putIfAbsent(stat, 1)
        }
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
        entity.isSwimming || entity.isSubmergedInWater -> this.swimmingEyeHeight
        entity.isFallFlying -> this.flyingEyeHeight
        else -> this.standingEyeHeight
    }

    internal fun encodeForClient(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.resourceIdentifier)
        buffer.writeString(this.name)
        buffer.writeInt(this.nationalPokedexNumber)
        buffer.writeMap(this.baseStats, { pb, stat -> pb.writeString(stat.id) }, { pb, value -> pb.writeInt(value) })
        // Hitbox start
        buffer.writeFloat(this.hitbox.width)
        buffer.writeFloat(this.hitbox.height)
        buffer.writeBoolean(this.hitbox.fixed)
        // Hitbox end
        // ToDo remake once we have custom typing support
        buffer.writeString(this.primaryType.name)
        buffer.writeNullable(this.secondaryType) { pb, type -> pb.writeString(type.name) }
        buffer.writeBoolean(this.dynamaxBlocked)
        buffer.writeCollection(this.pokedex) { pb, line -> pb.writeString(line) }
        buffer.writeCollection(this.forms) { pb, form -> form.encodeForClient(pb) }
    }

    internal fun decodeForClient(buffer: PacketByteBuf) {
        this.apply {
            name = buffer.readString()
            nationalPokedexNumber = buffer.readInt()
            baseStats.putAll(buffer.readMap({ Stats.getStat(it.readString(), true) }, { it.readInt() }))
            hitbox = EntityDimensions(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean())
            primaryType = ElementalTypes.getOrException(buffer.readString())
            secondaryType = buffer.readNullable { pb -> ElementalTypes.getOrException(pb.readString()) }
            dynamaxBlocked = buffer.readBoolean()
            pokedex.clear()
            pokedex += buffer.readList { pb -> pb.readString() }
            forms.clear()
            forms += buffer.readList{ pb -> FormData().apply { decodeForClient(pb) } }.filterNotNull()
        }
    }

    override fun toString() = name

    companion object {
        private const val VANILLA_DEFAULT_EYE_HEIGHT = .85F
    }
}