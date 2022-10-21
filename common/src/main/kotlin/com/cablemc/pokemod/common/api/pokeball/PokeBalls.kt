/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokeball

import com.cablemc.pokemod.common.api.pokeball.catching.modifiers.GuaranteedModifier
import com.cablemc.pokemod.common.api.pokeball.catching.modifiers.MultiplierModifier
import com.cablemc.pokemod.common.pokeball.PokeBall
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.util.Identifier

/**
 * Main API point for Pokeballs
 *
 * Get or register Pokeballs
 */
object PokeBalls {
    private val allPokeBalls = mutableListOf<PokeBall>()

    val POKE_BALL = registerPokeBall(PokeBall(pokemodResource("poke_ball")))
    val GREAT_BALL = registerPokeBall(PokeBall(pokemodResource("great_ball"), listOf(MultiplierModifier(1.5f, null))))
    val ULTRA_BALL = registerPokeBall(PokeBall(pokemodResource("ultra_ball"), listOf(MultiplierModifier(2.0f, null))))
    val MASTER_BALL = registerPokeBall(PokeBall(pokemodResource("master_ball"), listOf(GuaranteedModifier())))

    /**
     * Registers a new pokeball type.
     * @return the pokeball type.
     */
    fun registerPokeBall(pokeBall: PokeBall) : PokeBall {
        allPokeBalls.add(pokeBall)
        return pokeBall
    }

    /**
     * Gets a Pokeball from registry name.
     * @return the pokeball object if found otherwise null.
     */
    fun getPokeBall(name : Identifier) : PokeBall? {
        return allPokeBalls.find { pokeball -> pokeball.name == name }
    }
}