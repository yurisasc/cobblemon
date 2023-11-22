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
import com.cobblemon.mod.common.advancement.CobblemonCriteria
import com.cobblemon.mod.common.api.fossil.Fossil
import com.cobblemon.mod.common.api.fossil.Fossils
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.api.multiblock.MultiblockStructure
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.block.entity.fossil.FossilMultiblockEntity
import com.cobblemon.mod.common.block.fossilmachine.FossilCompartmentBlock
import com.cobblemon.mod.common.block.fossilmachine.FossilMonitorBlock
import com.cobblemon.mod.common.client.render.models.blockbench.fossil.FossilState
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.*
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityPose
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtList
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.ceil

class FossilMultiblockStructure (
    val monitorPos: BlockPos,
    val compartmentPos: BlockPos,
    val tubeBasePos: BlockPos
) : MultiblockStructure {

    override val controllerBlockPos = compartmentPos


    // TODO: API method for this
    var organicMaterialInside = 0
        private set

    var createdPokemon: Pokemon? = null
        private set
    var timeRemaining = -1
        private set
    var resultingFossil: Fossil? = null
        private set
    private var lastInteraction: Long = 0
    private var machineStartTime: Long = 0
    private var protectionTime: Int = -1
    private var fossilOwner: PlayerEntity? = null
    val fossilState = FossilState()
    var fossilInventory: MutableList<ItemStack> = mutableListOf<ItemStack>()
    var tubeConnectorDirection: Direction? = null

    //Only updated clientside
    var fillLevel = 0
    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        val stack = player.getStackInHand(interactionHand)

        if (this.createdPokemon != null) {
            if (player !is ServerPlayerEntity) {
                return ActionResult.FAIL
            }

            if (this.fossilOwner != null && player != this.fossilOwner) {
                player.sendMessage(lang("fossilmachine.protected", this.fossilOwner!!.name), true)
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
            this.fossilState.growthState = "Taken"
            player.playSound(CobblemonSounds.FOSSIL_MACHINE_RETRIEVE_POKEMON, SoundCategory.BLOCKS, 1.0F, 1.0F)
            CobblemonCriteria.RESURRECT_POKEMON.trigger(player, createdPokemon!!)

            this.createdPokemon = null
            this.fossilOwner = null
            this.protectionTime = -1
            this.updateFossilType(world)
            this.syncToClient(world)
            this.markDirty(world)
            return ActionResult.SUCCESS
        }

        if (this.isRunning()) {
            return ActionResult.FAIL
        }

        // Reclaim the last fossil from the machine if their hand is empty
        if (player.getStackInHand(interactionHand).isEmpty) {
            if (fossilInventory.isEmpty()) {
                return ActionResult.CONSUME
            }

            player.setStackInHand(interactionHand, fossilInventory.last())

            // remove last fossil in the fossil machine stack when grabbed out of the machine
            this.fossilInventory.removeAt(fossilInventory.size - 1)

            world.playSound(null, compartmentPos, CobblemonSounds.FOSSIL_MACHINE_RETRIEVE_FOSSIL, SoundCategory.BLOCKS)
            this.updateFossilType(world)
            this.syncToClient(world)
            this.markDirty(world)
            return ActionResult.SUCCESS
        }

        // Check if the player is holding a fossil and if so insert it into the machine.
        if (Fossils.isFossilIngredient(stack)) {
            if (fossilInventory.size > Cobblemon.config.maxInsertedFossilItems) {
                return ActionResult.FAIL
            }

            val copyFossilStack = stack.copyWithCount(1)
            if (!player.isCreative) {
                stack?.decrement(1)
            }

            fossilOwner = player
            fossilInventory.add(copyFossilStack)
            this.updateFossilType(world)
            world.playSound(null, compartmentPos, CobblemonSounds.FOSSIL_MACHINE_INSERT_FOSSIL, SoundCategory.BLOCKS)
            this.syncToClient(world)
            this.markDirty(world)
            return ActionResult.SUCCESS
        }

        // Check if the player is holding a natural material and if so, feed it to the machine.
        if (NaturalMaterials.isNaturalMaterial(stack)) {
            if (insertOrganicMaterial(stack, world)) {
                this.lastInteraction = world.time
                if (!player.isCreative) {
                    stack?.decrement(1)
                    player.giveOrDropItemStack(ItemStack(Registries.ITEM.get(NaturalMaterials.getReturnItem(stack))), false)
                }
                return ActionResult.SUCCESS
            }

            return ActionResult.FAIL
        }

        return ActionResult.CONSUME
    }

    public fun spawn(world: World, pos: BlockPos, directionToBehind: Direction, pokemon: Pokemon) : Boolean {
        val world = world ?: return false
        val entity = PokemonEntity(world, pokemon = pokemon)
        entity.calculateDimensions()
        val width = entity.boundingBox.xLength

        val idealPlace = pos.add(directionToBehind.vector.multiply(ceil(width).toInt() + 1))
        var box = entity.getDimensions(EntityPose.STANDING).getBoxAt(idealPlace.toCenterPos().subtract(0.0, 0.5, 0.0))

        for (i in 0..5) {
            box = box.offset(directionToBehind.vector.x.toDouble(), 0.0, directionToBehind.vector.z.toDouble())
            val fixedPosition = makeSuitableY(world, idealPlace.add(directionToBehind.vector), entity, box)
            if (fixedPosition != null) {
                entity.setPosition(fixedPosition.toCenterPos().subtract(0.0, 0.5, 0.0))
                // TODO: Find a correct way to set the new entity's Yaw rotation. (Face away from the machine)
                if (world.spawnEntity(entity)) {
                    return true
                } else {
                    Cobblemon.LOGGER.warn("Couldn't spawn resurrected Pokémon for some reason")
                }
                break
            }
        }
        return false
    }

    fun isSafeFloor(world: World, pos: BlockPos, entity: PokemonEntity): Boolean {
        val state = world.getBlockState(pos)
        return if (state.isAir) {
            false
        } else if (state.hasSolidTopSurface(world, pos, entity) || state.isSolidSurface(world, pos, entity, Direction.DOWN)) {
            true
        } else if ((entity.behaviour.moving.swim.canWalkOnWater || entity.behaviour.moving.swim.canSwimInWater) && state.fluidState.isIn(FluidTags.WATER)) {
            true
        } else {
            (entity.behaviour.moving.swim.canWalkOnLava || entity.behaviour.moving.swim.canSwimInLava) && state.fluidState.isIn(FluidTags.LAVA)
        }
    }

    fun makeSuitableY(world: World, pos: BlockPos, entity: PokemonEntity, box: Box): BlockPos? {
        if (world.canCollide(entity, box)) {
            for (i in 1..15) {
                val newBox = box.offset(0.5, i.toDouble(), 0.5)

                if (!world.canCollide(entity, newBox) && isSafeFloor(world, pos.add(0, i - 1, 0), entity)) {
                    return pos.add(0, i, 0)
                }
            }
        } else {
            for (i in 1..15) {
                val newBox = box.offset(0.5, -i.toDouble(), 0.5)

                if (world.canCollide(entity, newBox) && isSafeFloor(world, pos.add(0, -i, 0), entity)) {
                    return pos.add(0, -i + 1, 0)
                }
            }
        }

        return null
    }

    fun onRedstoneTriggerEvent(world: World, pos: BlockPos) {
        // TODO: Enable additional check once it's possible to hopper in a fossil
        if (this.fossilState.growthState == "Fully Grown" /* && this.protectionTime < 0 */) {
            // instantiate the pokemon as a new entity and spawn it at the location of the machine
            val wildPokemon: Pokemon = this.createdPokemon ?: return
            val monitorEntity = world.getBlockEntity(monitorPos) as MultiblockEntity
            val state = world.getBlockState(monitorEntity.pos)
            val direction = state.get(HorizontalFacingBlock.FACING).getOpposite()
            val success = this.spawn(world, pos, direction, wildPokemon)
            if(success) {
                this.fossilState.growthState = "Taken"
                this.createdPokemon = null
                this.fossilOwner = null
                this.protectionTime = -1
                this.updateFossilType(world)
                this.syncToClient(world)
                this.markDirty(world)
            }
        }
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        val monitorEntity = world.getBlockEntity(monitorPos) as MultiblockEntity
        val compartmentEntity = world.getBlockEntity(compartmentPos) as MultiblockEntity
        val tubeBaseEntity = world.getBlockEntity(tubeBasePos) as MultiblockEntity
        val tubeTopEntity = world.getBlockEntity(tubeBasePos.up()) as MultiblockEntity
        val state = world.getBlockState(monitorEntity.pos)
        val direction = state.get(HorizontalFacingBlock.FACING).getOpposite()
        val wildPokemon: Pokemon? = this.createdPokemon

        monitorEntity.multiblockStructure = null
        compartmentEntity.multiblockStructure = null
        tubeBaseEntity.multiblockStructure = null
        tubeTopEntity.multiblockStructure = null
        monitorEntity.masterBlockPos = null
        compartmentEntity.masterBlockPos = null
        tubeBaseEntity.masterBlockPos = null
        tubeTopEntity.masterBlockPos = null


        // Drop fossils from machine as long as the machine is not running
        if (this.timeRemaining == TIME_TO_TAKE || this.timeRemaining == -1) {
            this.fossilInventory.forEach {
                val stack = ItemStack(it.item, 1)
                ItemScatterer.spawn(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }


        // if the machine is broken while the pokemon is done then spawn the pokemon at the location and make it a wild pokemon
        if (this.fossilState.growthState == "Fully Grown" && wildPokemon != null) {
            //world.createExplosion(this.createdPokemon?.entity, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 5F, World.ExplosionSourceType.TNT)

            // instantiate the pokemon as a new entity and spawn it at the location of the machine
            //var wildPokemon = this.createdPokemon?.sendOut(world as ServerWorld, pos.toVec3d())

            //world.spawnEntity(wildPokemon)
            this.spawn(world, pos, direction, wildPokemon)

        }

        MinecraftClient.getInstance().soundManager.stopSounds(CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP.id, SoundCategory.BLOCKS)

        this.updateFossilType(world)
        this.stopMachine(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    override fun tick(world: World) {
        if (protectionTime > 0) protectionTime--
        if (protectionTime == 0) {
            protectionTime = -1
            this.fossilOwner = null
            world.playSound(null, tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_UNPROTECTED, SoundCategory.BLOCKS)
        }

        if (this.createdPokemon != null) {
            return
        }

        if (this.isRunning() && (world.time - this.machineStartTime) % 160L == 0L) {
            world.playSound(null, this.tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP, SoundCategory.BLOCKS, 1.0F, 1.0F)
        }

        if (this.timeRemaining == -1 && this.organicMaterialInside >= MATERIAL_TO_START && this.resultingFossil != null) {
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
            world.playSound(null, tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundCategory.BLOCKS)
            MinecraftClient.getInstance().soundManager.stopSounds(CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP.id, SoundCategory.BLOCKS)
            fossilInventory.clear()

            this.resultingFossil?.let {
                this.createdPokemon = it.result.create()
            }

            protectionTime = PROTECTION_TIME

            this.stopMachine(world)
        }
    }

    override fun syncToClient(world: World) {
        val tubeBaseEntity = world.getBlockEntity(tubeBasePos) as MultiblockEntity
        val compartmentEntity = world.getBlockEntity(controllerBlockPos) as MultiblockEntity
        val monitorEntity = world.getBlockEntity(monitorPos) as MultiblockEntity

        world.updateListeners(tubeBasePos, tubeBaseEntity.cachedState, tubeBaseEntity.cachedState, Block.NOTIFY_LISTENERS)
        world.updateListeners(compartmentPos, compartmentEntity.cachedState, compartmentEntity.cachedState, Block.NOTIFY_LISTENERS)
        world.updateListeners(monitorPos, monitorEntity.cachedState, monitorEntity.cachedState, Block.NOTIFY_LISTENERS)
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
        this.syncToClient(world)
        this.markDirty(world)
    }

    fun stopMachine(world: World){
        this.fossilState.growthState = "Fully Grown"
        this.timeRemaining = -1
        this.organicMaterialInside = 0

        fossilInventory.clear()

        this.updateOnStatus(world)
        this.updateProgress(world)
        this.syncToClient(world)
        this.markDirty(world)
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
        if (fossilInventory.isEmpty()) {
            if (this.resultingFossil == null) {
                return
            }
            this.resultingFossil = null
        } else {
            this.resultingFossil = Fossils.getFossilByItemStacks(fossilInventory)
        }
    }

    /**
     * Checks if the machine is currently running.
     */
    fun isRunning(): Boolean {
        return this.timeRemaining > 0
    }

    //Returns false if material wasnt inserted
    fun insertOrganicMaterial(stack: ItemStack, world: World): Boolean {
        val natureValue = NaturalMaterials.getContent(stack)
        if (timeRemaining > 0 || this.organicMaterialInside >= MATERIAL_TO_START || natureValue == null) {
            return false
        }
        val oldFillStage = organicMaterialInside / 8

        // to prevent over filling the tank causing a crash
        if ((organicMaterialInside + natureValue) > MATERIAL_TO_START) {
            organicMaterialInside = MATERIAL_TO_START
        }
        else {
            organicMaterialInside += natureValue
        }
        if (this.organicMaterialInside >= MATERIAL_TO_START) {
            world.playSound(null, this.tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_DNA_FULL, SoundCategory.BLOCKS, 1.0F, 1.0F)
        } else if (world.time - this.lastInteraction < 10) {
            world.playSound(null, this.tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_INSERT_DNA_SMALL, SoundCategory.BLOCKS, 1.0F, 1.0F)
        } else {
            world.playSound(null, this.tubeBasePos, CobblemonSounds.FOSSIL_MACHINE_INSERT_DNA, SoundCategory.BLOCKS, 1.0F, 1.0F)
        }
        this.markDirty(world)
        if (oldFillStage != (organicMaterialInside / 8)) {
            this.syncToClient(world)
        }
        return true
    }

    // insert fossil to fossilInventory - returns false if failed
    fun insertFossil(stack: ItemStack, world: World): Boolean {
        // if machine is running or fossil inventory is equal to 3 return false
        if (timeRemaining > 0 || this.fossilInventory.size == 3) {
            return false
        }
        val oldFillStage = this.fossilInventory.size

        //add fossil to the stack in the Compartment
        this.fossilInventory.add(stack)
        world.playSound(null, compartmentPos, CobblemonSounds.FOSSIL_MACHINE_INSERT_FOSSIL, SoundCategory.BLOCKS)

        this.updateFossilType(world)
        this.markDirty(world)
        if (oldFillStage != this.fossilInventory.size) {
            this.syncToClient(world)
        }
        return true
    }

    override fun writeToNbt(): NbtCompound {
        val result = NbtCompound()
        result.put(DataKeys.MONITOR_POS, NbtHelper.fromBlockPos(monitorPos))
        result.put(DataKeys.COMPARTMENT_POS, NbtHelper.fromBlockPos(compartmentPos))
        result.put(DataKeys.TUBE_BASE_POS, NbtHelper.fromBlockPos(tubeBasePos))
        result.putInt(DataKeys.TIME_LEFT, timeRemaining)
        result.putInt(DataKeys.ORGANIC_MATERIAL, organicMaterialInside)
        val fossilInv = NbtList()
        fossilInventory.forEach{ item ->
            fossilInv.add(item.writeNbt(NbtCompound()))
        }
        result.put(DataKeys.FOSSIL_INVENTORY, fossilInv)
        result.putString(DataKeys.CONNECTOR_DIRECTION, tubeConnectorDirection?.toString())

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
        const val PROTECTION_TIME = 6000

        fun fromNbt(nbt: NbtCompound): FossilMultiblockStructure {
            val monitorPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.MONITOR_POS))
            val compartmentPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.COMPARTMENT_POS))
            val tubeBasePos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.TUBE_BASE_POS))

            val result = FossilMultiblockStructure(monitorPos, compartmentPos, tubeBasePos)
            result.organicMaterialInside = nbt.getInt(DataKeys.ORGANIC_MATERIAL)
            result.timeRemaining = nbt.getInt(DataKeys.TIME_LEFT)

            val fossilInv = (nbt.get(DataKeys.FOSSIL_INVENTORY) as NbtList)
            val actualFossilList = mutableListOf<ItemStack>()
            fossilInv.forEach {
                actualFossilList.add(ItemStack.fromNbt(it as NbtCompound))
            }
            result.fossilInventory = actualFossilList
            result.tubeConnectorDirection = Direction.byName(nbt.getString(DataKeys.CONNECTOR_DIRECTION))

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
            result.fillLevel = result.organicMaterialInside / 8
            return result
        }

    }
}