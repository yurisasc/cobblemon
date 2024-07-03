/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.moves.BenchedMove
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.block.entity.TMBlockEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.components.TMMoveComponent
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.block.Block
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand

class TechnicalMachineItem(settings: Settings): CobblemonItem(settings) {
    override fun hasGlint(stack: ItemStack?) = false

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        tooltipType: TooltipType
    ) {
        val move = TMMoveComponent.getTMMove(stack)
        val text = move?.displayName ?: lang("tms.unknown_move")
        tooltip.add(text.gray())
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        if (user.world.isClient) return ActionResult.FAIL
        if (entity !is PokemonEntity) return ActionResult.FAIL

        val tm = TMMoveComponent.getTMMove(stack) ?: return ActionResult.FAIL
        val pokemon = entity.pokemon

        val tmLearnableMoves = pokemon.species.moves.tmLearnableMoves()

        if (!tmLearnableMoves.contains(tm)) {
            user.sendMessage(lang("tms.cannot_learn", pokemon.getDisplayName(), tm.displayName))
            return ActionResult.FAIL
        }
        if (pokemon.allAccessibleMoves.contains(tm)) {
            user.sendMessage(lang("tms.already_known", pokemon.getDisplayName(), tm.displayName))
            return ActionResult.FAIL
        }

        if (!user.isCreative) {
            stack.decrement(1)
        }
        if (pokemon.moveSet.hasSpace()) {
            pokemon.moveSet.add(tm.create())
        } else {
            pokemon.benchedMoves.add(BenchedMove(tm, 0))
        }

        user.sendMessage(lang("tms.teach_move", pokemon.getDisplayName(), tm.displayName).green())
        user.playSoundToPlayer(CobblemonSounds.TM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f)
        entity.cry()
        return ActionResult.CONSUME
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) {
            context.player?.swingHand(context.hand)
            return ActionResult.FAIL
        }

        val tmMachineEntity = context.world.getBlockEntity(context.hitPos.toBlockPos())

        if (tmMachineEntity is TMBlockEntity
                && (tmMachineEntity.tmmInventory.filterTM == null
                        || tmMachineEntity.tmmInventory.filterTM != TMMoveComponent.getTMMove(context.stack))) {
            context.player?.playSoundToPlayer(CobblemonSounds.TMM_ON, SoundCategory.BLOCKS, 1.0f, 1.0f)
            context.player?.swingHand(context.hand)
            val filterTM = tmMachineEntity.tmmInventory.filterTM
            if (filterTM != null) {
                if (!context.player?.isCreative!!) {
                    context.player!!.giveItemStack(TMMoveComponent.createStack(filterTM))
                }
            }

            // set filterTM equal to the item it corresponds to
            tmMachineEntity.tmmInventory.filterTM = TMMoveComponent.getTMMove(context.stack)



            /*// if materials are in the machine already
            if (TMM.tmmInventory.items?.isNotEmpty() == true) {
                TMM.tmmInventory.items?.forEach {
                    // Get the direction the block is facing
                    val facingDirection =  TMM.blockState.get(Properties.FACING).opposite

                    // Calculate the center position of the block
                    val frontOffset = 0.5 // Half block offset to the front
                    val spawnX = TMM.blockPos.x + 0.5 + facingDirection.offsetX * frontOffset
                    val spawnY = TMM.blockPos.y + 0.3 + facingDirection.offsetY * frontOffset
                    val spawnZ = TMM.blockPos.z + 0.5 + facingDirection.offsetZ * frontOffset

                    // Create the ItemEntity at the center of the block
                    val itemEntity = ItemEntity( context.world, spawnX, spawnY, spawnZ, it)

                    // Create the ItemEntity
                    itemEntity.setVelocity(0.0, 0.0, 0.0)

                    // Add the ItemEntity to the world
                    context.world.spawnEntity(itemEntity)
                }
                TMM.tmmInventory.items?.clear()
            }*/

            // todo change the color of the disk in the TMM
            // todo remove 1 from the stack in the player's hand if not in creative
            if (!context.player?.isCreative!!) {
                context.player?.getStackInHand(context.hand)?.decrement(1)
            }

            tmMachineEntity.markDirty()
            tmMachineEntity.world?.updateListeners(tmMachineEntity.blockPos, tmMachineEntity.cachedState, tmMachineEntity.cachedState, Block.NOTIFY_LISTENERS)
        }

        /*val type = ElementalTypes.getOrException(getMoveNbt(context.stack)!!.type)
        context.player!!.giveItemStack(Registries.ITEM.get(type.typeGem).defaultStack)*/
        return super.useOnBlock(context)
    }
}