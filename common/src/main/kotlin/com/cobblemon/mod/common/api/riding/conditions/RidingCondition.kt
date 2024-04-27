/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.conditions

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Represents a condition that should be met when queried for riding a pokemon. Conditions for
 * riding should validate at minimum against the interacting player or pokemon being ridden.
 *
 * @since 1.5.0
 */
fun interface RidingCondition {

    /**
     * Performs logic which determines whether a player or pokemon is allowed to be ridden.
     *
     * @return `true` when the condition is satisfactory, `false` otherwise
     * @since 1.5.0
     */
    fun validate(player: ServerPlayerEntity, entity: PokemonEntity) : Boolean

    /**
     * Creates a new [RidingCondition] which evaluates two conditions based on an OR logic gate.
     * In other words, conditions combined through this method only need one condition to succeed
     * for the overall condition to be considered valid.
     *
     * @return A new [RidingCondition] which accepts either condition as capable of being valid
     * @since 1.5.0
     */
    fun or(condition: RidingCondition) : RidingCondition {
        return RidingCondition { player, pokemon -> this.validate(player, pokemon) || condition.validate(player, pokemon) }
    }

    /**
     * Creates a new [RidingCondition] which evaluates two conditions based on an AND logic gate.
     * In other words, conditions combined through this method need both conditions to succeed
     * for the overall condition to be considered valid.
     *
     * @return A new [RidingCondition] which requires both conditions to be valid
     * @since 1.5.0
     */
    fun and(condition: RidingCondition) : RidingCondition {
        return RidingCondition { player, pokemon -> this.validate(player, pokemon) && condition.validate(player, pokemon) }
    }

}