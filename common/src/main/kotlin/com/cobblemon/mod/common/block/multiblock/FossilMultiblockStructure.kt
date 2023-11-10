/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.fossil.Fossil
import com.cobblemon.mod.common.api.fossil.Fossils
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.api.multiblock.MultiblockStructure
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.block.entity.fossil.FossilCompartmentBlockEntity
import com.cobblemon.mod.common.block.entity.fossil.FossilMultiblockEntity
import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
import com.cobblemon.mod.common.block.fossilmachine.FossilCompartmentBlock
import com.cobblemon.mod.common.block.fossilmachine.FossilMonitorBlock
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.giveOrDropItemStack
import com.cobblemon.mod.common.util.party
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FossilMultiblockStructure (
    val monitorPos: BlockPos,
    val compartmentPos: BlockPos,
    val tubeBasePos: BlockPos
) : MultiblockStructure {

    override val controllerBlockPos = compartmentPos

    // TODO: API method for this
    private var organicMaterialInside = 0

    private var createdPokemon: Pokemon? = null
    var timeRemaining = -1
        private set
    var resultingFossil: Fossil? = null
        private set
    private var lastInteraction: Long = 0
    private var machineStartTime: Long = 0

    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        val compartmentEntity = world.getBlockEntity(compartmentPos) as FossilCompartmentBlockEntity
        val stack = player.getStackInHand(interactionHand)
        val itemId = Registries.ITEM.getId(stack.item)

        if (this.createdPokemon != null) {
            if (player !is ServerPlayerEntity) {
                return ActionResult.FAIL
            }

            if (!stack.isIn(CobblemonItemTags.POKEBALLS)) {
                return ActionResult.FAIL
            }

            if (stack.item !is PokeBallItem) {
                return ActionResult.FAIL
            }

            val ballType = (stack.item as PokeBallItem).pokeBall
            if (!player.isCreative) {
                stack?.decrement(1)
            }

            this.createdPokemon!!.caughtBall = ballType
            player.party().add(this.createdPokemon!!)
            player.playSound(CobblemonSounds.FOSSIL_MACHINE_RETRIEVE_POKEMON, SoundCategory.BLOCKS, 1.0F, 1.0F)

            this.createdPokemon = null
            compartmentEntity.clear()
            this.updateFillLevel(world)
            this.updateFossilType(world)
            return ActionResult.SUCCESS
        }

        if (this.isRunning()) {
            return ActionResult.FAIL
        }

        // Reclaim the last fossil from the machine if their hand is empty
        if (player.getStackInHand(interactionHand).isEmpty) {
            if (compartmentEntity.isEmpty()) {
                return ActionResult.CONSUME
            }

            player.setStackInHand(interactionHand, compartmentEntity.withdrawLastFossilStack())
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.8f, 1.0f)
            this.updateFossilType(world)
            return ActionResult.SUCCESS
        }

        // Check if the player is holding a fossil and if so insert it into the machine.
        if (Fossils.isFossilIngredient(stack)) {
            if (compartmentEntity.isFull()) {
                return ActionResult.FAIL
            }

            val copyFossilStack = stack.copyWithCount(1)
            if (!player.isCreative) {
                stack?.decrement(1)
            }

            compartmentEntity.insertFossilStack(copyFossilStack)
            this.updateFossilType(world)
            world.playSound(null, compartmentPos, CobblemonSounds.FOSSIL_MACHINE_INSERT_FOSSIL, SoundCategory.BLOCKS)
            return ActionResult.SUCCESS
        }

        // Check if the player is holding a natural material and if so, feed it to the machine.
        if (NaturalMaterials.isNaturalMaterial(itemId)) {
            val natureValue = NaturalMaterials.getContent(itemId) ?: return ActionResult.FAIL

            if (timeRemaining > 0) return ActionResult.FAIL

            if (this.organicMaterialInside >= 64) return ActionResult.FAIL

            organicMaterialInside += natureValue

            if (this.organicMaterialInside >= 64) {
                player.playSound(CobblemonSounds.FOSSIL_MACHINE_DNA_FULL, SoundCategory.BLOCKS, 1.0F, 1.0F)
            } else if (world.time - this.lastInteraction < 10) {
                player.playSound(CobblemonSounds.FOSSIL_MACHINE_INSERT_DNA_SMALL, SoundCategory.BLOCKS, 1.0F, 1.0F)
            } else {
                player.playSound(CobblemonSounds.FOSSIL_MACHINE_INSERT_DNA, SoundCategory.BLOCKS, 1.0F, 1.0F)
            }

            this.lastInteraction = world.time

            if (!player.isCreative) {
                stack?.decrement(1)
                player.giveOrDropItemStack(ItemStack(Registries.ITEM.get(NaturalMaterials.getReturnItem(itemId))), false)
            }

            this.markDirty(world)
            this.updateFillLevel(world)
            return ActionResult.SUCCESS
        }

        return ActionResult.CONSUME
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        val monitorEntity = world.getBlockEntity(monitorPos) as MultiblockEntity
        val compartmentEntity = world.getBlockEntity(compartmentPos) as MultiblockEntity
        val tubeBaseEntity = world.getBlockEntity(tubeBasePos) as FossilTubeBlockEntity
        val tubeTopEntity = world.getBlockEntity(tubeBasePos.up()) as MultiblockEntity

        tubeBaseEntity.connectorPosition = null
        tubeBaseEntity.fillLevel = 0
        monitorEntity.multiblockStructure = null
        compartmentEntity.multiblockStructure = null
        tubeBaseEntity.multiblockStructure = null
        tubeTopEntity.multiblockStructure = null

        MinecraftClient.getInstance().soundManager.stopSounds(CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP.id, SoundCategory.BLOCKS)

        this.stopMachine(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    override fun tick(world: World) {
        if (this.createdPokemon != null) {
            return
        }

        if (this.isRunning() && (world.time - this.machineStartTime) % 160L == 0L) {
            world.playSound(null, this.tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP, SoundCategory.BLOCKS, 1.0F, 1.0F)
        }

        if (this.timeRemaining == -1 && this.organicMaterialInside >= MATERIAL_TO_START && this.resultingFossil != null) {
            // TODO: Set fossil to tube entity
            this.startMachine(world)
            return
        }

        if (this.timeRemaining >= 0) {
            this.timeRemaining--
        }

        if (this.timeRemaining % TIME_PER_STAGE == 0) {
            this.updateProgress(world)
            this.syncToClient(world)
            this.markDirty(world)
        }

        if (this.timeRemaining == 0) {
            val compartmentEntity = world.getBlockEntity(compartmentPos) as FossilCompartmentBlockEntity
            world.playSound(null, tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundCategory.BLOCKS)
            MinecraftClient.getInstance().soundManager.stopSounds(CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP.id, SoundCategory.BLOCKS)
            compartmentEntity.clear()

            this.resultingFossil?.let {
                this.createdPokemon = it.result.create()
            }

            this.stopMachine(world)
        }
    }

    override fun syncToClient(world: World) {
        val tubeBaseEntity = world.getBlockEntity(tubeBasePos) as MultiblockEntity
        val controllerEntity = world.getBlockEntity(controllerBlockPos) as MultiblockEntity
        val compartmentEntity = world.getBlockEntity(tubeBasePos) as MultiblockEntity

        world.updateListeners(controllerBlockPos, controllerEntity.cachedState, controllerEntity.cachedState, Block.NOTIFY_LISTENERS)
        world.updateListeners(tubeBasePos, tubeBaseEntity.cachedState, tubeBaseEntity.cachedState, Block.NOTIFY_LISTENERS)
        world.updateListeners(compartmentPos, compartmentEntity.cachedState, compartmentEntity.cachedState, Block.NOTIFY_LISTENERS)
    }

    override fun markDirty(world: World) {
        val entities = listOf(
            world.getBlockEntity(compartmentPos),
            world.getBlockEntity(tubeBasePos),
            world.getBlockEntity(tubeBasePos.up()),
            world.getBlockEntity(monitorPos)
        )
        entities.forEach {
            it?.markDirty()
        }
    }

    fun startMachine(world: World) {
        this.timeRemaining = TIME_TO_TAKE
        this.machineStartTime = world.time

        world.playSound(null, tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVATE, SoundCategory.BLOCKS)
        world.playSound(null, tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP, SoundCategory.BLOCKS)

        this.updateOnStatus(world)
        this.updateProgress(world)
        this.updateFillLevel(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    fun stopMachine(world: World){
        this.timeRemaining = -1
        this.organicMaterialInside = 0

        val compartmentEntity = world.getBlockEntity(compartmentPos) as FossilCompartmentBlockEntity
        compartmentEntity.clear()

        this.updateOnStatus(world)
        this.updateProgress(world)
        this.updateFillLevel(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    fun updateFillLevel(world: World) {
        val tubeEntity = world.getBlockEntity(tubeBasePos) as FossilTubeBlockEntity
        val currentFillLevel =  (this.organicMaterialInside / 8).coerceAtMost(8)

        if (this.createdPokemon != null) {
            tubeEntity.fillLevel = 8
            return
        }

        if (currentFillLevel != tubeEntity.fillLevel) {
            tubeEntity.fillLevel = currentFillLevel
            this.syncToClient(world)
            tubeEntity.markDirty()
        }
    }

    fun updateOnStatus(world: World) {
        val compartmentState = world.getBlockState(compartmentPos)
        world.setBlockState(compartmentPos, compartmentState.with(FossilCompartmentBlock.ON, timeRemaining >= 0))
    }

    fun updateProgress(world: World) {
        val progress = if (timeRemaining <= 0) 0 else ((TIME_TO_TAKE - timeRemaining) / TIME_PER_STAGE) + 1
        val monitorState = world.getBlockState(monitorPos)
        world.setBlockState(monitorPos, monitorState.with(FossilMonitorBlock.PROGRESS, progress))
    }

    /**
     * Checks for a resulting fossil type inside the machine.
     * @param world The world to check in.
     * @return The resulting fossil type if found, otherwise null.
     */
    fun updateFossilType(world: World) {
        val compartmentEntity = world.getBlockEntity(compartmentPos) as FossilCompartmentBlockEntity
        if (compartmentEntity.isEmpty()) {
            if (this.resultingFossil == null) {
                return
            }
            this.resultingFossil = null
        } else {
            this.resultingFossil = Fossils.getFossilByItemStacks(compartmentEntity.getInsertedFossilStacks())
        }
    }

    /**
     * Checks if the machine is currently running.
     */
    fun isRunning(): Boolean {
        return this.timeRemaining > 0
    }

    override fun writeToNbt(): NbtCompound {
        val result = NbtCompound()
        result.put(DataKeys.MONITOR_POS, NbtHelper.fromBlockPos(monitorPos))
        result.put(DataKeys.COMPARTMENT_POS, NbtHelper.fromBlockPos(compartmentPos))
        result.put(DataKeys.TUBE_BASE_POS, NbtHelper.fromBlockPos(tubeBasePos))
        result.putInt(DataKeys.TIME_LEFT, timeRemaining)
        result.putInt(DataKeys.ORGANIC_MATERIAL, organicMaterialInside)

        if (this.resultingFossil != null) {
            result.putString(DataKeys.INSERTED_FOSSIL, this.resultingFossil!!.asString())
        }

        if (this.createdPokemon != null) {
            result.put(DataKeys.CREATED_POKEMON, createdPokemon!!.saveToNBT(NbtCompound()))
        }

        return result
    }

    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { world, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick(world)
            }
        }

        const val TICKS_PER_MINUTE = 1200
        const val MATERIAL_TO_START = 64
        const val TIME_TO_TAKE = TICKS_PER_MINUTE * 1
        const val TIME_PER_STAGE = TIME_TO_TAKE / 8

        fun fromNbt(nbt: NbtCompound): FossilMultiblockStructure {
            val monitorPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.MONITOR_POS))
            val compartmentPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.COMPARTMENT_POS))
            val tubeBasePos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.TUBE_BASE_POS))

            val result = FossilMultiblockStructure(monitorPos, compartmentPos, tubeBasePos)
            result.organicMaterialInside = nbt.getInt(DataKeys.ORGANIC_MATERIAL)
            result.timeRemaining = nbt.getInt(DataKeys.TIME_LEFT)

            if (nbt.contains(DataKeys.INSERTED_FOSSIL)) {
                val id = Identifier(nbt.getString(DataKeys.INSERTED_FOSSIL))
                val fossil = Fossils.getByIdentifier(id)

                if (fossil != null) {
                    result.resultingFossil = fossil
                } else {
                    Cobblemon.LOGGER.error("Loaded fossil structure with invalid fossil type: {}", id)
                }
            }

            if (nbt.contains(DataKeys.CREATED_POKEMON)) {
                result.createdPokemon = Pokemon.loadFromNBT(nbt.getCompound(DataKeys.CREATED_POKEMON))
            }
            return result
        }

    }
}