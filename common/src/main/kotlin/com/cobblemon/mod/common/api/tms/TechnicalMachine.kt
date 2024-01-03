/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tms

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class TechnicalMachine(
    val moveName: String,
    val recipe: TechnicalMachineRecipe,
    val obtainMethods: List<ObtainMethod> = emptyList(),
    val type: String,
    val primaryColor: Int?,
    val secondaryColor: Int?
) {

    companion object {
        /**
         * Filters all available [TechnicalMachines] based on three optional arguments
         *
         * @param search A [String] to check against move names
         * @param type An [ElementalType] to check for
         * @param pokemon A [Pokemon] to check the [Learnset] of
         * @return A [MutableSet] of [TechnicalMachine] that passed the filter
         * @author whatsy
         */
        fun filterTms(search: String?, type: ElementalType?, pokemon: Pokemon?): MutableSet<TechnicalMachine> {
            val tms = TechnicalMachines.tmMap.values.toMutableSet()

            type?.let {
                val iterator = tms.iterator()
                while (iterator.hasNext()) {
                    val tm = iterator.next()
                    if (ElementalTypes.get(tm.type) != type) {
                        iterator.remove()
                    }
                }
            }

            pokemon?.let {
                val iterator = tms.iterator()
                while (iterator.hasNext()) {
                    val tm = iterator.next()
                    val move = Moves.getByName(tm.moveName)
                    if (!pokemon.species.moves.tmLearnableMoves().contains(move)) {
                        iterator.remove()
                    }
                }
            }

            search?.let {
                val iterator = tms.iterator()
                while (iterator.hasNext()) {
                    val tm = iterator.next()
                    if (!tm.translatedMoveName().toString().contains(search, true)) {
                        iterator.remove()
                    }
                }
            }

            return tms
        }
    }

    /**
     * Gets the [Identifier] of this [TechnicalMachine]
     *
     * @return The [Identifier] of this [TechnicalMachine] (or null if not found)
     * @author whatsy
     */
    fun id(): Identifier? {
        TechnicalMachines.tmMap.forEach {
            if (TechnicalMachines.tmMap[it.key] == this) return it.key
        }
        return null
    }

    /**
     * Unlocks this [TechnicalMachine] for the player.
     *
     * @param player The [ServerPlayerEntity] to give this [TechnicalMachine] to.
     * @return Whether the player was successfully granted the [TechnicalMachine]
     * @author whatsy
     */
    fun unlock(player: ServerPlayerEntity): Boolean {
        if (id() == null) return false
        Cobblemon.playerData.get(player).tmSet.add(id()!!)
        player.sendMessage(lang("tms.unlock_tm", lang("move.$moveName")))
        return true
    }

    /**
     * Returns a [Text] of the translated move name of this [TechnicalMachine]
     *
     * @return This [TechnicalMachine]'s move name, translated
     * @author whatsy
     */
    fun translatedMoveName(): Text {
        return lang("move." + this.moveName)
    }
}