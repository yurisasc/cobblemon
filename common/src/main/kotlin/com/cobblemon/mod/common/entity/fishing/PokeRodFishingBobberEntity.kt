package com.cobblemon.mod.common.entity.fishing

import com.cobblemon.mod.common.CobblemonEntities
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.registry.tag.FluidTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class PokeRodFishingBobberEntity(type: EntityType<out PokeRodFishingBobberEntity>, world: World) : FishingBobberEntity(type, world) {

    constructor(thrower: PlayerEntity, world: World, luckOfTheSeaLevel: Int, lureLevel: Int) : this(CobblemonEntities.POKE_BOBBER, world) {
        // Copy pasta a LOT
        //this(CobblemonEntities.POKE_BOBBER, world, luckOfTheSeaLevel, lureLevel)
        owner = thrower
        val f = thrower.pitch
        val g = thrower.yaw
        val h = MathHelper.cos(-g * 0.017453292f - 3.1415927f)
        val i = MathHelper.sin(-g * 0.017453292f - 3.1415927f)
        val j = -MathHelper.cos(-f * 0.017453292f)
        val k = MathHelper.sin(-f * 0.017453292f)
        val d = thrower.x - i.toDouble() * 0.3
        val e = thrower.eyeY
        val l = thrower.z - h.toDouble() * 0.3
        this.refreshPositionAndAngles(d, e, l, g, f)
        var vec3d = Vec3d((-i).toDouble(), MathHelper.clamp(-(k / j), -5.0f, 5.0f).toDouble(), (-h).toDouble())
        val m = vec3d.length()
        vec3d = vec3d.multiply(0.6 / m + random.nextTriangular(0.5, 0.0103365), 0.6 / m + random.nextTriangular(0.5, 0.0103365), 0.6 / m + random.nextTriangular(0.5, 0.0103365))
        velocity = vec3d
        yaw = (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875).toFloat()
        pitch = (MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875).toFloat()
        prevYaw = yaw
        prevPitch = pitch
    }

    fun isOpenOrWaterAround(pos: BlockPos): Boolean {
        var positionType = PokeRodFishingBobberEntity.PositionType.INVALID
        for (i in -1..2) {
            val positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2))
            when (positionType2) {
                PokeRodFishingBobberEntity.PositionType.INVALID -> return false
                PokeRodFishingBobberEntity.PositionType.ABOVE_WATER -> if (positionType == PokeRodFishingBobberEntity.PositionType.INVALID) {
                    return false
                }

                PokeRodFishingBobberEntity.PositionType.INSIDE_WATER -> if (positionType == PokeRodFishingBobberEntity.PositionType.ABOVE_WATER) {
                    return false
                }

                else -> return false
            }
            positionType = positionType2
        }
        return true
    }

    /*private fun getPositionType(start: BlockPos, end: BlockPos): PokeRodFishingBobberEntity.PositionType? {
        return BlockPos.stream(start, end).map { pos: BlockPos? -> this.getPositionType(pos!!) }.reduce { positionType: PokeRodFishingBobberEntity.PositionType, positionType2: PokeRodFishingBobberEntity.PositionType -> if (positionType == positionType2) positionType else PokeRodFishingBobberEntity.PositionType.INVALID } as ((PositionType?, PositionType?) -> PositionType?)?.orElse(PokeRodFishingBobberEntity.PositionType.INVALID) as PokeRodFishingBobberEntity.PositionType
    }*/

    private fun getPositionType(start: BlockPos, end: BlockPos): PositionType? {
        return BlockPos.stream(start, end)
                .map { pos -> this.getPositionType(pos) }
                .reduce { positionType, positionType2 ->
                    if (positionType == positionType2) positionType else PokeRodFishingBobberEntity.PositionType.INVALID
                }.orElse(PokeRodFishingBobberEntity.PositionType.INVALID)
    }

    private fun getPositionType(pos: BlockPos): PokeRodFishingBobberEntity.PositionType {
        val blockState = world.getBlockState(pos)
        return if (!blockState.isAir && !blockState.isOf(Blocks.LILY_PAD)) {
            val fluidState = blockState.fluidState
            if (fluidState.isIn(FluidTags.WATER) && fluidState.isStill && blockState.getCollisionShape(world, pos).isEmpty) PokeRodFishingBobberEntity.PositionType.INSIDE_WATER else PokeRodFishingBobberEntity.PositionType.INVALID
        } else {
            PokeRodFishingBobberEntity.PositionType.ABOVE_WATER
        }
    }

    enum class PositionType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID
    }

    private fun setPlayerFishHook(fishingBobber: FishingBobberEntity?) {
        val playerEntity = this.playerOwner
        if (playerEntity != null) {
            playerEntity.fishHook = fishingBobber
        }
    }

    internal enum class State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING
    }

    // todo custom behavior for fishing logic
    private fun tickFishingLogic(pos: BlockPos) {

    }

}

/*class PokeBobberEntity : FishingBobberEntity {

    constructor(entityType: EntityType<out FishingBobberEntity>, world: World) :
            super(entityType, world)

    constructor(player: PlayerEntity, world: World, luckOfTheSeaLevel: Int, lureLevel: Int) :
            super(player, world, luckOfTheSeaLevel, lureLevel)

    constructor(entityType: EntityType<out FishingBobberEntity>, world: World, luckOfTheSeaLevel: Int, lureLevel: Int) :
            super(entityType, world)


    override fun tick() {
        super.tick()
        // todo add more logic per tick here
    }

    // todo add new methods
}*/
