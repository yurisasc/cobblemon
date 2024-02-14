/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.EggItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class NestBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.NEST, pos, state) {
    var egg: Egg? = null
    var renderState: BlockEntityRenderState? = null

    fun dropEgg(
        state: BlockState,
        world: World,
        pos: BlockPos,
    ) {
        val blockNbt = NbtCompound()
        writeNbt(blockNbt)
        val itemEntity = ItemEntity(
            world,
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            egg!!.asItemStack(blockNbt)
        )
        world.spawnEntity(itemEntity)
        egg = null
        this.markDirty()
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS)

    }

    fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (egg != null) {
            return if (egg?.timeToHatch == 0) {
                hatchPokemon(state, world, pos, player)
                ActionResult.SUCCESS
            } else {
                dropEgg(state, world, pos)
                ActionResult.SUCCESS
            }
        }
        val playerStack = player.getStackInHand(hand)
        if (playerStack.item == CobblemonItems.POKEMON_EGG) {
            val blockNbt = BlockItem.getBlockEntityNbt(playerStack) as NbtCompound
            this.egg = Egg.fromBlockNbt(blockNbt)
            this.markDirty()
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS)
        }
        return ActionResult.FAIL

    }

    fun hatchPokemon(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity
    ) {
        if (!world.isClient) {
            val party = Cobblemon.storage.getParty(player.uuid)
            val newPoke = egg!!.hatchedPokemon.generatePokemon()
            party.add(newPoke)
            this.egg = null
            this.markDirty()
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS)
        }


    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (egg != null) {
            nbt.put(DataKeys.EGG, egg?.toNbt())
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains(DataKeys.EGG)) {
            egg = Egg.fromNbt(nbt.getCompound(DataKeys.EGG))
        }
        else {
            egg = null
        }
        renderState?.needsRebuild = true
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = createNbt()
        //The block update packet sets nbt to null if the nbt is empty, and doesn't actually update the block
        if (nbt.isEmpty){
           nbt.putBoolean("placeholder", true)
        }
        return nbt
    }

    companion object {
        val TICKER = BlockEntityTicker<NestBlockEntity> { world, pos, state, blockEntity ->
            blockEntity.egg?.let {
                if (it.timeToHatch == 0) {
                    world.setBlockState(pos, state, Block.NOTIFY_LISTENERS)
                    blockEntity.markDirty()
                }
                if (it.timeToHatch > 0) {
                    it.timeToHatch--
                    //Dont write the block to the world every tick, do it every 10 seconds
                    if (it.timeToHatch % 200 == 0) {
                        blockEntity.markDirty()
                    }
                }
            }
        }
    }

}