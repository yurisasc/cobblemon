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
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class PokeRodFishingBobberEntity(type: EntityType<out PokeRodFishingBobberEntity>, world: World) : FishingBobberEntity(type, world) {

    private val velocityRandom = Random.create()
    private val caughtFish = false
    private var outOfOpenWaterTicks = 0
    private val field_30665 = 10
    //private val HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
    val HOOK_ENTITY_ID = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
    private val CAUGHT_FISH = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
    private var removalTimer = 0
    private var hookCountdown = 0
    private var waitCountdown = 0
    private var fishTravelCountdown = 0
    private var fishAngle = 0f
    private var inOpenWater = true
    private var hookedEntity: Entity? = null
    private var state = State.FLYING
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
        var positionType = PositionType.INVALID
        for (i in -1..2) {
            val positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2))
            when (positionType2) {
                PositionType.INVALID -> return false
                PositionType.ABOVE_WATER -> if (positionType == PositionType.INVALID) {
                    return false
                }

                PositionType.INSIDE_WATER -> if (positionType == PositionType.ABOVE_WATER) {
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
                    if (positionType == positionType2) positionType else PositionType.INVALID
                }.orElse(PositionType.INVALID)
    }

    private fun getPositionType(pos: BlockPos): PositionType {
        val blockState = world.getBlockState(pos)
        return if (!blockState.isAir && !blockState.isOf(Blocks.LILY_PAD)) {
            val fluidState = blockState.fluidState
            if (fluidState.isIn(FluidTags.WATER) && fluidState.isStill && blockState.getCollisionShape(world, pos).isEmpty) PositionType.INSIDE_WATER else PositionType.INVALID
        } else {
            PositionType.ABOVE_WATER
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
        val serverWorld = world as ServerWorld
        var i = 1
        val blockPos = pos.up()
        if (random.nextFloat() < 0.25f && world.hasRain(blockPos)) {
            ++i
        }
        if (random.nextFloat() < 0.5f && !world.isSkyVisible(blockPos)) {
            --i
        }
        if (this.hookCountdown > 0) {
            --this.hookCountdown
            if (this.hookCountdown <= 0) {
                this.waitCountdown = 0
                this.fishTravelCountdown = 0
                getDataTracker().set(CAUGHT_FISH, false)
            }
        } else if (this.fishTravelCountdown > 0) {
            this.fishTravelCountdown -= i
            if (this.fishTravelCountdown > 0) {
                var j: Double
                var e: Double
                this.fishAngle += random.nextTriangular(0.0, 9.188).toFloat()
                val f = this.fishAngle * (Math.PI.toFloat() / 180)
                val g = MathHelper.sin(f)
                val h = MathHelper.cos(f)
                val d = this.x + (g * this.fishTravelCountdown.toFloat() * 0.1f).toDouble()
                val blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, (MathHelper.floor(this.y).toFloat() + 1.0f).toDouble().also { e = it } - 1.0, this.z + (h * this.fishTravelCountdown.toFloat() * 0.1f).toDouble().also { j = it }))
                if (blockState.isOf(Blocks.WATER)) {
                    if (random.nextFloat() < 0.15f) {
                        serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - 0.1, j, 1, g.toDouble(), 0.1, h.toDouble(), 0.0)
                    }
                    val k = g * 0.04f
                    val l = h * 0.04f
                    serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, l.toDouble(), 0.01, -k.toDouble(), 1.0)
                    serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, -l.toDouble(), 0.01, k.toDouble(), 1.0)
                }
            } else {
                playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (random.nextFloat() - random.nextFloat()) * 0.4f)
                val m = this.y + 0.5
                serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.x, m, this.z, (1.0f + this.width * 20.0f).toInt(), this.width.toDouble(), 0.0, this.width.toDouble(), 0.2)
                serverWorld.spawnParticles(ParticleTypes.FISHING, this.x, m, this.z, (1.0f + this.width * 20.0f).toInt(), this.width.toDouble(), 0.0, this.width.toDouble(), 0.2)
                this.hookCountdown = MathHelper.nextInt(random, 20, 40)
                getDataTracker().set(CAUGHT_FISH, true)
            }
        } else if (this.waitCountdown > 0) {
            this.waitCountdown -= i
            var f = 0.15f
            if (this.waitCountdown < 20) {
                f += (20 - this.waitCountdown).toFloat() * 0.05f
            } else if (this.waitCountdown < 40) {
                f += (40 - this.waitCountdown).toFloat() * 0.02f
            } else if (this.waitCountdown < 60) {
                f += (60 - this.waitCountdown).toFloat() * 0.01f
            }
            if (random.nextFloat() < f) {
                var j: Double
                var e: Double
                val g = MathHelper.nextFloat(random, 0.0f, 360.0f) * (Math.PI.toFloat() / 180)
                val h = MathHelper.nextFloat(random, 25.0f, 60.0f)
                val d = this.x + (MathHelper.sin(g) * h).toDouble() * 0.1
                val blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, (MathHelper.floor(this.y).toFloat() + 1.0f).toDouble().also { e = it } - 1.0, this.z + (MathHelper.cos(g) * h).toDouble() * 0.1.also { j = it }))
                if (blockState.isOf(Blocks.WATER)) {
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + random.nextInt(2), 0.1, 0.0, 0.1, 0.0)
                }
            }
            if (this.waitCountdown <= 0) {
                this.fishAngle = MathHelper.nextFloat(random, 0.0f, 360.0f)
                this.fishTravelCountdown = MathHelper.nextInt(random, 20, 80)
            }
        } else {
            this.waitCountdown = MathHelper.nextInt(random, 100, 600)
            this.waitCountdown -= this.lureLevel * 20 * 5
        }
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
        //super.tick()
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
            if (state == State.FLYING) {
                if (hookedEntity != null) {
                    velocity = Vec3d.ZERO
                    state = State.HOOKED_IN_ENTITY
                    return
                }
                if (bl) {
                    velocity = velocity.multiply(0.3, 0.2, 0.3)
                    state = State.BOBBING
                    return
                }
                checkForCollision()
            } else {
                if (state == State.HOOKED_IN_ENTITY) {
                    if (hookedEntity != null) {
                        if (!hookedEntity!!.isRemoved && hookedEntity!!.world.registryKey === world.registryKey) {
                            this.setPosition(hookedEntity!!.x, hookedEntity!!.getBodyY(0.8), hookedEntity!!.z)
                        } else {
                            updateHookedEntityId(null as Entity?)
                            state = State.FLYING
                        }
                    }
                    return
                }
                if (state == State.BOBBING) {
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
            if (state == State.FLYING && (this.isOnGround || horizontalCollision)) {
                velocity = Vec3d.ZERO
            }
            val e = 0.92 // air friction??
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
