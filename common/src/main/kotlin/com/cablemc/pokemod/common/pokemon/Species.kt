/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.abilities.AbilityPool
import com.cablemc.pokemod.common.api.data.ClientDataSynchronizer
import com.cablemc.pokemod.common.api.drop.DropTable
import com.cablemc.pokemod.common.api.pokemon.moves.Learnset
import com.cablemc.pokemod.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemod.common.api.pokemon.egg.EggGroup
import com.cablemc.pokemod.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemod.common.api.pokemon.stats.Stat
import com.cablemc.pokemod.common.api.pokemon.stats.Stats
import com.cablemc.pokemod.common.api.types.ElementalType
import com.cablemc.pokemod.common.api.types.ElementalTypes
import com.cablemc.pokemod.common.entity.PoseType.Companion.FLYING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.SWIMMING_POSES
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.pokemon.ai.PokemonBehaviour
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import net.minecraft.entity.EntityDimensions
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
class Species : ClientDataSynchronizer<Species> {
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
    var baseFriendship = 0
    val evYield = hashMapOf<Stat, Int>()
    var experienceGroup = ExperienceGroups.first()
    var hitbox = EntityDimensions(1F, 1F, false)
    var primaryType = ElementalTypes.GRASS
        internal set
    var secondaryType: ElementalType? = null
        internal set
    val abilities = AbilityPool()
    val shoulderMountable: Boolean = false
    val shoulderEffects = mutableListOf<ShoulderEffect>()
    val moves = Learnset()
    val features = mutableSetOf<String>()
    private var standingEyeHeight: Float? = null
    private var swimmingEyeHeight: Float? = null
    private var flyingEyeHeight: Float? = null
    val behaviour = PokemonBehaviour()
    val pokedex = mutableListOf<String>()
    val drops = DropTable()
    val eggCycles = 120
    val eggGroups = setOf<EggGroup>()
    // ToDo add this to the 3 guys
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

    /**
     * Initialize properties that relied on all species and forms to be loaded.
     *
     */
    internal fun initializePostLoads() {
        // These properties are lazy
        this.preEvolution?.species
        this.preEvolution?.form
    }

    fun create(level: Int = 10) = Pokemon().apply {
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
        entity.getPoseType() in SWIMMING_POSES -> this.swimmingEyeHeight ?: standingEyeHeight
        entity.getPoseType() in FLYING_POSES -> this.flyingEyeHeight ?: standingEyeHeight
        else -> this.standingEyeHeight
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.resourceIdentifier)
        buffer.writeString(this.name)
        buffer.writeInt(this.nationalPokedexNumber)
        buffer.writeMap(this.baseStats, { pb, stat -> pb.writeString(stat.id) }, { pb, value -> pb.writeSizedInt(IntSize.U_SHORT, value) })
        // Hitbox start
        buffer.writeFloat(this.hitbox.width)
        buffer.writeFloat(this.hitbox.height)
        buffer.writeBoolean(this.hitbox.fixed)
        // Hitbox end
        // ToDo remake once we have custom typing support
        buffer.writeString(this.primaryType.name)
        buffer.writeNullable(this.secondaryType) { pb, type -> pb.writeString(type.name) }
        buffer.writeCollection(this.pokedex) { pb, line -> pb.writeString(line) }
        buffer.writeCollection(this.forms) { pb, form -> form.encode(pb) }
    }

    override fun decode(buffer: PacketByteBuf) {
        this.apply {
            name = buffer.readString()
            nationalPokedexNumber = buffer.readInt()
            baseStats.putAll(buffer.readMap({ Stats.getStat(it.readString(), true) }, { it.readSizedInt(IntSize.U_SHORT) }))
            hitbox = EntityDimensions(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean())
            primaryType = ElementalTypes.getOrException(buffer.readString())
            secondaryType = buffer.readNullable { pb -> ElementalTypes.getOrException(pb.readString()) }
            pokedex.clear()
            pokedex += buffer.readList { pb -> pb.readString() }
            forms.clear()
            forms += buffer.readList{ pb -> FormData().apply { decode(pb) } }.filterNotNull()
        }
    }

    override fun shouldSynchronize(other: Species): Boolean {
        if (other.resourceIdentifier.toString() != other.resourceIdentifier.toString())
            return false
        return other.name != this.name
                || other.nationalPokedexNumber != this.nationalPokedexNumber
                || other.baseStats != this.baseStats
                || other.hitbox != this.hitbox
                || other.primaryType != this.primaryType
                || other.secondaryType != this.secondaryType
                || other.standingEyeHeight != this.standingEyeHeight
                || other.swimmingEyeHeight != this.swimmingEyeHeight
                || other.flyingEyeHeight != this.flyingEyeHeight
                || other.dynamaxBlocked != this.dynamaxBlocked
                || other.pokedex != this.pokedex
                || other.forms != this.forms
    }

    override fun toString() = name

    companion object {
        private const val VANILLA_DEFAULT_EYE_HEIGHT = .85F
    }
}