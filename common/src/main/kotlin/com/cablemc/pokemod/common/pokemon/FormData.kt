/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.abilities.AbilityPool
import com.cablemc.pokemod.common.api.drop.DropTable
import com.cablemc.pokemod.common.api.moves.MoveTemplate
import com.cablemc.pokemod.common.api.net.Decodable
import com.cablemc.pokemod.common.api.net.Encodable
import com.cablemc.pokemod.common.api.pokemon.Learnset
import com.cablemc.pokemod.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemod.common.api.pokemon.egg.EggGroup
import com.cablemc.pokemod.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.api.pokemon.experience.ExperienceGroup
import com.cablemc.pokemod.common.api.pokemon.stats.Stat
import com.cablemc.pokemod.common.api.pokemon.stats.Stats
import com.cablemc.pokemod.common.api.types.ElementalType
import com.cablemc.pokemod.common.api.types.ElementalTypes
import com.cablemc.pokemod.common.entity.PoseType
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.pokemon.ai.FormPokemonBehaviour
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import com.google.gson.annotations.SerializedName
import net.minecraft.entity.EntityDimensions
import net.minecraft.network.PacketByteBuf

class FormData(
    name: String = "normal",
    @SerializedName("baseStats")
    private var _baseStats: MutableMap<Stat, Int>? = null,
    @SerializedName("maleRatio")
    private val _maleRatio: Float? = null,
    @SerializedName("baseScale")
    private var _baseScale: Float? = null,
    @SerializedName("hitbox")
    private var _hitbox: EntityDimensions? = null,
    @SerializedName("catchRate")
    private var _catchRate: Int? = null,
    @SerializedName("experienceGroup")
    private var _experienceGroup: ExperienceGroup? = null,
    @SerializedName("baseExperienceYield")
    private var _baseExperienceYield: Int? = null,
    @SerializedName("primaryType")
    private var _primaryType: ElementalType? = null,
    @SerializedName("secondaryType")
    private var _secondaryType: ElementalType? = null,
    @SerializedName("shoulderMountable")
    private val _shoulderMountable: Boolean? = null,
    @SerializedName("shoulderEffects")
    private val _shoulderEffects: MutableList<ShoulderEffect>? = null,
    @SerializedName("moves")
    private val _moves: Learnset? = null,
    @SerializedName("evolutions")
    private val _evolutions: MutableSet<Evolution>? = null,
    @SerializedName("abilities")
    private val _abilities: AbilityPool? = null,
    @SerializedName("drops")
    private val _drops: DropTable? = null,
    @SerializedName("pokedex")
    private var _pokedex: MutableList<String>? = null,
    private val _preEvolution: PreEvolution? = null,
    private var standingEyeHeight: Float? = null,
    private var swimmingEyeHeight: Float? = null,
    private var flyingEyeHeight: Float? = null,
    @SerializedName("labels")
    private val _labels: Set<String>? = null,
    @SerializedName("cannotDynamax")
    private var _dynamaxBlocked: Boolean? = null,
    @SerializedName("eggGroups")
    private val _eggGroups: Set<EggGroup>? = null,
    @SerializedName("height")
    private var _height: Float? = null,
    @SerializedName("weight")
    private var _weight: Float? = null,
    /**
     * The [MoveTemplate] of the signature attack of the G-Max form.
     * This is always null on any form aside G-Max.
     */
    val gigantamaxMove: MoveTemplate? = null
) : Decodable, Encodable {
    @SerializedName("name")
    var name: String = name
        private set

    val baseStats: Map<Stat, Int>
        get() = _baseStats ?: species.baseStats

    val maleRatio: Float
        get() = _maleRatio ?: species.maleRatio
    val baseScale: Float
        get() = _baseScale ?: species.baseScale
    val hitbox: EntityDimensions
        get() = _hitbox ?: species.hitbox
    val catchRate: Int
        get() = _catchRate ?: species.catchRate
    val experienceGroup: ExperienceGroup
        get() = _experienceGroup ?: species.experienceGroup
    val baseExperienceYield: Int
        get() = _baseExperienceYield ?: species.baseExperienceYield
    val primaryType: ElementalType
        get() = _primaryType ?: species.primaryType

    val secondaryType: ElementalType?
        get() = _secondaryType ?: species.secondaryType

    val shoulderMountable: Boolean
        get() = _shoulderMountable ?: species.shoulderMountable

    val shoulderEffects: MutableList<ShoulderEffect>
        get() = _shoulderEffects ?: species.shoulderEffects

    val pokedex
        get() = _pokedex ?: species.pokedex
    val moves: Learnset
        get() = _moves ?: species.moves

    val types: Iterable<ElementalType>
        get() = secondaryType?.let { listOf(primaryType, it) } ?: listOf(primaryType)

    val abilities: AbilityPool
        get() = _abilities ?: species.abilities

    val drops: DropTable
        get() = _drops ?: species.drops

    var aspects = mutableListOf<String>()

    internal val preEvolution: PreEvolution?
        get() = _preEvolution ?: species.preEvolution

    val behaviour = FormPokemonBehaviour()

    val dynamaxBlocked: Boolean
        get() = _dynamaxBlocked ?: species.dynamaxBlocked

    val eggGroups: Set<EggGroup>
        get() = _eggGroups ?: species.eggGroups

    /**
     * The height in decimeters
     */
    val height: Float
        get() = _height ?: species.height

    /**
     * The weight in hectograms
     */
    val weight: Float
        get() = _weight ?: species.weight

    internal val labels: Set<String>
        get() = _labels ?: species.labels

    // Only exists for use of the field in Pokémon do not expose to end user due to how the species/form data is structured
    internal val evolutions: MutableSet<Evolution>
        get() = _evolutions ?: species.evolutions

    fun eyeHeight(entity: PokemonEntity): Float {
        val multiplier = this.resolveEyeHeight(entity) ?: return this.species.eyeHeight(entity)
        return entity.height * multiplier
    }

    private fun resolveEyeHeight(entity: PokemonEntity): Float? = when {
        entity.getPoseType() in PoseType.SWIMMING_POSES -> this.swimmingEyeHeight ?: this.standingEyeHeight
        entity.getPoseType() in PoseType.FLYING_POSES -> this.flyingEyeHeight ?: this.standingEyeHeight
        else -> this.standingEyeHeight
    }

    @Transient
    lateinit var species: Species

    fun initialize(species: Species): FormData {
        Stats.mainStats.forEach { stat ->
            this._baseStats?.putIfAbsent(stat, 1)
        }
        this.species = species
        this.behaviour.parent = species.behaviour
        return this
    }

    override fun equals(other: Any?): Boolean {
        return other is FormData
                && other.species.name.equals(this.species.name, true)
                && other.name.equals(this.name, true)
    }

    override fun hashCode(): Int {
        return this.species.name.hashCode() and this.name.hashCode()
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(this.name)
        buffer.writeNullable(this._baseStats) { pb1, map -> pb1.writeMap(map, { pb2, stat -> pb2.writeString(stat.id) }, { pb, value -> pb.writeSizedInt(IntSize.U_SHORT, value) }) }
        buffer.writeNullable(this._hitbox) { pb, hitbox ->
            pb.writeFloat(hitbox.width)
            pb.writeFloat(hitbox.height)
            pb.writeBoolean(hitbox.fixed)
        }
        buffer.writeNullable(this._primaryType) { pb, type -> pb.writeString(type.name) }
        buffer.writeNullable(this._secondaryType) { pb, type -> pb.writeString(type.name) }
        buffer.writeNullable(this._height) { pb, height -> pb.writeFloat(height) }
        buffer.writeNullable(this._weight) { pb, weight -> pb.writeFloat(weight) }
        buffer.writeNullable(this._pokedex) { pb1, pokedex -> pb1.writeCollection(pokedex)  { pb2, line -> pb2.writeString(line) } }
    }

    override fun decode(buffer: PacketByteBuf) {
        this.name = buffer.readString()
        this._baseStats = buffer.readNullable { pb -> pb.readMap({ Stats.getStat(it.readString(), true) }, { it.readSizedInt(IntSize.U_SHORT) }) }
        this._hitbox = buffer.readNullable { pb ->
            EntityDimensions(pb.readFloat(), pb.readFloat(), pb.readBoolean())
        }
        this._primaryType = buffer.readNullable { pb -> ElementalTypes.get(pb.readString()) }
        this._secondaryType = buffer.readNullable { pb -> ElementalTypes.get(pb.readString()) }
        this._height = buffer.readNullable { pb -> pb.readFloat() }
        this._weight = buffer.readNullable { pb -> pb.readFloat() }
        this._pokedex = buffer.readNullable { pb -> pb.readList { it.readString() } }
    }
}
