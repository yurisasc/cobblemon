package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.api.fossil.FossilVariant
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.entity.MultiblockEntity
import com.cobblemon.mod.common.util.DataKeys
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
    val ticksPerMinute = 1200
    var fossilInside: FossilVariant? = null
    var organicMaterialInside = 0
    var timeRemaining = ticksPerMinute * 5
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
                organicMaterialInside += NaturalMaterials.getContent(itemId) ?: 0
                if (!player.isCreative) {
                    stack?.decrement(1)
                }
                return ActionResult.PASS
            }
        }

        return ActionResult.PASS

    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        val monitorEntity = world.getBlockEntity(monitorPos) as MultiblockEntity
        val compartmentEntity = world.getBlockEntity(compartmentPos) as MultiblockEntity
        val tubeBaseEntity = world.getBlockEntity(tubeBasePos) as MultiblockEntity
        val tubeTopEntity = world.getBlockEntity(tubeBasePos.up()) as MultiblockEntity

        monitorEntity.multiblockStructure = null
        compartmentEntity.multiblockStructure = null
        tubeBaseEntity.multiblockStructure = null
        tubeTopEntity.multiblockStructure = null
    }

    override fun tick() {
        if (timeRemaining > 0) {
            timeRemaining--
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

    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { _, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick()
            }
        }
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