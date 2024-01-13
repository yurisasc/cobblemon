package com.cobblemon.mod.common.entity.fishing

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.CobblemonItems
import com.mojang.logging.LogUtils
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.MovementType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.registry.tag.FluidTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class PokeRodFishingBobberEntity(type: EntityType<out PokeRodFishingBobberEntity>, world: World) : FishingBobberEntity(type, world) {

    private val LOGGER = LogUtils.getLogger()
    private val velocityRandom = Random.create()
    private val caughtFish = false
    private var outOfOpenWaterTicks = 0
    private val field_30665 = 10
    //private val HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
    val HOOK_ENTITY_ID = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
    private val CAUGHT_FISH = DataTracker.registerData(FishingBobberEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
    private var removalTimer = 0
    private val hookCountdown = 0
    private val waitCountdown = 0
    private val fishTravelCountdown = 0
    private val fishAngle = 0f
    private var inOpenWater = true
    private var hookedEntity: Entity? = null
    private var state = PokeRodFishingBobberEntity.State.FLYING
    private val luckOfTheSeaLevel = 0
    private val lureLevel = 0

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

    private fun removeIfInvalid(player: PlayerEntity): Boolean {
        val itemStack = player.mainHandStack
        val itemStack2 = player.offHandStack
        val bl = itemStack.isOf(CobblemonItems.POKEROD)
        val bl2 = itemStack2.isOf(CobblemonItems.POKEROD)
        if (player.isRemoved || !player.isAlive || !bl && !bl2 || this.squaredDistanceTo(player) > 1024.0) {
            discard()
            return true
        }
        return false
    }

    private fun checkForCollision() {
        val hitResult = ProjectileUtil.getCollision(this) { entity: Entity? -> this.canHit(entity) }
        onCollision(hitResult)
    }

    private fun updateHookedEntityId(entity: Entity?) {
        this.hookedEntity = entity
        getDataTracker().set(HOOK_ENTITY_ID, if (entity == null) 0 else entity.id + 1)
    }

    override fun tick() {
        velocityRandom.setSeed(getUuid().leastSignificantBits xor world.time)
        super.tick()
        val playerEntity = this.playerOwner
        if (playerEntity == null) {
            discard()
        } else if (world.isClient || !removeIfInvalid(playerEntity)) {
            if (this.isOnGround) {
                ++removalTimer
                if (removalTimer >= 1200) {
                    discard()
                    return
                }
            } else {
                removalTimer = 0
            }
            var f = 0.0f
            val blockPos = blockPos
            val fluidState = world.getFluidState(blockPos)
            if (fluidState.isIn(FluidTags.WATER)) {
                f = fluidState.getHeight(world, blockPos)
            }
            val bl = f > 0.0f
            if (state == PokeRodFishingBobberEntity.State.FLYING) {
                if (hookedEntity != null) {
                    velocity = Vec3d.ZERO
                    state = PokeRodFishingBobberEntity.State.HOOKED_IN_ENTITY
                    return
                }
                if (bl) {
                    velocity = velocity.multiply(0.3, 0.2, 0.3)
                    state = PokeRodFishingBobberEntity.State.BOBBING
                    return
                }
                checkForCollision()
            } else {
                if (state == PokeRodFishingBobberEntity.State.HOOKED_IN_ENTITY) {
                    if (hookedEntity != null) {
                        if (!hookedEntity!!.isRemoved && hookedEntity!!.world.registryKey === world.registryKey) {
                            this.setPosition(hookedEntity!!.x, hookedEntity!!.getBodyY(0.8), hookedEntity!!.z)
                        } else {
                            updateHookedEntityId(null as Entity?)
                            state = PokeRodFishingBobberEntity.State.FLYING
                        }
                    }
                    return
                }
                if (state == PokeRodFishingBobberEntity.State.BOBBING) {
                    val vec3d = velocity
                    var d = this.y + vec3d.y - blockPos.y.toDouble() - f.toDouble()
                    if (Math.abs(d) < 0.01) {
                        d += Math.signum(d) * 0.1
                    }
                    this.setVelocity(vec3d.x * 0.9, vec3d.y - d * random.nextFloat().toDouble() * 0.2, vec3d.z * 0.9)
                    if (hookCountdown <= 0 && fishTravelCountdown <= 0) {
                        inOpenWater = true
                    } else {
                        inOpenWater = inOpenWater && outOfOpenWaterTicks < 10 && isOpenOrWaterAround(blockPos)
                    }
                    if (bl) {
                        outOfOpenWaterTicks = Math.max(0, outOfOpenWaterTicks - 1)
                        if (caughtFish) {
                            velocity = velocity.add(0.0, -0.1 * velocityRandom.nextFloat().toDouble() * velocityRandom.nextFloat().toDouble(), 0.0)
                        }
                        if (!world.isClient) {
                            tickFishingLogic(blockPos)
                        }
                    } else {
                        outOfOpenWaterTicks = Math.min(10, outOfOpenWaterTicks + 1)
                    }
                }
            }
            if (!fluidState.isIn(FluidTags.WATER)) {
                velocity = velocity.add(0.0, -0.03, 0.0)
            }
            move(MovementType.SELF, velocity)
            this.updateRotation()
            if (state == PokeRodFishingBobberEntity.State.FLYING && (this.isOnGround || horizontalCollision)) {
                velocity = Vec3d.ZERO
            }
            val e = 0.92
            velocity = velocity.multiply(0.92)
            refreshPosition()
        }
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
