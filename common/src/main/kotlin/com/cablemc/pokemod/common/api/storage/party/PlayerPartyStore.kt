/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.storage.party

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.pokemon.evolution.PassiveEvolution
import com.cablemc.pokemod.common.api.storage.pc.PCStore
import com.cablemc.pokemod.common.battles.BattleRegistry
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.activestate.ShoulderedState
import com.cablemc.pokemod.common.util.DataKeys
import com.cablemc.pokemod.common.util.getPlayer
import com.cablemc.pokemod.common.util.isPokemonEntity
import com.cablemc.pokemod.common.util.lang
import java.util.UUID
import kotlin.math.round
import kotlin.random.Random
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/**
 * A [PartyStore] used for a single player. This uses the player's UUID as the store's UUID, and is declared as its own
 * class so that the purpose of this store is clear in practice. It also automatically adds the player's UUID as an
 * observer UUID as per [PartyStore.observerUUIDs]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
open class PlayerPartyStore(
    /** The UUID of the player this store is for. */
    val playerUUID: UUID
) : PartyStore(playerUUID) {
    override fun initialize() {
        super.initialize()
        observerUUIDs.add(playerUUID)
    }

    open fun getOverflowPC(): PCStore? {
        return Pokemod.storage.getPC(playerUUID)
    }

    override fun add(pokemon: Pokemon): Boolean {
        return if (super.add(pokemon)) {
            true
        } else {
            val player = playerUUID.getPlayer()
            val pc = getOverflowPC()

            if (pc == null || !pc.add(pokemon)) {
                if (pc == null) {
                    player?.sendMessage(lang("overflow_no_pc"))
                } else {
                    player?.sendMessage(lang("overflow_no_space", pc.name))
                }
                false
            } else {
                player?.sendMessage(lang("overflow_to_pc", pokemon.species.translatedName, pc.name))
                true
            }
        }
    }

    /**
     * Called on the party every second for routine party updates
     * ex: Passive healing, statuses, etc
     */
    fun onSecondPassed(player: ServerPlayerEntity) {
        // Passive healing and passive statuses require the player be out of battle
        if (BattleRegistry.getBattleByParticipatingPlayer(player) == null) {
            val random = Random.Default
            for (pokemon in this) {
                // Awake from fainted
                if (pokemon.isFainted()) {
                    pokemon.faintedTimer -= 1
                    if (pokemon.faintedTimer <= -1) {
                        pokemon.currentHealth = (pokemon.hp * Pokemod.config.faintAwakenHealthPercent).toInt()
                        player.sendMessage(Text.translatable("pokemod.party.faintRecover", pokemon.species.translatedName))
                    }
                }
                // Passive healing while less than full health
                else if (pokemon.currentHealth < pokemon.hp) {
                    pokemon.healTimer -= 1
                    if (pokemon.healTimer <= -1) {
                        pokemon.healTimer = Pokemod.config.healTimer;
                        val healAmount = 1.0.coerceAtLeast(pokemon.hp.toDouble() * Pokemod.config.healPercent)
                        pokemon.currentHealth = pokemon.currentHealth + round(healAmount).toInt();
                    }
                }

                // Statuses
                val status = pokemon.status
                if (status != null) {
                    if (status.isExpired()) {
                        status.status.onStatusExpire(player, pokemon, random)
                        pokemon.status = null
                    } else {
                        status.status.onSecondPassed(player, pokemon, random)
                        status.tickTimer()
                    }
                }

                // Passive evolutions
                pokemon.evolutions.filterIsInstance<PassiveEvolution>().forEach { it.attemptEvolution(pokemon) }
            }
        }

        // Shoulder validation code
        if (player.shoulderEntityLeft.isPokemonEntity() && !validateShoulder(player.shoulderEntityLeft, true)) {
            player.dropShoulderEntity(player.shoulderEntityLeft)
        }
        if (player.shoulderEntityRight.isPokemonEntity() && !validateShoulder(player.shoulderEntityRight, false)) {
            player.dropShoulderEntity(player.shoulderEntityRight)
        }

        forEach {
            val state = it.state
            if (state is ShoulderedState && !state.isStillShouldered(player)) {
                it.recall()
            }
        }
    }

    fun validateShoulder(shoulderEntity: NbtCompound, isLeft: Boolean): Boolean {
        val pokemon = find { it.uuid == shoulderEntity.getCompound("Pokemon").getUuid(DataKeys.POKEMON_UUID) }
        if (pokemon == null || (pokemon.state as? ShoulderedState)?.isLeftShoulder != isLeft) {
            return false
        }
        return true
    }
}