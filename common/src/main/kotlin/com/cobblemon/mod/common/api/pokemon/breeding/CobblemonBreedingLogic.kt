/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.breeding

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.abilities.Ability
import com.cobblemon.mod.common.api.moves.BenchedMove
import com.cobblemon.mod.common.api.moves.MoveSet
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup
import com.cobblemon.mod.common.pokemon.*
import java.util.Random

class CobblemonBreedingLogic {

    /**
     * Takes two [Pokemon] and returns the result of the two breeding (if compatible) as a [Pokemon]
     *
     * @param parent1 The first parent [Pokemon]
     * @param parent2 The second parent [Pokemon]
     * @return The child of the two Pokémon
     */
    fun breed(parent1: Pokemon, parent2: Pokemon): Pokemon? {
        if (!canBreed(parent1, parent2)) {
            println("Parents failed breeding check")
            return null
        }

        // parentF will always be either the female Pokémon or the non-ditto Pokémon
        val parentF: Pokemon
        val parentM: Pokemon

        if (parent1.gender == Gender.FEMALE || parent2.species.eggGroups.contains(EggGroup.DITTO)) { parentF = parent1; parentM = parent2 }
        else if (parent2.gender == Gender.FEMALE || parent1.species.eggGroups.contains(EggGroup.DITTO)) { parentF = parent2; parentM = parent2 }
        else { println("This should not happen I hope"); return null }

        val child = Pokemon()

        child.species = determineSpecies(parentF)
        child.nature = determineNature(parentF, parentM)
        child.ability = determineAbility(parentF)
        // TODO: IVs

        var i = 0
        for (move in determineMoveSet(parentF, parentM, child.species)) {
            if (i <= 3) {
                child.moveSet.add(move.create())
            } else {
                child.benchedMoves.add(BenchedMove(move, 0))
            }
            i += 1
        }

        return child
    }

    /**
     * Determines whether two [Pokemon] are compatible for breeding
     *
     * @param parent1 The first parent [Pokemon]
     * @param parent2 The second parent [Pokemon]
     * @return If the Pokémon can breed
     */
    fun canBreed(parent1: Pokemon, parent2: Pokemon): Boolean {
        // Is either parent a part of the No Eggs Discovered egg group?
        if (parent1.species.eggGroups.contains(EggGroup.UNDISCOVERED) || parent2.species.eggGroups.contains(EggGroup.UNDISCOVERED)) {
            return false
        }

        // Is either parent part of the Ditto egg group?
        if (parent1.species.eggGroups.contains(EggGroup.DITTO) || parent2.species.eggGroups.contains(EggGroup.DITTO)) {
            // Are both parents part of the Ditto egg group?
            return !(parent1.species.eggGroups.contains(EggGroup.DITTO) && parent2.species.eggGroups.contains(EggGroup.DITTO))
        }

        // Are both parents the opposite gender?
        if ((parent1.gender == Gender.MALE && parent2.gender == Gender.FEMALE) || (parent1.gender == Gender.FEMALE && parent2.gender == Gender.MALE)) {
            // Do both parents share an egg group?
            for (group in parent1.species.eggGroups) {
                if (parent2.species.eggGroups.contains(group)) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }

    /**
     * Returns a [Species] for a child [Pokemon]
     *
     * @param parentF The female parent [Pokemon]
     * @return A [Species] for the child [Pokemon]
     */
    fun determineSpecies(parentF: Pokemon): Species {
        // 490 = Manaphy; 489 = Phione
        if (parentF.species.nationalPokedexNumber == 490) {
            return PokemonSpecies.getByPokedexNumber(489)!!
        } else {
            return PokemonSpecies.getFirstEvolution(parentF.species)
        }
    }

    /**
     * Returns a Nature for a child [Pokemon]
     *
     * @param parent1 The first parent [Pokemon]
     * @param parent2 The second parent [Pokemon]
     * @return A [Nature] for the child [Pokemon]
     */
    fun determineNature(parent1: Pokemon, parent2: Pokemon): Nature {
        if (parent1.heldItem().isOf(CobblemonItems.EVERSTONE) && parent2.heldItem().isOf(CobblemonItems.EVERSTONE)) {
            return if (Random().nextBoolean()) {
                parent1.nature
            } else {
                parent2.nature
            }
        } else if (parent1.heldItem().isOf(CobblemonItems.EVERSTONE)) {
            return parent1.nature
        } else if (parent2.heldItem().isOf(CobblemonItems.EVERSTONE)) {
            return parent2.nature
        } else {
            return Natures.getRandomNature()
        }
    }

    /**
     * Returns an [Ability] for a child [Pokemon]
     *
     * @param parentF The female parent [Pokemon]
     * @return An [Ability] for the child [Pokemon]
     */
    fun determineAbility(parentF: Pokemon): Ability {
        if (Random().nextInt(0, 100) <= 60) {
            return parentF.ability
        } else {
            return parentF.species.abilities.select(parentF.species, parentF.aspects).first
        }
    }

    /**
     * Returns a [MoveSet] for a child [Pokemon]
     *
     * @param parentF The female parent [Pokemon]
     * @param parentM The male parent [Pokemon]
     * @param childSpecies The [Species] of the child
     * @return A [MoveSet] for the child [Pokemon]
     */
    fun determineMoveSet(parentF: Pokemon, parentM: Pokemon, childSpecies: Species): MutableList<MoveTemplate> {
        val returnMoves = mutableListOf<MoveTemplate>()

        // Level 1 moves
        for (move in childSpecies.moves.getLevelUpMovesUpTo(1)) {
            returnMoves.add(move)
        }

        // Level up moves, if both parents have them
        for (move in childSpecies.moves.getLevelUpMovesUpTo(100)) {
            if (parentF.allAccessibleMoves.contains(move) && parentM.allAccessibleMoves.contains(move)) {
                returnMoves.add(move)
            }
        }

        // Egg moves
        for (move in childSpecies.moves.eggMoves) {
            if (parentF.allAccessibleMoves.contains(move) || parentM.allAccessibleMoves.contains(move)) {
                returnMoves.add(move)
            }
        }

        return returnMoves
    }

    /**
     * Returns
     */
}