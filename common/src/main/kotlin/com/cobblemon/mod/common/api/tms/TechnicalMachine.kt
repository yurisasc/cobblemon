/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tms

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.tms.obtain.NoneObtainMethod
import com.cobblemon.mod.common.util.lang
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class TechnicalMachine(
    val move: MoveTemplate,
    val recipe: TechnicalMachineRecipe?,
    val obtainMethods: List<ObtainMethod> = emptyList(),
    val type: String
) {
    lateinit var id: Identifier

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
                    if (!pokemon.species.moves.tmLearnableMoves().contains(tm.move)) {
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
     * Unlocks this [TechnicalMachine] for the player.
     *
     * @param player The [ServerPlayerEntity] to give this [TechnicalMachine] to.
     * @return Whether the player was successfully granted the [TechnicalMachine]
     * @author whatsy
     */
    fun unlock(player: ServerPlayerEntity): Boolean {
        Cobblemon.playerData.get(player).tmSet.add(id)
        if (!obtainMethods.any { it is NoneObtainMethod }) player.sendMessage(lang("tms.unlock_tm", move.displayName), true)
        return true
    }

    /**
     * Returns a [MutableText] of the translated move name of this [TechnicalMachine]
     *
     * @return This [TechnicalMachine]'s move name, translated
     * @author whatsy
     */
    fun translatedMoveName() = move.displayName
}