/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.evolution.adapters

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.*
import com.cobblemon.mod.common.pokemon.evolution.variants.BlockClickEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.LevelUpEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry

/**
 *
 */
object EvolutionRegistry {

    private val EVOLUTION_REGISTRY = SimpleRegistry<Variant<Evolution>>(RegistryKey.ofRegistry(cobblemonResource("evolution_variants")), Lifecycle.stable())
    private val REQUIREMENT_REGISTRY = SimpleRegistry<Variant<EvolutionRequirement>>(RegistryKey.ofRegistry(cobblemonResource("evolution_requirements")), Lifecycle.stable())

    /**
     * A [Codec] for [Evolution].
     * If you wish to register more variants see [registerEvolutionVariant].
     */
    @JvmField
    val EVOLUTION_CODEC: Codec<Evolution> = EVOLUTION_REGISTRY.codec.dispatch("variant", Evolution::variant) { variant -> variant.codec }

    /**
     * A [Codec] for [EvolutionRequirement].
     * If you wish to register more variants see [registerRequirementVariant].
     */
    @JvmField
    val REQUIREMENT_CODEC: Codec<EvolutionRequirement> = REQUIREMENT_REGISTRY.codec.dispatch("variant", EvolutionRequirement::variant) { variant -> variant.codec }

    /**
     * Registers an [Evolution] type.
     *
     * @param variant The [Variant] of an [Evolution] being registered.
     *
     * @throws IllegalStateException If the backing registry already contains an entry with the [Variant.identifier].
     * @throws AssertionError If the registry is missing an intrusive holder.
     */
    @JvmStatic
    fun registerEvolutionVariant(variant: Variant<Evolution>) {
        EVOLUTION_REGISTRY.add(RegistryKey.of(EVOLUTION_REGISTRY.key, variant.identifier), variant, Lifecycle.stable())
    }

    /**
     * Registers an [EvolutionRequirement] type.
     *
     * @param variant The [Variant] of an [EvolutionRequirement] being registered.
     *
     * @throws IllegalStateException If the backing registry already contains an entry with the [Variant.identifier].
     * @throws AssertionError If the registry is missing an intrusive holder.
     */
    @JvmStatic
    fun registerRequirementVariant(variant: Variant<EvolutionRequirement>) {
        REQUIREMENT_REGISTRY.add(RegistryKey.of(REQUIREMENT_REGISTRY.key, variant.identifier), variant, Lifecycle.stable())
    }

    internal fun init() {
        this.registerDefaultEvolutions()
        this.registerDefaultRequirements()
    }

    private fun registerDefaultEvolutions() {
        this.registerEvolutionVariant(BlockClickEvolution.VARIANT)
        this.registerEvolutionVariant(ItemInteractionEvolution.VARIANT)
        this.registerEvolutionVariant(LevelUpEvolution.MAIN_VARIANT)
        this.registerEvolutionVariant(LevelUpEvolution.ALTERNATIVE_VARIANT)
        this.registerEvolutionVariant(TradeEvolution.VARIANT)
    }

    private fun registerDefaultRequirements() {
        this.registerRequirementVariant(AllOfRequirement.VARIANT)
        this.registerRequirementVariant(AnyOfRequirement.VARIANT)
        this.registerRequirementVariant(AreaRequirement.VARIANT)
        this.registerRequirementVariant(AttackDefenceRatioRequirement.VARIANT)
        this.registerRequirementVariant(BattleCriticalHitsRequirement.VARIANT)
        this.registerRequirementVariant(BiomeRequirement.VARIANT)
        this.registerRequirementVariant(BlocksTraveledRequirement.VARIANT)
        this.registerRequirementVariant(DamageTakenRequirement.VARIANT)
        this.registerRequirementVariant(DefeatRequirement.VARIANT)
        this.registerRequirementVariant(FriendshipRequirement.VARIANT)
        this.registerRequirementVariant(HeldItemRequirement.VARIANT)
        this.registerRequirementVariant(LevelRequirement.VARIANT)
        this.registerRequirementVariant(MoonPhaseRequirement.VARIANT)
        this.registerRequirementVariant(MoveSetRequirement.VARIANT)
        this.registerRequirementVariant(MoveTypeRequirement.VARIANT)
        this.registerRequirementVariant(NoneOfRequirement.VARIANT)
        this.registerRequirementVariant(NotRequirement.VARIANT)
        this.registerRequirementVariant(PartyMemberRequirement.VARIANT)
        this.registerRequirementVariant(PokemonPropertiesRequirement.VARIANT)
        this.registerRequirementVariant(RecoilRequirement.VARIANT)
        this.registerRequirementVariant(TimeRangeRequirement.VARIANT)
        this.registerRequirementVariant(UseMoveRequirement.VARIANT)
        this.registerRequirementVariant(WeatherRequirement.VARIANT)
        this.registerRequirementVariant(WorldRequirement.VARIANT)
    }

}