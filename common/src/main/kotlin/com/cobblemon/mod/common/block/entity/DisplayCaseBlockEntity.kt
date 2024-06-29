/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonSounds
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundCategory
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.level.Level

class DisplayCaseBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.DISPLAY_CASE, pos, state), SidedInventory {

    val inv: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)

    /**
     * Updates the [ItemStack] stored in this entity
     *
     * @param player The [Player] that performed this interaction
     * @param hand The [Hand] the player used to perform this interaction
     * @author whatsy
     */
    fun updateItem(player: Player, hand: InteractionHand): InteractionResult {
        val playerStack = player.getItemInHand(hand)

        // Player and case item are the same - do nothing
        if (playerStack.item == getStack().item) {
            return if (playerStack.item != Items.AIR) InteractionResult.SUCCESS else InteractionResult.FAIL
        }

        // Player's hand is empty, case is not empty - give player the item in the case
        if (playerStack.isEmpty && !getStack().isEmpty) {
            if (!player.isCreative) player.setStackInHand(hand, getStack())
            setCaseStack(ItemStack.EMPTY)
            return InteractionResult.success(true)
        }

        // Case is empty, player's hand is not - put playerStack in the case
        if (getStack().isEmpty && !playerStack.isEmpty) {
            setCaseStack(playerStack.copy())
            if (!player.isCreative) playerStack.shrink(1)
            return InteractionResult.success(true)
        }

        // Player has item, case has item - swap items
        if (!getStack().isEmpty && !playerStack.isEmpty) {
            val oldCaseStack = getStack()
            setCaseStack(playerStack.copy())
            if (!player.isCreative) {
                playerStack.shrink(1)
                player.giveItemStack(oldCaseStack)
            }

            return InteractionResult.SUCCESS
        }

        return InteractionResult.FAIL
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
            world!!.playSound(null, pos, CobblemonSounds.DISPLAY_CASE_REMOVE_ITEM, SoundCategory.BLOCKS)
        } else {
            world!!.playSound(null, pos, CobblemonSounds.DISPLAY_CASE_ADD_ITEM, SoundCategory.BLOCKS)
        }
        onItemUpdated(world!!, oldState, world!!.getBlockState(pos))
    }

    override fun writeNbt(nbt: CompoundTag, registryLookup: RegistryWrapper.WrapperLookup) {
        super.writeNbt(nbt, registryLookup)
        Inventories.writeNbt(nbt, inv, true, registryLookup)
    }

    override fun readNbt(nbt: CompoundTag, registryLookup: RegistryWrapper.WrapperLookup) {
        super.readNbt(nbt, registryLookup)
        inv.clear()
        Inventories.readNbt(nbt, inv, registryLookup)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun getUpdateTag(registryLookup: RegistryWrapper.WrapperLookup): CompoundTag? {
        return this.createNbt(registryLookup)
    }

    private fun onItemUpdated(world: Level, oldState: BlockState, newState: BlockState) {
        world.updateListeners(pos, oldState, newState, Block.NOTIFY_LISTENERS)
        world.updateComparators(pos, world.getBlockState(pos).block)
        markDirty()
    }

    override fun clear() {
        inv.clear()
    }

    override fun size(): Int {
        return inv.size
    }

    override fun isEmpty(): Boolean {
        return getStack().isEmpty
    }

    override fun getStack(slot: Int): ItemStack {
        return getStack()
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        val oldState = cachedState
        val result = Inventories.splitStack(inv, slot, amount)
        if (world != null) onItemUpdated(world!!, oldState, world!!.getBlockState(pos))
        return result
    }

    override fun removeStack(slot: Int): ItemStack {
        val oldState = cachedState
        val result = Inventories.removeStack(inv, slot)
        if (world != null) onItemUpdated(world!!, oldState, world!!.getBlockState(pos))
        return result
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        val oldState = cachedState
        inv[slot] = stack
        if (stack.count > stack.maxCount) {
            stack.count = stack.maxCount
        }
        if (world != null) onItemUpdated(world!!, oldState, world!!.getBlockState(pos))
    }

    override fun canPlayerUse(player: Player?) = false

    override fun getAvailableSlots(side: Direction?): IntArray {
        val result = IntArray(inv.size)
        for (i in result.indices) {
            result[i] = i
        }
        return result
    }

    override fun getMaxCountPerStack() = 1

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        if(dir == Direction.DOWN) return false
        return getStack().isEmpty
    }
    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean = (dir == Direction.DOWN)
}