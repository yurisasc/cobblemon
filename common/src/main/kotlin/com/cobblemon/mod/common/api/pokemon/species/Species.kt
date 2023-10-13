/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.species

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.abilities.AbilityPool
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceGroup
import com.cobblemon.mod.common.api.pokemon.gender.Gender
import com.cobblemon.mod.common.api.pokemon.gender.GenderSelector
import com.cobblemon.mod.common.api.pokemon.moves.Learnset
import com.cobblemon.mod.common.api.pokemon.species.internal.*
import com.cobblemon.mod.common.api.pokemon.stats.StatMap
import com.cobblemon.mod.common.api.registry.CobblemonRegistryElement
import com.cobblemon.mod.common.api.registry.CobblemonRegistryKeys
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.ai.PokemonBehaviour
import com.cobblemon.mod.common.util.codec.ExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

@Suppress("unused")
class Species internal constructor(
    nationalPokedexNumber: Int,
    types: Pair<ElementalType, Optional<ElementalType>>,
    abilities: AbilityPool,
    baseStats: StatMap,
    catchRate: Int,
    genderSelector: GenderSelector,
    private val experienceData: ExperienceData,
    private val eggData: EggData,
    learnset: Learnset,
    private val behaviourData: BehaviourData,
    private val evolutionData: EvolutionData,
    baseFriendship: Int,
    evYield: StatMap,
    private val cosmeticData: CosmeticData,
    formData: Optional<FormData>
): CobblemonRegistryElement<Species>, ShowdownIdentifiable {

    companion object {

        private val TYPES_CODEC = ExtraCodecs.ENTITY_DIMENSIONS

        val codec = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.INT.fieldOf("nationalPokedexNumber").forGetter(Species::nationalPokedexNumber),
                ExtraCodecs.DUAL_TYPE_CODEC.fieldOf("types").forGetter(Species::types),
                // ToDo: abilities
                StatMap.BASE_STATS_CODEC.fieldOf("baseStats").forGetter(Species::baseStats),
                Codec.INT.fieldOf("catchRate").forGetter(Species::catchRate),
                GenderSelector.CODEC.fieldOf("genderSelector").forGetter(Species::genderSelector),
                ExperienceData.MAP_CODEC.forGetter(Species::experienceData),
                EggData.MAP_CODEC.fieldOf("eggData").forGetter(Species::eggData),
                // ToDo: learnset
                BehaviourData.MAP_CODEC.forGetter(Species::behaviourData),
                EvolutionData.MAP_CODEC.forGetter(Species::evolutionData),
                Codec.INT.fieldOf("baseFriendship").forGetter(Species::baseFriendship),
                StatMap.BASE_STATS_CODEC.fieldOf("evYield").forGetter(Species::evYield),
                CosmeticData.MAP_CODEC.forGetter(Species::cosmeticData),
                FormData.CODEC.optionalFieldOf("formData").forGetter(Species::formData)
            ).apply(builder, ::Species)
        }

    }

    val displayName: MutableText by lazy { Text.translatable("${this.id().namespace}.species.${this.id().path.replace("/", ".")}.name") }

    var nationalPokedexNumber: Int = nationalPokedexNumber
        private set

    var types: Pair<ElementalType, Optional<ElementalType>> = types
        private set

    val primaryType: ElementalType get() = this.types.first

    val secondaryType: Optional<ElementalType> get() = this.types.second

    var abilities: AbilityPool = abilities
        private set

    var baseStats: StatMap = baseStats
        private set

    var catchRate: Int = catchRate
        private set

    /**
     * Responsible for picking a [Gender] for this [Species] on [create].
     */
    var genderSelector: GenderSelector = genderSelector
        private set

    val shoulderMountable: Boolean get() = this.behaviourData.shoulderMountable

    val baseExperienceYield: Int get() = this.experienceData.baseExperienceYield

    val experienceGroup: ExperienceGroup get() = this.experienceData.experienceGroup

    val eggCycles: Int get() = this.eggData.eggCycles

    val eggGroups: Set<EggGroup> get() = this.eggData.eggGroups

    var learnset: Learnset = learnset
        private set

    val behaviour: PokemonBehaviour get() = this.behaviourData.pokemonBehaviour

    val evolutions: Set<Evolution> get() = this.evolutionData.evolutions

    val preEvolution: Optional<PreEvolution> get() = this.evolutionData.preEvolution

    val baseScale: Float get() = this.cosmeticData.baseScale

    val hitbox: EntityDimensions get() = this.cosmeticData.hitbox

    var baseFriendship: Int = baseFriendship
        private set

    var evYield: StatMap = evYield
        private set

    val weight: Float get() = this.cosmeticData.weight

    val height: Float get() = this.cosmeticData.height

    val aspects: Set<String> get() = this.cosmeticData.aspects

    val features: Set<String> get() = this.behaviourData.features

    val standingEyeHeight: Float get() = this.cosmeticData.standingEyeHeight

    val swimmingEyeHeight: Float get() = this.cosmeticData.swimmingEyeHeight

    val flyingEyeHeight: Float get() = this.cosmeticData.flyingEyeHeight

    var formData: Optional<FormData> = formData
        private set

    /**
     * TODO
     *
     * @param level
     * @return
     */
    fun create(level: Int): Pokemon {
        TODO("Not yet implemented")
    }

    /**
     * Picks a randomly generated [Gender] using the [genderSelector].
     *
     * @return The picked [Gender].
     */
    fun pickGender(): Gender = this.genderSelector.generate()

    /**
     * Checks if this [Species] can be of the given [gender].
     * This is checked against the [genderSelector].
     *
     * @param gender The [Gender] being checked.
     * @return If the given [gender] is possible for this [Species].
     */
    fun canBeOfGender(gender: Gender): Boolean = this.genderSelector.isValid(gender)

    override fun showdownId(): String {
        val id = this.id()
        val showdownId = ShowdownIdentifiable.REGEX.replace(id.path, "")
        if (id.namespace == Cobblemon.MODID) {
            return showdownId
        }
        return id.namespace + showdownId
    }

    override fun id(): Identifier = this.registry().getId(this)!!

    override fun registryEntry(): RegistryEntry<Species> = this.registry().getEntry(this)

    override fun registry(): Registry<Species> = Cobblemon.implementation.getRegistry(CobblemonRegistryKeys.SPECIES)

}