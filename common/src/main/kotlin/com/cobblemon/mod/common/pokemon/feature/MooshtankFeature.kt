/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.feature

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonObject
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemUsage
import net.minecraft.item.Items
import net.minecraft.item.SuspiciousStewItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

class MooshtankFlowerEffect(
    val items: Set<Identifier>,
    val effect: StatusEffect,
    val duration: Int
)

object MooshtankFeatureProvider : SpeciesFeatureProvider<MooshtankFeature>, AspectProvider, CustomPokemonPropertyType<MooshtankFeature> {
    override fun invoke(pokemon: Pokemon): MooshtankFeature? {
        return pokemon.getFeature(DataKeys.MOOSHTANK) // It's not a mooshtank unless it's been explicitly branded as one already
    }

    override fun invoke(nbt: NbtCompound): MooshtankFeature? {
        if (nbt.contains(DataKeys.MOOSHTANK)) {
            return MooshtankFeature().also { it.loadFromNBT(nbt.getCompound(DataKeys.MOOSHTANK)) }
        } else {
            return null
        }
    }

    override fun invoke(json: JsonObject): MooshtankFeature? {
        if (json.has(DataKeys.MOOSHTANK)) {
            return MooshtankFeature().also { it.loadFromJSON(json.getAsJsonObject(DataKeys.MOOSHTANK)) }
        } else {
            return null
        }
    }

    override fun provide(pokemon: Pokemon): Set<String> {
        val feature = pokemon.getFeature<MooshtankFeature>(DataKeys.MOOSHTANK)
        return if (feature != null) {
            setOf("${feature.name}-" + if (feature.isBrown) "brown" else "red")
        } else {
            emptySet()
        }
    }

    override fun provide(properties: PokemonProperties): Set<String> {
        val feature = properties.customProperties.filterIsInstance<MooshtankFeature>().firstOrNull()
        return if (feature != null) {
            setOf("${feature.name}-" + if (feature.isBrown) "brown" else "red")
        } else {
            emptySet()
        }
    }

    override val keys = setOf(DataKeys.MOOSHTANK)
    override val needsKey = true

    override fun fromString(value: String?): MooshtankFeature? {
        val isBrown = when (value?.lowercase()) {
            null -> false
            "red" -> false
            "brown" -> true
            else -> return null
        }
        return MooshtankFeature().also { it.isBrown = isBrown }
    }

    override fun examples() = setOf("red", "brown")
}

class MooshtankFeature : SpeciesFeature, CustomPokemonProperty {
    companion object {
        val effectedFlowers = mutableListOf(
            MooshtankFlowerEffect(
                setOf(Identifier("allium")),
                StatusEffects.FIRE_RESISTANCE,
                80 // 4 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("azure_bluet")),
                StatusEffects.BLINDNESS,
                160 // 8 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("blue_orchid"), Identifier("dandelion")),
                StatusEffects.SATURATION,
                7 // .35 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("cornflower")),
                StatusEffects.JUMP_BOOST,
                120 // 6 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("lily_of_the_valley")),
                StatusEffects.POISON,
                240 // 12 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("oxeye_daisy")),
                StatusEffects.REGENERATION,
                160 // 8 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("poppy"), Identifier("torchflower")),
                StatusEffects.NIGHT_VISION,
                100 // 5 seconds
            ),
            MooshtankFlowerEffect(
                setOf(
                    Identifier("pink_tulip"),
                    Identifier("red_tulip"),
                    Identifier("white_tulip"),
                    Identifier("orange_tulip")
                ),
                StatusEffects.WEAKNESS,
                180 // 9 seconds
            ),
            MooshtankFlowerEffect(
                setOf(Identifier("wither_rose")),
                StatusEffects.WITHER,
                160 // 8 seconds
            ),
            MooshtankFlowerEffect(
                setOf(cobblemonResource("pep_up_flower")),
                StatusEffects.LEVITATION,
                160 // 8 seconds
            )
        )
    }

    override val name = DataKeys.MOOSHTANK
    var isBrown = false
    var lastFlowerFed: Identifier? = null

    fun interact(pokemonEntity: PokemonEntity, player: PlayerEntity, hand: Hand): ActionResult? {
        val itemStack = player.getStackInHand(hand)
        val itemId = pokemonEntity.world.registryManager.get(RegistryKeys.ITEM).getId(itemStack.item) ?: return null // not likely
        if (itemStack.isOf(Items.BOWL)) {
            player.playSound(SoundEvents.ENTITY_MOOSHROOM_MILK, 1.0f, 1.0f)
            // if the Mooshtank ate a Flower beforehand
            val lastFlowerFed = this.lastFlowerFed
            if (lastFlowerFed != null && isBrown) {
                val flowerEffect = effectedFlowers.find { lastFlowerFed in it.items }
                val effect = flowerEffect?.effect
                val duration = flowerEffect?.duration ?: 0

                // modify the suspicious stew with the effect
                val susStewStack = Items.SUSPICIOUS_STEW.defaultStack
                SuspiciousStewItem.addEffectToStew(susStewStack, effect, duration)
                val susStewEffect = ItemUsage.exchangeStack(itemStack, player, susStewStack)
                //give player modified Suspicious Stew
                player.setStackInHand(hand, susStewEffect)
                // reset the flower fed state
                this.lastFlowerFed = null
                pokemonEntity.pokemon.markFeatureDirty(this)
                return ActionResult.success(pokemonEntity.world.isClient)
            } else {
                val mushroomStew = ItemUsage.exchangeStack(itemStack, player, Items.MUSHROOM_STEW.defaultStack)
                player.setStackInHand(hand, mushroomStew)
                return ActionResult.success(pokemonEntity.world.isClient)
            }
        } else if (this.isBrown && effectedFlowers.any { itemId in it.items }) {
            player.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 1.0f, 1.0f)
            lastFlowerFed = itemId
            pokemonEntity.pokemon.markFeatureDirty(this)
            return ActionResult.success(pokemonEntity.world.isClient)
        }
        return null
    }

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        val nbt = NbtCompound()
        nbt.putBoolean(DataKeys.MOOSHTANK_BROWN, isBrown)
        val lastFlowerFed = this.lastFlowerFed
        if (lastFlowerFed != null) {
            nbt.putString(DataKeys.MOOSHTANK_LAST_FLOWER_FED, lastFlowerFed.toString())
        }
        pokemonNBT.put(DataKeys.MOOSHTANK, nbt)
        return pokemonNBT
    }

    override fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature {
        val nbt = pokemonNBT.getCompound(DataKeys.MOOSHTANK)
        isBrown = nbt.getBoolean(DataKeys.MOOSHTANK_BROWN)
        lastFlowerFed = nbt.getString(DataKeys.MOOSHTANK_LAST_FLOWER_FED).takeIf { it.isNotBlank() }?.let(::Identifier)
        return this
    }

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        val json = JsonObject()
        json.addProperty(DataKeys.MOOSHTANK_BROWN, isBrown)
        val lastFlowerFed = this.lastFlowerFed
        if (lastFlowerFed != null) {
            json.addProperty(DataKeys.MOOSHTANK_LAST_FLOWER_FED, lastFlowerFed.toString())
        }
        pokemonJSON.add(DataKeys.MOOSHTANK, json)
        return pokemonJSON
    }

    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature {
        val json = pokemonJSON.getAsJsonObject(DataKeys.MOOSHTANK)
        isBrown = json.get(DataKeys.MOOSHTANK_BROWN).asBoolean
        lastFlowerFed = json.get(DataKeys.MOOSHTANK_LAST_FLOWER_FED)?.asString?.let(::Identifier)
        return this
    }

    override fun asString() = "mooshtank=${if (isBrown) "brown" else "red"}"

    override fun apply(pokemon: Pokemon) {
        val featureProvider = SpeciesFeatures.getFeature(name) ?: return
        if (featureProvider in SpeciesFeatures.getFeaturesFor(pokemon.species)) {
            val existingFeature = pokemon.getFeature<MooshtankFeature>(name)
            if (existingFeature != null) {
                existingFeature.isBrown = isBrown
                pokemon.markFeatureDirty(existingFeature)
            } else {
                val feature = MooshtankFeature().also { it.isBrown = isBrown }
                pokemon.features.add(feature)
                pokemon.markFeatureDirty(feature)
            }
            pokemon.updateAspects()
        }
    }

    override fun matches(pokemon: Pokemon): Boolean {
        val feature = pokemon.getFeature<MooshtankFeature>(DataKeys.MOOSHTANK)
        return feature != null && feature.isBrown == isBrown
    }
}