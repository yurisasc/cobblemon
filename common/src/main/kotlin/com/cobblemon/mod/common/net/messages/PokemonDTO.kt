/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages

import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.moves.BenchedMoves
import com.cobblemon.mod.common.api.moves.MoveSet
import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures
import com.cobblemon.mod.common.api.pokemon.feature.SynchronizedSpeciesFeature
import com.cobblemon.mod.common.api.pokemon.feature.SynchronizedSpeciesFeatureProvider
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.types.tera.TeraTypes
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.pokemon.*
import com.cobblemon.mod.common.pokemon.activestate.PokemonState
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer
import com.cobblemon.mod.common.util.*
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.*

/**
 * A data transfer object for an entire [Pokemon], complete with all of the information a player is allowed
 * to know about their own Pokémon. The main purpose of this is to have a write- and read-safe object that
 * doesn't need reference data to be loaded prior to being mapped back into a [Pokemon] instance.
 *
 * @author Hiroku
 * @since November 10th, 2022
 */
class PokemonDTO : Encodable, Decodable {
    var toClient = false
    var uuid = UUID.randomUUID()
    lateinit var species: ResourceLocation
    var nickname: MutableComponent? = null
    var form = ""
    var level = 1
    var experience = 1
    var friendship = 0
    var currentHealth = 0
    var gender = Gender.MALE
    var ivs = IVs()
    var evs = EVs()
    var moveSet = MoveSet()
    var scaleModifier = 0F
    var ability = ""
    var shiny = false
    var status: ResourceLocation? = null
    lateinit var state: PokemonState
    lateinit var caughtBall: ResourceLocation
    var benchedMoves = BenchedMoves()
    var aspects = setOf<String>()
    lateinit var evolutionData: Tag
    lateinit var nature: ResourceLocation
    var mintNature: ResourceLocation? = null
    var heldItem: ItemStack = ItemStack.EMPTY
    var tetheringId: UUID? = null
    var teraType = ""
    var dmaxLevel = 0
    var gmaxFactor = false
    var tradeable = true
    //    var features: List<SynchronizedSpeciesFeature> = emptyList()
    val features = Object2ObjectOpenHashMap<String, CompoundTag>()
    var originalTrainerType: OriginalTrainerType = OriginalTrainerType.NONE
    var originalTrainer: String? = null
    var originalTrainerName: String? = null

    constructor()
    constructor(pokemon: Pokemon, toClient: Boolean) {
        this.toClient = toClient
        this.uuid = pokemon.uuid
        this.species = pokemon.species.resourceIdentifier
        this.nickname = pokemon.nickname
        this.form = pokemon.form.name
        this.level = pokemon.level
        this.experience = pokemon.experience
        this.friendship = pokemon.friendship
        this.currentHealth = pokemon.currentHealth
        this.gender = pokemon.gender
        this.ivs = pokemon.ivs
        this.evs = pokemon.evs
        this.moveSet = pokemon.moveSet
        this.scaleModifier = pokemon.scaleModifier
        this.ability = pokemon.ability.name
        this.shiny = pokemon.shiny
        this.status = pokemon.status?.status?.name
        this.state = pokemon.state
        this.caughtBall = pokemon.caughtBall.name
        this.benchedMoves = pokemon.benchedMoves
        this.aspects = pokemon.aspects
        this.evolutionData = pokemon.evolutionProxy.saveToNBT()
        this.nature = pokemon.nature.name
        this.mintNature = pokemon.mintedNature?.name
        this.heldItem = pokemon.heldItemNoCopy()
        this.tetheringId = pokemon.tetheringId
        this.teraType = pokemon.teraType.id.toString()
        this.dmaxLevel = pokemon.dmaxLevel
        this.gmaxFactor = pokemon.gmaxFactor
        this.tradeable = pokemon.tradeable

        this.features.clear()
        pokemon.features
            .filterIsInstance<SynchronizedSpeciesFeature>()
            .filter { (SpeciesFeatures.getFeature(it.name) as? SynchronizedSpeciesFeatureProvider<*>)?.visible == true }
            .forEach { feature -> this.features[feature.name] = feature.saveToNBT(CompoundTag()) }

        this.originalTrainerType = pokemon.originalTrainerType
        this.originalTrainer = pokemon.originalTrainer
        this.originalTrainerName = pokemon.originalTrainerName
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeBoolean(toClient)
        buffer.writeUUID(uuid)
        buffer.writeIdentifier(species)
        ComponentSerialization.OPTIONAL_STREAM_CODEC.encode(buffer, Optional.ofNullable(nickname))
        buffer.writeString(form)
        buffer.writeInt(experience)
        buffer.writeSizedInt(IntSize.U_SHORT, level)
        buffer.writeShort(friendship)
        buffer.writeShort(currentHealth)
        buffer.writeSizedInt(IntSize.U_BYTE, gender.ordinal)
        ivs.saveToBuffer(buffer)
        evs.saveToBuffer(buffer)
        moveSet.saveToBuffer(buffer)
        buffer.writeFloat(scaleModifier)
        buffer.writeString(ability)
        buffer.writeBoolean(shiny)
        state.writeToBuffer(buffer)
        buffer.writeNullable(status) { _, v -> buffer.writeIdentifier(v) }
        buffer.writeIdentifier(caughtBall)
        benchedMoves.saveToBuffer(buffer)
        buffer.writeSizedInt(IntSize.U_BYTE, aspects.size)
        aspects.forEach { buffer.writeString(it) }
        buffer.writeNbt(evolutionData)
        buffer.writeIdentifier(nature)
        buffer.writeNullable(mintNature) { _, v -> buffer.writeIdentifier(v) }
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, heldItem)
        buffer.writeNullable(tetheringId) { _, v -> buffer.writeUUID(v) }
        buffer.writeString(teraType)
        buffer.writeInt(dmaxLevel)
        buffer.writeBoolean(gmaxFactor)
        buffer.writeBoolean(tradeable)
        buffer.writeSizedInt(IntSize.U_SHORT, features.size)
        features.forEach { (id, feature) ->
            buffer.writeString(id)
            buffer.writeNbt(feature)
        }
        buffer.writeString(originalTrainerType.name)
        buffer.writeNullable(originalTrainer) { _, v -> buffer.writeString(v) }
        buffer.writeNullable(originalTrainerName) { _, v -> buffer.writeString(v) }
    }

    override fun decode(buffer: RegistryFriendlyByteBuf) {
        toClient = buffer.readBoolean()
        uuid = buffer.readUUID()
        species = buffer.readIdentifier()
        nickname = ComponentSerialization.OPTIONAL_STREAM_CODEC.decode(buffer).map { it::copy as MutableComponent }.orElse(null)
        form = buffer.readString()
        experience = buffer.readInt()
        level = buffer.readSizedInt(IntSize.U_SHORT)
        friendship = buffer.readUnsignedShort()
        currentHealth = buffer.readUnsignedShort()
        gender = Gender.values()[buffer.readSizedInt(IntSize.U_BYTE)]
        ivs.loadFromBuffer(buffer)
        evs.loadFromBuffer(buffer)
        moveSet.loadFromBuffer(buffer)
        scaleModifier = buffer.readFloat()
        ability = buffer.readString()
        shiny = buffer.readBoolean()
        state = PokemonState.fromBuffer(buffer)
        status = buffer.readNullable { buffer.readIdentifier() }
        caughtBall = buffer.readIdentifier()
        benchedMoves.loadFromBuffer(buffer)
        val aspects = mutableSetOf<String>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            aspects.add(buffer.readString())
        }
        this.aspects = aspects
        evolutionData = requireNotNull(buffer.readNbt(NbtAccounter.create(4096))) { "Failed to read evolution data NBT" }
        nature = buffer.readIdentifier()
        mintNature = buffer.readNullable { buffer.readIdentifier() }
        heldItem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)
        tetheringId = buffer.readNullable { buffer.readUUID() }
        teraType = buffer.readString()
        dmaxLevel = buffer.readInt()
        gmaxFactor = buffer.readBoolean()
        tradeable = buffer.readBoolean()

        this.features.clear()
        val featureCount = buffer.readSizedInt(IntSize.U_SHORT)
        for (i in 0 until featureCount) {
            val featureName = buffer.readString()
            val featureData = requireNotNull(buffer.readNbt()) { "Failed to read feature data NBT for feature $featureName" }
            this.features[featureName] = featureData
        }

        originalTrainerType = OriginalTrainerType.valueOf(buffer.readString())
        originalTrainer = buffer.readNullable { buffer.readString() }
        originalTrainerName = buffer.readNullable { buffer.readString() }
    }

    fun create(): Pokemon {
        val species = requireNotNull(PokemonSpecies.getByIdentifier(this.species))
            { "PokemonDTO transmitted unknown Pokemon: ${this.species}" }

        return Pokemon().also {
            it.isClient = toClient
            it.uuid = uuid
            it.species = species
            it.nickname = nickname
            it.form = it.species.forms.find { it.name == form } ?: it.species.standardForm
            it.experience = experience
            it.level = level
            it.setFriendship(friendship)
            it.gender = gender
            ivs.forEach { stat ->
                it.setIV(stat.key, stat.value)
            }
            evs.forEach { stat ->
                it.setEV(stat.key, stat.value)
            }
            it.currentHealth = currentHealth
            it.moveSet.clear()
            for (move in moveSet) {
                it.moveSet.add(move)
            }
            it.scaleModifier = scaleModifier
            it.ability = Abilities.getOrException(ability).create()
            it.shiny = shiny
            it.state = state
            it.status = status?.let { id ->
                val statusType = Statuses.getStatus(id)
                if (statusType is PersistentStatus) {
                    PersistentStatusContainer(statusType, 0)
                } else {
                    null
                }
            }
            it.caughtBall = PokeBalls.getPokeBall(caughtBall)!!
            it.benchedMoves.addAll(benchedMoves)
            it.forcedAspects = aspects
            it.evolutionProxy.loadFromNBT(evolutionData)
            it.nature = Natures.getNature(nature)!!
            it.mintedNature = mintNature?.let { id -> Natures.getNature(id)!! }
            it.swapHeldItem(heldItem, false)
            it.tetheringId = tetheringId
            it.teraType = TeraTypes.get(this.teraType.asIdentifierDefaultingNamespace())!!
            it.dmaxLevel = dmaxLevel
            it.gmaxFactor = gmaxFactor
            it.tradeable = tradeable

            if (features.isNotEmpty()) {
                val supportedFeatures = SpeciesFeatures
                    .getFeaturesFor(species)
                    .filterIsInstance<SynchronizedSpeciesFeatureProvider<*>>()

                features.forEach { featureName, featureData ->
                    val feature = supportedFeatures.firstNotNullOfOrNull { it(featureData) }
                        ?: error("Couldn't find a feature provider to deserialize the feature named $featureName. Something's wrong.")
                    it.features.removeIf { it.name == feature.name }
                    it.features.add(feature)
                }
            }

            when (originalTrainerType)
            {
                OriginalTrainerType.NONE -> {
                    it.removeOriginalTrainer()
                }
                OriginalTrainerType.PLAYER -> {
                    originalTrainer?.let { ot ->
                        UUID.fromString(ot)?.let { uuid ->
                            it.setOriginalTrainer(uuid)
                        }
                    }
                }
                OriginalTrainerType.NPC ->
                {
                    originalTrainer?.let { ot ->
                        it.setOriginalTrainer(ot)
                    }
                }
            }
            it.originalTrainerName = originalTrainerName
        }
    }
}