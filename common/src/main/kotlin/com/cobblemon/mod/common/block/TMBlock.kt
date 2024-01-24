/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.net.messages.client.ui.OpenTMMPacket
import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.rmi.registry.Registry

class TMBlock(properties: Settings): HorizontalFacingBlock(properties), Waterloggable, SidedInventory {

    var filterTM: TechnicalMachine? = null
    var previousFilterTM: TechnicalMachine? = null
    var loadedMaterials: MutableList<ItemStack> = mutableListOf()
    companion object {
        val WATERLOGGED = BooleanProperty.of("waterlogged")
        val ON = BooleanProperty.of("on")

        private var NORTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.3125, 0.9375),
            VoxelShapes.cuboid(0.0, 0.3125, 0.75, 1.0, 0.9375, 0.9375),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

        private var SOUTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0625, 1.0, 0.3125, 1.0),
            VoxelShapes.cuboid(0.0, 0.3125, 0.0625, 1.0, 0.9375, 0.25),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

        private var WEST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.9375, 0.3125, 1.0),
            VoxelShapes.cuboid(0.75, 0.3125, 0.0, 0.9375, 0.9375, 1.0),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

        private var EAST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0, 0.0, 1.0, 0.3125, 1.0),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0, 0.25, 0.9375, 1.0),
            VoxelShapes.cuboid(0.0625, 0.3125, 0.0625, 0.9375, 0.875, 0.9375)
        )

    }

    init {
        defaultState = this.stateManager.defaultState.with(FACING, Direction.NORTH)
            .with(WATERLOGGED, false)
            .with(ON, false)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        val abovePosition = blockPlaceContext.blockPos.up()
        val world = blockPlaceContext.world
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState
                .with(FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(WATERLOGGED, blockPlaceContext.world.getFluidState(blockPlaceContext.blockPos).fluid == Fluids.WATER)
                .with(ON, false)
        }

        return null
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(WATERLOGGED)
        builder.add(ON)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(blockState: BlockState, rotation: BlockRotation) =
        blockState.with(FACING, rotation.rotate(blockState.get(FACING)))

    @Deprecated("Deprecated in Java")
    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        blockState: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        // todo if player is not sneaking then act as normal
        if (!player.isSneaking) {
            if (TechnicalMachines.isTechnicalMachine(player.getStackInHand(interactionHand))) {
                if (filterTM != null) {
                    var previousFilterTM = filterTM
                }
                // add TM from player hand to TM Machine as a filter
                filterTM = TechnicalMachines.getTechnicalMachineFromStack(player.getStackInHand(interactionHand))
                // todo change the color of the disk in the TMM
                // todo play a sound
                // todo remove 1 from the stack in the player's hand if not in creative
                if (!player.isCreative) {
                    player.getStackInHand(interactionHand)?.decrement(1)
                }


                if (previousFilterTM != null) {
                    //todo give player previousFilterTM
                    //player.setStackInHand(interactionHand, STACKHERE)
                    previousFilterTM = null
                }

                return ActionResult.SUCCESS
            } else {
                if (world.isClient) {
                    return ActionResult.SUCCESS
                }
                // todo spit out any disc filters or materials
                if (filterTM != null) {
                    // spit out the filter TM and set filterTM to null
                    // todo eject TM related to filterTM
                    filterTM = null

                    // todo eject any materials stored in loadedMaterials list
                }

                player.playSound(CobblemonSounds.TMM_ON, SoundCategory.BLOCKS, 1.0f, 1.0f)
                val serverPlayer = player as ServerPlayerEntity
                serverPlayer.openHandledScreen(blockState.createScreenHandlerFactory(world, pos))
                return ActionResult.SUCCESS
            }
        }
        return ActionResult.FAIL
        /*else {
            if (TechnicalMachines.isTechnicalMachine(player.getStackInHand(interactionHand))) {
                if (filterTM != null) {
                    var previousFilterTM = filterTM
                }
                // add TM from player hand to TM Machine as a filter
                filterTM = TechnicalMachines.getTechnicalMachineFromStack(player.getStackInHand(interactionHand))
                // todo change the color of the disk in the TMM
                // todo play a sound
                // todo remove 1 from the stack in the player's hand if not in creative
                if (!player.isCreative) {
                    player.getStackInHand(interactionHand)?.decrement(1)
                }


                if (previousFilterTM != null) {
                    //todo give player previousFilterTM
                    //player.setStackInHand(interactionHand, STACKHERE)
                    previousFilterTM = null
                }

                return ActionResult.SUCCESS
            }
            return ActionResult.FAIL
        }*/
    }

    override fun createScreenHandlerFactory(
        state: BlockState?,
        world: World?,
        pos: BlockPos?
    ): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory(::TMMScreenHandler, Text.of("TM Machine"))
    }

    fun loadMaterial(itemStack: ItemStack) {
        loadedMaterials.add(itemStack)
    }

    fun materialNeeded(tm: TechnicalMachine, itemStack: ItemStack): Boolean {
        val typeGem = Registries.ITEM.get(ElementalTypes.get(tm.type)?.typeGem).defaultStack
        val recipeItem = Registries.ITEM.get(tm.recipe?.item)

        // if blank TM is needed still
        if ((itemStack !in loadedMaterials && itemStack == CobblemonItems.BLANK_TM.defaultStack))
            return true // load in blank TM
        // if type gem is needed still
        else if ((itemStack !in loadedMaterials && itemStack == typeGem))
            return true // load in Type Gem
        // if the itemStack is listed as needed in the recipe and the count is not currently met
        else if (itemStack.item == recipeItem &&  loadedMaterials.count { it == itemStack } < tm.recipe?.count!!)
            return true
        else
            return false
    }

    fun clearLoadedMaterials() {
        loadedMaterials.clear()
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getStack(slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun removeStack(slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        TODO("Not yet implemented")
    }

    override fun markDirty() {
        TODO("Not yet implemented")
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAvailableSlots(side: Direction?): IntArray {
        TODO("Not yet implemented")
    }

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        // todo only allow for hopper to insert materials if it has a filterTM in it
        if (filterTM != null && stack != null) {

            // if material is needed then load it
            if (materialNeeded(filterTM!!, stack)) {
                loadMaterial(stack)
                return true
            }
            else
                return false
            //return (materialNeeded(filterTM!!, stack))
            // if blank disk has not been loaded


            /*val structure = analyzerEntity.multiblockStructure as FossilMultiblockStructure
            return stack?.let { TechnicalMachines.isTechnicalMachine(it) } == true
                    && structure.fossilInventory.size < Cobblemon.config.maxInsertedFossilItems
                    && structure.isRunning() == false && structure.resultingFossil == null
                    && Fossils.getSubFossilByItemStacks( structure.fossilInventory + mutableListOf(stack) ) != null*/
        }
        return false
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        TODO("Not yet implemented")
    }

    // todo use this code to create the TM item
    /*
    val currentTm = filterTM ?: return@EjectButton sendPacketToServer(CraftBlankTMPacket(handler.input.getStack(2)))
                    sendPacketToServer(
                        CraftTMPacket(
                            currentTm,
                            handler.input.getStack(0),
                            handler.input.getStack(1),
                            handler.input.getStack(2)
                        )
                    )
     */

    @Deprecated("Deprecated in Java")
    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return when (state.get(FACING)) {
            Direction.NORTH -> NORTH_OUTLINE
            Direction.SOUTH -> SOUTH_OUTLINE
            Direction.WEST -> WEST_OUTLINE
            else -> EAST_OUTLINE
        }
    }
}