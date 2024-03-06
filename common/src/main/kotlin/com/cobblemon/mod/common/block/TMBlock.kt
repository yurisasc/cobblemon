/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.block.entity.TMBlockEntity
import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.util.playSoundServer
import com.cobblemon.mod.common.util.toVec3d
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BrewingStandBlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
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
import net.minecraft.world.WorldAccess

class TMBlock(properties: Settings): BlockWithEntity(properties), Waterloggable, InventoryProvider {
    /*val FACING = Properties.FACING
    val TRIGGERED = Properties.TRIGGERED
    val WATERLOGGED = Properties.WATERLOGGED*/

    //var filterTM: TechnicalMachine? = null
    //var previousFilterTM: TechnicalMachine? = null
    var loadedMaterials: MutableList<ItemStack> = mutableListOf()
    //val inv = TMBlockInventory(this)

    /*class TMBlockInventory(val tmBlock: TMBlock) : SidedInventory {
        override fun clear() {
            TODO("Not yet implemented")
        }

        override fun size(): Int {
            TODO("Not yet implemented")
        }

        override fun isEmpty(): Boolean {
            return false
        }

        override fun getStack(slot: Int): ItemStack {
            return ItemStack.EMPTY
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            return ItemStack.EMPTY
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
            return false
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            return IntArray(1)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            // todo only allow for hopper to insert materials if it has a filterTM in it
            if (tmBlock.filterTM != null && stack != null) {

                // if material is needed then load it
                if (tmBlock.materialNeeded(tmBlock.filterTM!!, stack)) {
                    tmBlock.loadMaterial(stack)
                    return true
                }
                else
                    return false
            }
            return false
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            return false
        }

    }*/
    companion object {
        //val WATERLOGGED = BooleanProperty.of("waterlogged")
        val ON = BooleanProperty.of("on")
        val FACING = Properties.FACING
        val TRIGGERED = Properties.TRIGGERED
        val WATERLOGGED = Properties.WATERLOGGED

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
                .with(TRIGGERED, false)
    }

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        return defaultState
                .with(FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(WATERLOGGED, blockPlaceContext.world.getFluidState(blockPlaceContext.blockPos).fluid == Fluids.WATER)
                .with(ON, false)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        super.onBreak(world, pos, state, player)
        // todo dump materials and filterTM on the ground

    }

    @Deprecated("Deprecated in Java")
    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, sourceBlock: Block?, sourcePos: BlockPos?, notify: Boolean) {
        val bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up())
        val bl2 = state.get(TRIGGERED)
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, this, 4)
            world.setBlockState(pos, state.with(TRIGGERED, true) as BlockState, NO_REDRAW)
        } else if (!bl && bl2) {
            world.setBlockState(pos, state.with(TRIGGERED, false) as BlockState, NO_REDRAW)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun scheduledTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: net.minecraft.util.math.random.Random?) {
        val inventory = (world!!.getBlockEntity(pos) as TMBlockEntity).tmmInventory
        val tmEntity = (world.getBlockEntity(pos) as TMBlockEntity)

        if(world == null || pos == null) {
            return
        }
        //val tankEntity = world.getBlockEntity(pos) as MultiblockEntity

        if (tmEntity.blockState.get(ON)) {
            //print("TMM is in use so scheduled Tick is disabled")
            return
        }

        //this.onTriggerEvent(state, world, pos, random)
        // todo if the output slot is empty then try to craft a TM to that slot
        if (ItemStack.areItemsEqual(inventory.items!!.get(3), ItemStack.EMPTY)) {
                if (state?.let { getInventory(it, world, pos) } != null) {
                    val itemStack: ItemStack
                    itemStack = if (inventory.filterTM != null)
                        inventory.filterTM!!.copy()
                    else
                        ItemStack(CobblemonItems.BLANK_TM, 1)
                    val tm = TechnicalMachines.getTechnicalMachineFromStack(itemStack)
                    // todo use isReadyToCraftTM  to finalize the creation
                    if (((inventory.filterTM != null && tm != null) && isReadyToCraftTM(state, world, pos, tm)) || isReadyToCraftBlankTM(state, world, pos)) {

                        // create TM item to the output slot
                        inventory.items!!.set(3, itemStack)
                        tmEntity.markDirty()

                        // Play sound for TM creation
                        world.playSoundServer(tmEntity.blockPos.toVec3d(), CobblemonSounds.TMM_CRAFT, SoundCategory.BLOCKS)
                    }
                }
        }
        // todo if there is an item in the output slot then spit it out
        else {
            if (state?.let { getInventory(it, world, pos) } != null) {

                val itemStack = inventory.items!!.get(3)

                // Get the direction the block is facing
                val facingDirection = state.get(Properties.FACING)?.opposite ?: return

                // Calculate the center position of the block
                val frontOffset = 0.5 // Half block offset to the front
                val spawnX = pos.x + 0.5 + facingDirection.offsetX * frontOffset
                val spawnY = pos.y + 0.3 + facingDirection.offsetY * frontOffset
                val spawnZ = pos.z + 0.5 + facingDirection.offsetZ * frontOffset

                // Create the ItemEntity at the center of the block
                val itemEntity = ItemEntity(world, spawnX, spawnY, spawnZ, itemStack).copy()

                // Create the ItemEntity
                itemEntity.setVelocity(0.0, 0.0, 0.0)

                // Add the ItemEntity to the world
                world.spawnEntity(itemEntity)

                //clear inventory of SidedInventory
                inventory.items?.clear()
                tmEntity.markDirty()
                //getInventory(state, world, pos).clear()
            }
        }

        /*if (state?.let { getInventory(it, world, pos) } != null) {
            val itemStack: ItemStack
            itemStack = if (inventory.filterTM != null)
                inventory.filterTM!!
            else
                ItemStack(CobblemonItems.BLANK_TM, 1)
            val tm = TechnicalMachines.getTechnicalMachineFromStack(itemStack)
            // todo use isReadyToCraftTM  to finalize the creation
            if ((inventory.filterTM != null && isReadyToCraftTM(state, world, pos, tm!!)) || isReadyToCraftBlankTM(state, world, pos)) {
                // Get the direction the block is facing
                val facingDirection = state.get(Properties.FACING)?.opposite ?: return

                // Calculate the center position of the block
                val frontOffset = 0.5 // Half block offset to the front
                val spawnX = pos.x + 0.5 + facingDirection.offsetX * frontOffset
                val spawnY = pos.y + 0.3 + facingDirection.offsetY * frontOffset
                val spawnZ = pos.z + 0.5 + facingDirection.offsetZ * frontOffset

                // Create the ItemEntity at the center of the block
                val itemEntity = ItemEntity(world, spawnX, spawnY, spawnZ, itemStack).copy()

                // Create the ItemEntity
                itemEntity.setVelocity(0.0, 0.0, 0.0)

                // Add the ItemEntity to the world
                world.spawnEntity(itemEntity)

                //clear inventory of SidedInventory
                inventory.items?.clear()
                //getInventory(state, world, pos).clear()
                }
            }*/
        }

        /*val currentTm = filterTM ?: return sendPacketToServer(CraftBlankTMPacket(handler.input.getStack(2)))
                        sendPacketToServer(
                            CraftTMPacket(
                                currentTm,
                                handler.input.getStack(0),
                                handler.input.getStack(1),
                                handler.input.getStack(2)
                            )
                        )*/

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false

    @Deprecated("Deprecated in Java")
    override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun getComparatorOutput(state: BlockState, world: World?, pos: BlockPos?): Int {

        if(world == null || pos == null) {
            return 0
        }
        val tmBlockEntity = world.getBlockEntity(pos) as TMBlockEntity
        val tm = TechnicalMachines.getTechnicalMachineFromStack(tmBlockEntity.tmmInventory.filterTM)

        // if TMM has already crafted something and something is in the output slot do not send signal out
        if (!ItemStack.areItemsEqual(tmBlockEntity.tmmInventory.items?.get(3), ItemStack.EMPTY))
            return 0

        // if the TMM is ready to craft send signal of 15 out
        if ((tm != null && isReadyToCraftTM(state, world, pos, tm)) || isReadyToCraftBlankTM(state, world, pos))
            return 15

        /*if (tmBlockEntity.automationDelay > 0)
            tmBlockEntity.automationDelay--

        if ((tmBlockEntity.tmmInventory.filterTM != null && isReadyToCraftTM(state, world, pos, tmBlockEntity.tmmInventory.filterTM!!)) || isReadyToCraftBlankTM(state, world, pos)) {
            if (tmBlockEntity.automationDelay == 0) {
                tmBlockEntity.resetAutomationDelay()
                return 15
            }
            else
                return 0
        }*/
        return 0
    }


    override fun getInventory(
            state: BlockState,
            world: WorldAccess,
            pos: BlockPos
    ): SidedInventory {
        val tmBlockEntity = world.getBlockEntity(pos) as TMBlockEntity

        return tmBlockEntity.tmmInventory
    }


    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(WATERLOGGED)
        builder.add(ON)
        builder.add(TRIGGERED)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TMBlockEntity(pos, state)
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

        // clear filter TM on client
        if (world.isClient) {
            val tmBlockEntity = world.getBlockEntity(pos)

            val inventory = (tmBlockEntity as TMBlockEntity).tmmInventory

            inventory.filterTM = null

            return ActionResult.SUCCESS
        }

        val tmBlockEntity = world.getBlockEntity(pos)

        if (tmBlockEntity is TMBlockEntity) {
            // Set the block state to ON when the block is used
            tmBlockEntity.stateManager.openContainer(player, world, pos, blockState)
            player.openHandledScreen(tmBlockEntity as TMBlockEntity?)
        }

        val inventory = (tmBlockEntity as TMBlockEntity).tmmInventory // (world.getBlockEntity(pos) as TMBlockEntity).tmmInventory

        // remove all machine from machine upon use
//        inventory.items?.forEach {
//            // Get the direction the block is facing
//            val facingDirection = blockState.get(Properties.FACING).opposite
//
//            // Calculate the center position of the block
//            val frontOffset = 0.5 // Half block offset to the front
//            val spawnX = pos.x + 0.5 + facingDirection.offsetX * frontOffset
//            val spawnY = pos.y + 0.3 + facingDirection.offsetY * frontOffset
//            val spawnZ = pos.z + 0.5 + facingDirection.offsetZ * frontOffset
//
//            // Create the ItemEntity at the center of the block
//            val itemEntity = ItemEntity(world, spawnX, spawnY, spawnZ, it)
//
//            // Create the ItemEntity
//            itemEntity.setVelocity(0.0, 0.0, 0.0)
//
//            // Add the ItemEntity to the world
//            world.spawnEntity(itemEntity)
//        }
//
//        inventory.items?.clear()

        // spit out any disc filters
        if (inventory.filterTM != null) {
            // spit out the filter TM and set filterTM to null

            val filterTMStack = inventory.filterTM!!

            // Get the direction the block is facing
            val facingDirection = blockState.get(Properties.FACING).opposite

            // Calculate the center position of the block
            val frontOffset = 0.5 // Half block offset to the front
            val spawnX = pos.x + 0.5 + facingDirection.offsetX * frontOffset
            val spawnY = pos.y + 0.3 + facingDirection.offsetY * frontOffset
            val spawnZ = pos.z + 0.5 + facingDirection.offsetZ * frontOffset

            // Create the ItemEntity at the center of the block
            val itemEntity = ItemEntity(world, spawnX, spawnY, spawnZ, filterTMStack).copy()

            // Create the ItemEntity
            itemEntity.setVelocity(0.0, 0.0, 0.0)

            // Add the ItemEntity to the world
            world.spawnEntity(itemEntity)

            inventory.filterTM = null
        }

        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is TMBlockEntity) {
            //player.openHandledScreen(blockEntity as TMBlockEntity?)
            player.playSound(CobblemonSounds.TMM_ON, SoundCategory.BLOCKS, 1.0f, 1.0f)

            // todo maybe uncomment this
            //val serverPlayer = player as ServerPlayerEntity
            //serverPlayer.openHandledScreen(blockState.createScreenHandlerFactory(world, pos))
        }
        return ActionResult.CONSUME
    }

//    override fun createScreenHandlerFactory(
//        state: BlockState?,
//        world: World?,
//        pos: BlockPos?
//    ): NamedScreenHandlerFactory {
//        return SimpleNamedScreenHandlerFactory(::TMMScreenHandler, Text.of("TM Machine"))
//    }

    /*fun loadMaterial(itemStack: ItemStack) {
        loadedMaterials.add(itemStack)
    }*/

    fun isReadyToCraftTM(state: BlockState?, world: WorldAccess, pos: BlockPos, tm: TechnicalMachine): Boolean {
        val typeGem = Registries.ITEM.get(tm.let { ElementalTypes.get(it.type)?.typeGem }).defaultStack
        val recipeItem = Registries.ITEM.get(tm.recipe?.item)

        val inventory = state?.let { getInventory(it, world, pos) }

        return (inventory!!.count(CobblemonItems.BLANK_TM) == 1
                && inventory.count(typeGem.item) == 1
                && ((inventory.count(recipeItem) == tm.recipe?.count) || (ItemStack.areItemsEqual(recipeItem.defaultStack, ItemStack.EMPTY))))
    }

    fun isReadyToCraftBlankTM(state: BlockState?, world: WorldAccess, pos: BlockPos): Boolean {
        val inventory = state?.let { getInventory(it, world, pos) }

        return (inventory!!.count(Items.AMETHYST_SHARD) == 1)
    }

    /*fun materialNeeded(tm: TechnicalMachine, itemStack: ItemStack): Boolean {
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
    }*/

    /*fun clearLoadedMaterials() {
        loadedMaterials.clear()
    }*/

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