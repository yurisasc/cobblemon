/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity.displaycase

import com.cobblemon.mod.common.CobblemonBlockEntities
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class DisplayCaseBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.DISPLAY_CASE, pos, state) {

    val inv: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)

    /**
     * Updates the [ItemStack] stored in this entity
     *
     * @param player The [PlayerEntity] that performed this interaction
     * @param hand The [Hand] the player used to perform this interaction
     * @author whatsy
     */
    fun updateItem(player: PlayerEntity, hand: Hand): ActionResult {
        val playerStack = player.getStackInHand(hand)

        // Player and case item are the same - do nothing
        if (playerStack.item == getStack().item) {
            return ActionResult.FAIL
        }

        // Player's hand is empty, case is not empty - give player the item in the case
        if (playerStack.isEmpty && !getStack().isEmpty) {
            if (!player.isCreative) player.setStackInHand(hand, getStack())
            setCaseStack(ItemStack.EMPTY)
            return ActionResult.success(true)
        }

        // Case is empty, player's hand is not - put playerStack in the case
        if (getStack().isEmpty && !playerStack.isEmpty) {
            setCaseStack(playerStack.copy())
            if (!player.isCreative) playerStack.decrement(1)
            return ActionResult.success(true)
        }

        // Player has item, case has item - swap items
        if (!getStack().isEmpty && !playerStack.isEmpty) {
            val oldCaseStack = getStack()
            setCaseStack(playerStack.copy())
            if (!player.isCreative) {
                playerStack.decrement(1)
                player.giveItemStack(oldCaseStack)
            }

            return ActionResult.SUCCESS
        }

        return ActionResult.FAIL
    }

    fun getStack(): ItemStack {
        return inv[0]
    }

    private fun setCaseStack(newStack: ItemStack) {
        if (world == null) return
        val oldState = world!!.getBlockState(pos)
        newStack.count = 1
        inv[0] = newStack
        if (newStack.isEmpty) {
            world!!.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS)
        } else {
            world!!.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS)
        }
        world!!.updateListeners(pos, oldState, world!!.getBlockState(pos), Block.NOTIFY_LISTENERS)
        world!!.updateComparators(pos, world!!.getBlockState(pos).block)
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inv, true)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        inv.clear()
        Inventories.readNbt(nbt, inv)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return this.createNbt()
    }
}