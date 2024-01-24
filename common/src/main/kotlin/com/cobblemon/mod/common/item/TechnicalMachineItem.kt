/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.moves.BenchedMove
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.block.TMBlock
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

class TechnicalMachineItem(settings: Settings): CobblemonItem(settings) {

    companion object {
        val STORED_MOVE_KEY = "StoredMove"

        fun getMoveNbt(stack: ItemStack): TechnicalMachine? {
            val nbtCompound = stack.nbt ?: return null

            return TechnicalMachines.tmMap[Identifier.tryParse(nbtCompound.getString(STORED_MOVE_KEY))]
        }
    }

    override fun hasGlint(stack: ItemStack?) = false

    fun setNbt(stack: ItemStack, id: String): ItemStack {
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

        val tm = getMoveNbt(stack) ?: return ActionResult.FAIL
        val move = Moves.getByName(tm.moveName) ?: return ActionResult.FAIL
        val pokemon = entity.pokemon

        val tmLearnableMoves = pokemon.species.moves.tmLearnableMoves()

        if (!tmLearnableMoves.contains(move)) {
            user.sendMessage(lang("tms.cannot_learn", pokemon.getDisplayName(), tm.translatedMoveName()))
            return ActionResult.FAIL
        }
        if (pokemon.allAccessibleMoves.contains(move)) {
            user.sendMessage(lang("tms.already_known", pokemon.getDisplayName(), tm.translatedMoveName()))
            return ActionResult.FAIL
        }

        if (!user.isCreative) stack.decrement(1)
        if (pokemon.moveSet.hasSpace()) { pokemon.moveSet.add(move.create()) }
            else { pokemon.benchedMoves.add(BenchedMove(move, 0)) }

        user.sendMessage(lang("tms.teach_move", pokemon.getDisplayName(), tm.translatedMoveName()).green())
        user.playSound(CobblemonSounds.TM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f)
        entity.cry()
        return ActionResult.CONSUME
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) return ActionResult.FAIL

        val TMM = context.world.getBlockState(context.hitPos.toBlockPos()).block


        if (TMM is TMBlock) {
            if (TMM.filterTM != null) {
                TMM.previousFilterTM = TMM.filterTM
            }
            // set filterTM equal to the item it corresponds to
            TMM.filterTM = getMoveNbt(context.stack)

            // todo change the color of the disk in the TMM
            // todo play a e
            // todo remove 1 from the stack in the player's hand if not in creative
            if (!context.player?.isCreative!!) {
                context.player?.getStackInHand(context.hand)?.decrement(1)
            }

            if (TMM.previousFilterTM != null) {
                //todo give player previousFilterTM
                context.player!!.giveItemStack(TechnicalMachines.getStackFromTechnicalMachine(TMM.previousFilterTM!!))
                TMM.previousFilterTM = null
            }
        }

        /*val type = ElementalTypes.getOrException(getMoveNbt(context.stack)!!.type)
        context.player!!.giveItemStack(Registries.ITEM.get(type.typeGem).defaultStack)*/
        return super.useOnBlock(context)
    }
}