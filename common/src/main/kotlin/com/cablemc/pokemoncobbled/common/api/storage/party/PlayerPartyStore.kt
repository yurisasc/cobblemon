/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PassiveEvolution
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.cablemc.pokemoncobbled.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.UUID
import kotlin.math.round
import kotlin.random.Random

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
        return PokemonCobbled.storage.getPC(playerUUID)
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
                        pokemon.currentHealth = (pokemon.hp * PokemonCobbled.config.faintAwakenHealthPercent).toInt()
                        player.sendMessage(Text.translatable("pokemoncobbled.party.faintRecover", pokemon.species.translatedName))
                    }
                }
                // Passive healing while less than full health
                else if (pokemon.currentHealth < pokemon.hp) {
                    pokemon.healTimer -= 1
                    if (pokemon.healTimer <= -1) {
                        pokemon.healTimer = PokemonCobbled.config.healTimer;
                        val healAmount = 1.0.coerceAtLeast(pokemon.hp.toDouble() * PokemonCobbled.config.healPercent)
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
    }
}