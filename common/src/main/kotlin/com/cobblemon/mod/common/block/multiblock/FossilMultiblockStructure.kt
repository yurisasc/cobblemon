package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.fossil.FossilVariant
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.block.entity.MultiblockEntity
import com.cobblemon.mod.common.block.entity.fossil.FossilMultiblockEntity
import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
import com.cobblemon.mod.common.block.fossilmachine.FossilCompartmentBlock
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.registry.Registries
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FossilMultiblockStructure (
    val monitorPos: BlockPos,
    val compartmentPos: BlockPos,
    val tubeBasePos: BlockPos
) : MultiblockStructure {
    var fossilInside: FossilVariant? = null
    var organicMaterialInside = 0
    var timeRemaining = -1
    override val controllerBlockPos = compartmentPos

    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            //We could potentially use item tags instead of getting the IDs but its nice having everything in one file
            val stack = player.getStackInHand(interactionHand)
            val item = stack?.item
            val itemId = Registries.ITEM.getId(item)
            if (NaturalMaterials.isNaturalMaterial(itemId)) {
                if (timeRemaining > 0) return ActionResult.FAIL
                organicMaterialInside += NaturalMaterials.getContent(itemId) ?: 0
                if (!player.isCreative) {
                    stack?.decrement(1)
                }
                if (organicMaterialInside >= MATERIAL_TO_START) {
                    startMachine(world)
                }
                else {
                    val stage = organicMaterialInside / 8
                    val tubeEntity = world.getBlockEntity(tubeBasePos) as FossilTubeBlockEntity
                    tubeEntity.fillLevel = stage
                    tubeEntity.markDirty()
                    updateTube(world)
                }
                return ActionResult.PASS
            }
        }

        return ActionResult.PASS

    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        stopMachine(world)
        val monitorEntity = world.getBlockEntity(monitorPos) as MultiblockEntity
        val compartmentEntity = world.getBlockEntity(compartmentPos) as MultiblockEntity
        val tubeBaseEntity = world.getBlockEntity(tubeBasePos) as MultiblockEntity
        val tubeTopEntity = world.getBlockEntity(tubeBasePos.up()) as MultiblockEntity

        monitorEntity.multiblockStructure = null
        compartmentEntity.multiblockStructure = null
        tubeBaseEntity.multiblockStructure = null
        tubeTopEntity.multiblockStructure = null
    }

    override fun tick(world: World) {
        if (timeRemaining >= 0) {
            timeRemaining--
        }
        if (timeRemaining == 0) {
            stopMachine(world)
        }
    }

    override fun writeToNbt(): NbtCompound {
        val result = NbtCompound()
        result.put(DataKeys.MONITOR_POS, NbtHelper.fromBlockPos(monitorPos))
        result.put(DataKeys.COMPARTMENT_POS, NbtHelper.fromBlockPos(compartmentPos))
        result.put(DataKeys.TUBE_BASE_POS, NbtHelper.fromBlockPos(tubeBasePos))
        result.putInt(DataKeys.ORGANIC_MATERIAL, organicMaterialInside)
        if (fossilInside != null) {
            result.putString(DataKeys.INSERTED_FOSSIL, fossilInside?.name)
        }
        return result
    }

    fun startMachine(world: World) {
        timeRemaining = 1 * TICKS_PER_MINUTE
        organicMaterialInside = 0
        world.setBlockState(
            compartmentPos,
            world.getBlockState(compartmentPos).with(FossilCompartmentBlock.ON, true)
        )
        world.getBlockEntity(
            tubeBasePos,
            CobblemonBlockEntities.FOSSIL_TUBE
        ).ifPresent {
            it.fillLevel = 8
            it.markDirty()
            updateTube(world)
        }

    }

    fun stopMachine(world: World){
        world.setBlockState(
            compartmentPos,
            world.getBlockState(compartmentPos).with(FossilCompartmentBlock.ON, false)
        )
        world.getBlockEntity(
            tubeBasePos,
            CobblemonBlockEntities.FOSSIL_TUBE
        ).ifPresent {
            it.fillLevel = 0
            it.markDirty()
        }
        val tubeState = world.getBlockState(tubeBasePos)
        world.updateListeners(tubeBasePos, tubeState, tubeState, Block.NOTIFY_LISTENERS)
    }

    fun updateTube(world: World) {
        val tubeState = world.getBlockState(tubeBasePos)
        world.updateListeners(tubeBasePos, tubeState, tubeState, Block.NOTIFY_LISTENERS)
    }

    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { world, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick(world)
            }
        }
        const val TICKS_PER_MINUTE = 1200
        const val MATERIAL_TO_START = 64
        fun fromNbt(nbt: NbtCompound): FossilMultiblockStructure {
            val monitorPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.MONITOR_POS))
            val compartmentPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.COMPARTMENT_POS))
            val tubeBasePos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.TUBE_BASE_POS))
            val result = FossilMultiblockStructure(monitorPos, compartmentPos, tubeBasePos)
            result.organicMaterialInside = nbt.getInt(DataKeys.ORGANIC_MATERIAL)
            if (nbt.contains(DataKeys.INSERTED_FOSSIL)) {
                result.fossilInside = FossilVariant.valueOf(nbt.getString(DataKeys.INSERTED_FOSSIL))
            }
            return result
        }

    }
}