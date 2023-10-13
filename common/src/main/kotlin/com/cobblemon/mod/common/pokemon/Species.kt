/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.abilities.AbilityPool
import com.cobblemon.mod.common.api.data.ClientDataSynchronizer
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.drop.DropTable
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.effect.ShoulderEffect
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceGroups
import com.cobblemon.mod.common.api.pokemon.moves.Learnset
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.entity.PoseType.Companion.FLYING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.SWIMMING_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.pokemon.ai.PokemonBehaviour
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import net.minecraft.entity.EntityDimensions
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

class Species : ClientDataSynchronizer<Species>, ShowdownIdentifiable {
    var name: String = "Bulbasaur"
    val translatedName: MutableText
        get() = Text.translatable("${this.resourceIdentifier.namespace}.species.${this.unformattedShowdownId()}.name")
    var nationalPokedexNumber = 1

    var baseStats = hashMapOf<Stat, Int>()
        private set
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    var maleRatio: Float = 0.5F
        private set
    var catchRate = 45
        private set
    // Only modifiable for debugging sizes
    var baseScale = 1F
    var baseExperienceYield = 10
    var baseFriendship = 0
    var evYield = hashMapOf<Stat, Int>()
        private set
    var experienceGroup = ExperienceGroups.first()
    var hitbox = EntityDimensions(1F, 1F, false)
    var primaryType: ElementalType = CobblemonRegistries.ELEMENTAL_TYPE.getRandom(Random.create()).get().value()
        internal set
    var secondaryType: ElementalType? = null
        internal set
    var abilities = AbilityPool()
        private set
    var shoulderMountable: Boolean = false
        private set
    var shoulderEffects = mutableListOf<ShoulderEffect>()
        private set
    var moves = Learnset()
        private set
    var features = mutableSetOf<String>()
        private set
    private var standingEyeHeight: Float? = null
    private var swimmingEyeHeight: Float? = null
    private var flyingEyeHeight: Float? = null
    var behaviour = PokemonBehaviour()
        private set
    var pokedex = mutableListOf<String>()
        private set
    var drops = DropTable()
        private set
    var eggCycles = 120
        private set
    var eggGroups = hashSetOf<EggGroup>()
        private set
    var dynamaxBlocked = false
    var implemented = false

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
        private set

    val standardForm by lazy { FormData(_evolutions = this.evolutions).initialize(this) }

    var labels = hashSetOf<String>()
        private set

    /**
     * Contains the evolutions of this species.
     * If you're trying to find out the possible evolutions of a Pokémon you should always work with their [FormData].
     * The base species is the [standardForm].
     * Do not access this property immediately after a species is loaded, it requires all species in the game to be loaded.
     * To be aware of this gamestage subscribe to [PokemonSpecies.observable].
     */
    var evolutions: MutableSet<Evolution> = hashSetOf()
        private set

    var preEvolution: PreEvolution? = null
        private set

    @Transient
    lateinit var resourceIdentifier: Identifier

    val types: Iterable<ElementalType>
        get() = secondaryType?.let { listOf(primaryType, it) } ?: listOf(primaryType)

    fun initialize() {
        Cobblemon.statProvider.provide(this)
        this.forms.forEach { it.initialize(this) }
        if (this.forms.isNotEmpty() && this.forms.none { it == this.standardForm }) {
            this.forms.add(0, this.standardForm)
        }
        // These properties are lazy, these need all species to be reloaded but SpeciesAdditions is what will eventually trigger this after all species have been loaded
        this.preEvolution?.species
        this.preEvolution?.form
        this.evolutions.size
    }

    // Ran after initialize due to us creating a Pokémon here which requires all the properties in #initialize to be present for both this and the results, this is the easiest way to quickly resolve species + form
    internal fun resolveEvolutionMoves() {
        this.evolutions.forEach { evolution ->
            if (evolution.learnableMoves.isNotEmpty() && evolution.result.species != null) {
                val pokemon = evolution.result.create()
                pokemon.form.moves.evolutionMoves += evolution.learnableMoves
            }
        }
        this.forms.forEach(FormData::resolveEvolutionMoves)
    }

    fun create(level: Int = 10) = PokemonProperties.parse("species=\"${this.name}\" level=${level}").create()

    fun getForm(aspects: Set<String>) = forms.lastOrNull { it.aspects.all { it in aspects } } ?: standardForm

    fun eyeHeight(entity: PokemonEntity): Float {
        val multiplier = this.resolveEyeHeight(entity) ?: VANILLA_DEFAULT_EYE_HEIGHT
        return entity.height * multiplier
    }

    private fun resolveEyeHeight(entity: PokemonEntity): Float? = when {
        entity.getPoseType() in SWIMMING_POSES -> this.swimmingEyeHeight ?: standingEyeHeight
        entity.getPoseType() in FLYING_POSES -> this.flyingEyeHeight ?: standingEyeHeight
        else -> this.standingEyeHeight
    }

    fun canGmax() = this.forms.find { it.formOnlyShowdownId() == "gmax" } != null

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(this.implemented)
        buffer.writeString(this.name)
        buffer.writeInt(this.nationalPokedexNumber)
        buffer.writeMap(this.baseStats,
            { keyBuffer, stat -> Cobblemon.statProvider.encode(keyBuffer, stat)},
            { valueBuffer, value -> valueBuffer.writeSizedInt(IntSize.U_SHORT, value) }
        )
        buffer.writeRegistryValue(CobblemonRegistries.ELEMENTAL_TYPE, this.primaryType)
        buffer.writeNullable(this.secondaryType) { pb, type -> pb.writeRegistryValue(CobblemonRegistries.ELEMENTAL_TYPE, type) }
        buffer.writeString(this.experienceGroup.name)
        buffer.writeFloat(this.height)
        buffer.writeFloat(this.weight)
        buffer.writeFloat(this.baseScale)
        // Hitbox start
        buffer.writeFloat(this.hitbox.width)
        buffer.writeFloat(this.hitbox.height)
        buffer.writeBoolean(this.hitbox.fixed)
        // Hitbox end
        this.moves.encode(buffer)
        buffer.writeCollection(this.pokedex) { pb, line -> pb.writeString(line) }
        buffer.writeCollection(this.forms) { pb, form -> form.encode(pb) }
    }

    override fun decode(buffer: PacketByteBuf) {
        this.implemented = buffer.readBoolean()
        this.name = buffer.readString()
        this.nationalPokedexNumber = buffer.readInt()
        this.baseStats.putAll(buffer.readMap(
            { keyBuffer -> Cobblemon.statProvider.decode(keyBuffer) },
            { valueBuffer -> valueBuffer.readSizedInt(IntSize.U_SHORT) })
        )
        this.primaryType = buffer.readRegistryValue(CobblemonRegistries.ELEMENTAL_TYPE)!!
        this.secondaryType = buffer.readNullable { pb -> pb.readRegistryValue(CobblemonRegistries.ELEMENTAL_TYPE)!! }
        this.experienceGroup = ExperienceGroups.findByName(buffer.readString())!!
        this.height = buffer.readFloat()
        this.weight = buffer.readFloat()
        this.baseScale = buffer.readFloat()
        this.hitbox = EntityDimensions(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean())
        this.moves.decode(buffer)
        this.pokedex.clear()
        this.pokedex += buffer.readList { pb -> pb.readString() }
        this.forms.clear()
        this.forms += buffer.readList{ pb -> FormData().apply { decode(pb) } }.filterNotNull()
        this.initialize()
    }

    override fun shouldSynchronize(other: Species): Boolean {
        if (other.resourceIdentifier.toString() != other.resourceIdentifier.toString())
            return false
        return other.showdownId() != this.showdownId()
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
                // We only sync level up moves atm
                || this.moves.shouldSynchronize(other.moves)
    }

    /**
     * The literal Showdown ID of this species.
     * This will be a lowercase version of the [name] with all the non-alphanumeric characters removed. For example, "Mr. Mime" becomes "mrmime".
     * If a Species is not a part of the Cobblemon mon the [resourceIdentifier] will have the namespace appended at the start of the ID resulting in something such as 'somemodaspecies'
     *
     * @return The literal Showdown ID of this species.
     */
    override fun showdownId(): String {
        val id = this.unformattedShowdownId()
        if (this.resourceIdentifier.namespace == Cobblemon.MODID) {
            return id
        }
        return this.resourceIdentifier.namespace + id
    }

    /**
     * The unformatted literal Showdown ID of this species.
     * This will be a lowercase version of the [name] with all the non-alphanumeric characters removed. For example, "Mr. Mime" becomes "mrmime".
     * Unlike [showdownId] the [resourceIdentifier] namespace will not be appended at the start regardless if the species is from Cobblemon or a 3rd party addon.
     * The primary purpose of this is for lang keys.
     *
     * @return The unformatted literal Showdown ID of this species.
     */
    private fun unformattedShowdownId(): String = ShowdownIdentifiable.REGEX.replace(this.name.lowercase(), "")

    override fun toString() = this.showdownId()

    companion object {
        private const val VANILLA_DEFAULT_EYE_HEIGHT = .85F
    }
}