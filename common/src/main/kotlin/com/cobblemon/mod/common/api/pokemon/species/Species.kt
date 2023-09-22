/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.species

import com.cobblemon.mod.common.api.abilities.Ability
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceGroup
import com.cobblemon.mod.common.api.pokemon.gender.GenderSelector
import com.cobblemon.mod.common.api.pokemon.stats.StatMap
import com.cobblemon.mod.common.api.registry.CobblemonRegistryElement
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.pokemon.gender.Gender
import com.cobblemon.mod.common.api.pokemon.moves.Learnset
import com.cobblemon.mod.common.pokemon.ai.PokemonBehaviour
import net.minecraft.entity.EntityDimensions
import net.minecraft.text.MutableText
import java.util.Optional

@Suppress("unused")
interface Species : CobblemonRegistryElement<Species>, ShowdownIdentifiable {

    fun nationalPokedexNumber(): Int

    fun types(): Pair<ElementalType, ElementalType?>

    fun abilities(): Set<Ability>

    fun baseStats(): StatMap

    fun catchRate(): Int

    /**
     * Responsible picking a [Gender] for this [Species].
     *
     * @return The backing [GenderSelector].
     */
    fun genderSelector(): GenderSelector

    /**
     * Picks a randomly generated [Gender] using the [genderSelector].
     *
     * @return The picked [Gender].
     */
    fun pickGender(): Gender = this.genderSelector().generate()

    /**
     * Checks if this [Species] can be of the given [gender].
     * This is checked against the [genderSelector].
     *
     * @param gender The [Gender] being checked.
     * @return If the given [gender] is possible for this [Species].
     */
    fun canBeOfGender(gender: Gender): Boolean = this.genderSelector().isValid(gender)

    fun shoulderMountable(): Boolean

    fun baseExperienceYield(): Int

    fun experienceGroup(): ExperienceGroup

    fun eggCycles(): Int

    fun eggGroups(): Set<EggGroup>

    // ToDo: Move drops to LootTables instead

    fun learnset(): Learnset

    fun behaviour(): PokemonBehaviour

    fun pokedexEntries(): Collection<MutableText>

    fun evolutions(): Set<Evolution>

    fun preEvolution(): Optional<PreEvolution>

    fun baseScale(): Float

    fun hitbox(): EntityDimensions

    fun baseFriendship(): Int

    fun evYield(): StatMap

    fun weight(): Float

    fun height(): Float

    fun aspects(): Set<String>

    fun formData(): Optional<SpeciesForm>

}