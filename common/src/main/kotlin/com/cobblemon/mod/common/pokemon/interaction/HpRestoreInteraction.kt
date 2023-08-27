/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.interaction

import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

/**
 * An interaction that heals the [Pokemon.currentHealth].
 * This interaction consumes the [ItemStack] behind the trigger.
 *
 * @property amount The amount of HP to restore, this can be a percentage or flat depending on the value of [flat].
 * @property flat If the healing is a flat amount or percentage based.
 *
 * @author Licious
 * @since January 19th, 2022
 */
class HpRestoreInteraction(
    private val amount: Int,
    private val flat: Boolean
) : PokemonEntityInteraction {

    constructor() : this(0, true)

    override val accepted: Set<PokemonEntityInteraction.Ownership> = EnumSet.of(PokemonEntityInteraction.Ownership.OWNER)

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        if (this.amount <= 0 || entity.pokemon.currentHealth == entity.pokemon.hp) {
            return false
        }
        val calculatedAmount = if (this.flat) this.amount else (entity.pokemon.hp * (this.amount * 0.1)).toInt()
        entity.pokemon.currentHealth += calculatedAmount
        entity.heal(entity.maxHealth - entity.health)
        player.sendMessage(lang("interaction.hp.heal"))
        return true
    }

    companion object {

        val TYPE_ID = cobblemonResource("heal")

    }

}