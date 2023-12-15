/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.moves.BenchedMove
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtString
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

class TechnicalMachineItem(settings: Settings): CobblemonItem(settings) {

    companion object {
        val STORED_MOVE_KEY = "StoredMove"
    }

    override fun hasGlint(stack: ItemStack?) = true

    fun getMoveNbt(stack: ItemStack): TechnicalMachine? {
        val nbtCompound = stack.nbt ?: return null

        return TechnicalMachines.tmMap[Identifier.tryParse(nbtCompound.getString(STORED_MOVE_KEY))]
    }

    fun setMoveNbt(stack: ItemStack, id: String): ItemStack {
        stack.getOrCreateNbt().put(STORED_MOVE_KEY, NbtString.of(id))
        return stack
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        val nbt = getMoveNbt(stack)
        val text: MutableText = if (nbt != null) {
            lang("move." + nbt.moveName)
        } else {
            lang("tms.unknown_move")
        }
        tooltip.add(text.gray())
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        if (user.world.isClient) return ActionResult.FAIL
        if (entity !is PokemonEntity) return ActionResult.FAIL

        val tm = this.getMoveNbt(stack) ?: return ActionResult.FAIL
        val move = Moves.getByName(tm.moveName) ?: return ActionResult.FAIL
        val translatedName = lang("move." + tm.moveName)
        val pokemon = entity.pokemon

        if (!pokemon.species.moves.tmMoves.contains(move)) {
            user.sendMessage(lang("tms.cannot_learn", pokemon.getDisplayName(), translatedName))
            return ActionResult.FAIL
        }
        if (pokemon.allAccessibleMoves.contains(move)) {
            user.sendMessage(lang("tms.already_known", pokemon.getDisplayName(), translatedName))
            return ActionResult.FAIL
        }

        if (!user.isCreative) stack.decrement(1)
        pokemon.benchedMoves.add(BenchedMove(move, 0))
        user.sendMessage(lang("tms.teach_move", pokemon.getDisplayName(), translatedName).green())
        return ActionResult.CONSUME
    }
}