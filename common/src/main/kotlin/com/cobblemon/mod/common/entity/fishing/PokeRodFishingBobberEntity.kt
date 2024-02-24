/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.fishing

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.stats.*
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.spawning.*
import com.cobblemon.mod.common.api.spawning.detail.EntitySpawnResult
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.interactive.PokerodItem
import com.cobblemon.mod.common.loot.CobblemonLootTables
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Nature
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility
import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty
import com.cobblemon.mod.common.util.commandLang
import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Blocks
import net.minecraft.entity.*
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.FluidTags
import net.minecraft.registry.tag.ItemTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.*
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import kotlin.math.sqrt

class PokeRodFishingBobberEntity(type: EntityType<out PokeRodFishingBobberEntity>, world: World) : FishingBobberEntity(type, world) {

    private val velocityRandom = Random.create()
    private val caughtFish = false
    private var outOfOpenWaterTicks = 0

    // todo is this needed?
    //val HOOK_ENTITY_ID = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
    //private val CAUGHT_FISH = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

    // todo if so replace these later
    var HOOK_ENTITY_ID = 0
    var CAUGHT_FISH = false

    private var removalTimer = 0
    private var hookCountdown = 0
    private var waitCountdown = 0
    private var fishTravelCountdown = 0
    private var fishAngle = 0f
    var inOpenWater = true
    private var hookedEntity: Entity? = null
    var state = State.FLYING
    private var luckOfTheSeaLevel = 0
    private var lureLevel = 0
    private var typeCaught= "ITEM"
    private var chosenBucket = Cobblemon.bestSpawner.config.buckets[0] // default to first rarity bucket
    private val pokemonSpawnChance = 85 // chance a Pokemon will be fished up % out of 100
    var pokeRodId: Identifier? = null
    var lineColor: String = "000000" // default line color is black
    var usedRod: Identifier? = null
    var bobberBait: ItemStack = ItemStack.EMPTY

    constructor(thrower: PlayerEntity, pokeRodId: Identifier, bait: ItemStack, world: World, luckOfTheSea: Int, lure: Int) : this(CobblemonEntities.POKE_BOBBER, world) {
        owner = thrower
        luckOfTheSeaLevel = luckOfTheSea
        lureLevel = lure
        this.pokeRodId = pokeRodId
        this.bobberBait = bait
        dataTracker.set(POKEROD_ID, pokeRodId.toString() ?: "")
        dataTracker.set(POKEBOBBER_BAIT, bobberBait ?: ItemStack.EMPTY)
        this.usedRod = pokeRodId


        val throwerPitch = thrower.pitch
        val throwerYaw = thrower.yaw
        val cosYaw = MathHelper.cos(-throwerYaw * 0.017453292f - 3.1415927f)
        val sinYaw = MathHelper.sin(-throwerYaw * 0.017453292f - 3.1415927f)
        val cosPitch = -MathHelper.cos(-throwerPitch * 0.017453292f)
        val sinPitch = MathHelper.sin(-throwerPitch * 0.017453292f)
        val posX = thrower.x - sinYaw.toDouble() * 0.3
        val posY = thrower.eyeY
        val posZ = thrower.z - cosYaw.toDouble() * 0.3
        this.refreshPositionAndAngles(posX, posY, posZ, throwerYaw, throwerPitch)
        var vec3d = Vec3d((-sinYaw).toDouble(), MathHelper.clamp(-(sinPitch / cosPitch), -5.0f, 5.0f).toDouble(), (-cosYaw).toDouble())
        val m = vec3d.length()
        vec3d = vec3d.multiply(0.6 / m + random.nextTriangular(0.5, 0.0103365), 0.6 / m + random.nextTriangular(0.5, 0.0103365), 0.6 / m + random.nextTriangular(0.5, 0.0103365))
        velocity = vec3d
        yaw = (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875).toFloat()
        pitch = (MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875).toFloat()
        prevYaw = yaw
        prevPitch = pitch
    }

    override fun initDataTracker() {
        this.dataTracker.startTracking(POKEROD_ID, pokeRodId.toString() ?: "")
        this.dataTracker.startTracking(POKEBOBBER_BAIT, bobberBait ?: ItemStack.EMPTY)
    }

    fun calculateMinMaxCountdown(weight: Float): Pair<Int, Int> {
        // Constants for the target min and max values at weight extremes
        val minAtMaxWeight = 20
        val maxAtMaxWeight = 40
        val minAtMinWeight = 15
        val maxAtMinWeight = 20

        // Calculate factors for min and max based on the weight
        val minFactor = ((minAtMaxWeight - minAtMinWeight) / 100f) * weight + minAtMinWeight
        val maxFactor = ((maxAtMaxWeight - maxAtMinWeight) / 100f) * weight + maxAtMinWeight

        // Ensure min and max are within the needed bounds
        val min = minFactor.toInt().coerceIn(minAtMinWeight, minAtMaxWeight)
        val max = maxFactor.toInt().coerceIn(maxAtMinWeight, maxAtMaxWeight)

        return Pair(min, max)
    }

    fun chooseAdjustedSpawnBucket(buckets: List<SpawnBucket>, luckOfTheSeaLevel: Int): SpawnBucket {
        val baseIncreases = listOf(5.0F, 1.0F, 0.2F)  // Base increases for the first three buckets beyond the first
        val adjustedWeights = buckets.mapIndexed { index, bucket ->
            if (index == 0) {
                // Placeholder, will be recalculated
                0.0F
            } else {
                val increase = if (index < baseIncreases.size) baseIncreases[index] else baseIncreases.last() + (index - baseIncreases.size + 1) * 0.15F
                bucket.weight + increase * luckOfTheSeaLevel
            }
        }.toMutableList()

        // Recalculate the first bucket's weight to ensure the total is 100%
        val totalAdjustedWeight = adjustedWeights.sum() - adjustedWeights[0]  // Corrected to ensure the list contains Floats
        adjustedWeights[0] = 100.0F - totalAdjustedWeight + buckets[0].weight

        // Random selection based on adjusted weights
        val weightSum = adjustedWeights.sum()
        val chosenSum = kotlin.random.Random.nextDouble(weightSum.toDouble()).toFloat()  // Ensure usage of Random from kotlin.random package
        var sum = 0.0F
        adjustedWeights.forEachIndexed { index, weight ->
            sum += weight
            if (sum >= chosenSum) {
                return buckets[index]
            }
        }

        return buckets.first()  // Fallback
    }

    /*// make more generic version for other enchants. Pass in id of enchant
    fun getEnchantLevel(enchantment: Enchantment): Int {
        var pokerodEnchantments: NbtList? = null
        if (owner?.isPlayer == true) {
            pokerodEnchantments = (owner as PlayerEntity).mainHandStack?.enchantments
        }

        val luckOfTheSeaEnchantment = pokerodEnchantments?.filter {
            if (it is NbtCompound)
                if (it.getString("id") == Registries.ENCHANTMENT.getEntry(enchantment).key.toString())
                    return@filter true
            return@filter false
        }?.first()

        return (luckOfTheSeaEnchantment as? NbtCompound)?.getShort("lvl")?.toInt() ?: 0
    }*/

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

    enum class State {
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
                this.CAUGHT_FISH = false
                //getDataTracker().set(CAUGHT_FISH, false)
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
                //playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (random.nextFloat() - random.nextFloat()) * 0.4f)
                // play bobber hook notification sound
                world.playSound(null, this.blockPos, CobblemonSounds.FISHING_NOTIFICATION, SoundCategory.BLOCKS, 1.0F, 1.0F)

                val m = this.y + 0.5
                serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.x, m, this.z, (1.0f + this.width * 20.0f).toInt(), this.width.toDouble(), 0.0, this.width.toDouble(), 0.2)
                serverWorld.spawnParticles(ParticleTypes.FISHING, this.x, m, this.z, (1.0f + this.width * 20.0f).toInt(), this.width.toDouble(), 0.0, this.width.toDouble(), 0.2)

                // check for chance to catch pokemon based on the bait
                if (MathHelper.nextInt(random, 0, 100) < getPokemonSpawnChance(bobberBait)) {
                    this.typeCaught = "POKEMON"

                    val buckets = Cobblemon.bestSpawner.config.buckets

                    // choose a spawn bucket according to weights no matter how many there are
                    chosenBucket = chooseAdjustedSpawnBucket(buckets, luckOfTheSeaLevel)
                    val reactionMinMax = calculateMinMaxCountdown(chosenBucket.weight)

                    // set the hook reaction time to be based off the rarity of the bucket chosen
                    this.hookCountdown = MathHelper.nextInt(random, reactionMinMax.first, reactionMinMax.second)

                    println("Player hooked a Pokemon from the bucket: " + chosenBucket.name)

                }
                else {
                    // todo caught item
                    this.typeCaught = "ITEM"
                    this.hookCountdown = MathHelper.nextInt(random, 20, 40)

                }

                //this.hookCountdown = MathHelper.nextInt(random, 20, 40)
                //getDataTracker().set(CAUGHT_FISH, true)
                this.CAUGHT_FISH = true
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
            // set the time it takes to wait for a hooked item or pokemon
            this.waitCountdown = MathHelper.nextInt(random, 100, 600)
            this.waitCountdown -= this.lureLevel * 20 * 5

            // check for the bait on the hook and see if the waitCountdown is reduced
            if (checkReduceBiteTime(bobberBait))
                this.waitCountdown = alterBiteTimeAttempt(this.waitCountdown, this.bobberBait)

        }
    }

    private fun removeIfInvalid(player: PlayerEntity): Boolean {
        val itemStack = player.mainHandStack
        val itemStack2 = player.offHandStack
        val bl = Registries.ITEM[this.usedRod] == itemStack.item //(itemStack.item is PokerodItem) // todo make this work again so the line breaks when you swap items
        val bl2 = Registries.ITEM[this.usedRod] == itemStack2.item //(itemStack2.item is PokerodItem) // todo make this work again so the line breaks when you swap items
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
        //getDataTracker().set(HOOK_ENTITY_ID, if (entity == null) 0 else entity.id + 1)
        this.HOOK_ENTITY_ID = if (entity == null) 0 else entity.id + 1
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
                    inOpenWater = if (hookCountdown <= 0 && fishTravelCountdown <= 0) {
                        true
                    } else {
                        inOpenWater && outOfOpenWaterTicks < 10 && isOpenOrWaterAround(blockPos)
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
            velocity = velocity.multiply(0.92)
            refreshPosition()
        }
    }

    override fun use(usedItem: ItemStack): Int {
        val playerEntity = this.playerOwner

        return if (!world.isClient && playerEntity != null && !removeIfInvalid(playerEntity)) {
            var i = 0
            if (this.hookedEntity != null) {
                pullHookedEntity(this.hookedEntity)
                Criteria.FISHING_ROD_HOOKED.trigger(playerEntity as ServerPlayerEntity?, usedItem, this, emptyList())
                world.sendEntityStatus(this, 31.toByte())
                i = if (this.hookedEntity is ItemEntity) 3 else 5
            } else if (this.hookCountdown > 0) {
                // check if thing caught was an item
                if (this.typeCaught == "ITEM") {
                    val lootContextParameterSet = LootContextParameterSet.Builder(world as ServerWorld).add(LootContextParameters.ORIGIN, pos).add(LootContextParameters.TOOL, usedItem).add(LootContextParameters.THIS_ENTITY, this).luck(this.luckOfTheSeaLevel.toFloat() + playerEntity.luck).build(LootContextTypes.FISHING)
                    val lootTable = world.server!!.lootManager.getLootTable(CobblemonLootTables.FISHING_GAMEPLAY)
                    val list: List<ItemStack> = lootTable.generateLoot(lootContextParameterSet)
                    Criteria.FISHING_ROD_HOOKED.trigger(playerEntity as ServerPlayerEntity?, usedItem, this, list)
                    val var7: Iterator<*> = list.iterator()
                    while (var7.hasNext()) {
                        val itemStack = var7.next() as ItemStack
                        val itemEntity = ItemEntity(world, this.x, this.y, this.z, itemStack)
                        val d = playerEntity.getX() - this.x
                        val e = playerEntity.getY() - this.y
                        val f = playerEntity.getZ() - this.z
                        itemEntity.setVelocity(d * 0.1, e * 0.1 + sqrt(sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1)
                        world.spawnEntity(itemEntity)
                        playerEntity.getWorld().spawnEntity(ExperienceOrbEntity(playerEntity.getWorld(), playerEntity.getX(), playerEntity.getY() + 0.5, playerEntity.getZ() + 0.5, random.nextInt(6) + 1))
                        if (itemStack.isIn(ItemTags.FISHES)) {
                            playerEntity.increaseStat(Stats.FISH_CAUGHT, 1)
                        }
                    }
                    i = 1
                }
                else { // logic for spawning Pokemon using rarity

                    System.out.println("Player reeled in a Pokemon from the bucket: " + chosenBucket.name)

                    val bobberOwner = playerOwner as ServerPlayerEntity

                    // spawn the pokemon from the chosen bucket at the bobber's location
                    spawnPokemonFromFishing(bobberOwner, chosenBucket, bobberBait)

                    val serverWorld = world as ServerWorld

                    val g = MathHelper.nextFloat(random, 0.0f, 360.0f) * (Math.PI.toFloat() / 180)
                    val h = MathHelper.nextFloat(random, 25.0f, 60.0f)
                    val partX = this.x + (MathHelper.sin(g) * h).toDouble() * 0.1
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, partX, this.y, this.z, 4 + random.nextInt(4), 0.1, 0.0, 0.1, 0.0)
                    playerEntity.getWorld().spawnEntity(ExperienceOrbEntity(playerEntity.getWorld(), playerEntity.getX(), playerEntity.getY() + 0.5, playerEntity.getZ() + 0.5, random.nextInt(6) + 1))
                }
            }
            if (this.isOnGround) {
                i = 2
            }
            discard()
            i
        } else {
            0
        }
    }

    fun spawnPokemonFromFishing(player: PlayerEntity, chosenBucket: SpawnBucket, bobberBait: ItemStack) {
        var hookedEntityID: Int? = null
        
        val spawner = BestSpawner.fishingSpawner

        //val spawnCause = SpawnCause(spawner = spawner, bucket = spawner.chooseBucket(), entity = spawner.getCauseEntity())
        val spawnCause = FishingSpawnCause(
            spawner = spawner,
            bucket = chosenBucket,
            entity = player,
            rodStack = player.mainHandStack // Crab, you should probably parse in the rod item connected to the bobber so we can check enchants in spawn conditions
        )

        val result = spawner.run(spawnCause, world as ServerWorld, pos.toBlockPos())

        if (result == null) {
        // This has a chance to fail, if the position has no suitability for a fishing context
        //  it could also just be a miss which
        //   means two attempts to spawn in the same location can have differing results (which is expected for
        //   randomness).
            player.sendMessage("Not even a nibble".red())
        }

        var spawnedPokemon: PokemonEntity? = null
        val resultingSpawn = result?.get()

        if (resultingSpawn is EntitySpawnResult) {
            for (entity in resultingSpawn.entities) {
                //pokemon.uuid = it.uuid
                hookedEntityID = entity.id
                spawnedPokemon = (entity as PokemonEntity)

                // todo Check if there was a berry used on the bobber and if so then see how it may affect the pokemon
                if (bobberBait != ItemStack.EMPTY) {
                    // look up how mints affect the pokemon

                    modifyPokemonWithBait(spawnedPokemon, bobberBait) // check to see if the spawned pokemon gets modified due to the bait used





                    // remove the bait from the bobber
                    val playerPokerod = if (this.playerOwner?.getStackInHand(Hand.MAIN_HAND)?.item is PokerodItem) this.playerOwner!!.getStackInHand(Hand.MAIN_HAND).item else this.playerOwner!!.getStackInHand(Hand.OFF_HAND).item
                    (playerPokerod as PokerodItem).bait = ItemStack.EMPTY
                }


                if (spawnedPokemon.pokemon.species.weight.toDouble() < 200.0) { // if weight value of Pokemon is less than 200 then reel it in to the player
                    // play sound for small splash when this weight class is fished up
                    world.playSound(null, this.blockPos, CobblemonSounds.FISHING_SPLASH_SMALL_1, SoundCategory.BLOCKS, 1.0F, 1.0F)
                    //world.playSound(null, this.blockPos, CobblemonSounds.FISHING_SPLASH_SMALL_2, SoundCategory.BLOCKS, 1.0F, 1.0F)
                    //world.playSound(null, this.blockPos, CobblemonSounds.FISHING_SPLASH_SMALL_3, SoundCategory.BLOCKS, 1.0F, 1.0F)

                    // direction and position
                    val rad = Math.toRadians(player.yaw.toDouble() + 180)
                    val behindDirection = Vec3d(-Math.sin(rad), 0.0, Math.cos(rad))
                    val targetPos = player.pos.add(behindDirection.multiply(2.0))
                    val diff = targetPos.subtract(entity.pos)
                    val distance = diff.horizontalLength()

                    // variables for velocity adjustments
                    val baseVelocity: Double
                    val velocityIncreasePerBlock: Double
                    val baseArc: Double
                    val arcIncreasePerBlock: Double

                    // velocity based on distance
                    when {
                        distance < 5 -> { // distances under 10 blocks be a bit softer launch
                            baseVelocity = 0.05
                            velocityIncreasePerBlock = 0.02
                            baseArc = 0.35
                            arcIncreasePerBlock = 0.015
                        }
                        distance < 10 -> { // distances under 10 blocks be a bit softer launch
                            baseVelocity = 0.3
                            velocityIncreasePerBlock = 0.03
                            baseArc = 0.35
                            arcIncreasePerBlock = 0.015
                        }
                        distance <= 20 -> { // distances over 20 blocks be a bit harsher launch
                            baseVelocity = 0.4
                            velocityIncreasePerBlock = 0.04
                            baseArc = 0.4
                            arcIncreasePerBlock = 0.02
                        }
                        else -> { // baseline launch for between 10 and 20 block distance
                            baseVelocity = 0.5
                            velocityIncreasePerBlock = 0.06
                            baseArc = 0.45
                            arcIncreasePerBlock = 0.025
                        }
                    }

                    // Calculate velocities with adjusted factors
                    val velocityX = diff.x / distance * (baseVelocity + distance * velocityIncreasePerBlock)
                    val velocityZ = diff.z / distance * (baseVelocity + distance * velocityIncreasePerBlock)
                    val velocityY = baseArc + distance * arcIncreasePerBlock

                    val tossVelocity = Vec3d(velocityX, velocityY, velocityZ)
                    entity.setVelocity(tossVelocity)
                    //entity.pokemon.aspects
                }
                else { // it is a big lad
                    world.playSound(null, this.blockPos, CobblemonSounds.FISHING_SPLASH_BIG_1, SoundCategory.BLOCKS, 1.0F, 1.0F)
                    //world.playSound(null, this.blockPos, CobblemonSounds.FISHING_SPLASH_BIG_2, SoundCategory.BLOCKS, 1.0F, 1.0F)
                    //world.playSound(null, this.blockPos, CobblemonSounds.FISHING_SPLASH_BIG_3, SoundCategory.BLOCKS, 1.0F, 1.0F)
                }
            }
        }

        // What if it isn't an entity though
        hookedEntity = world.getEntityById(hookedEntityID!!)

        //val spawnedPokemon = spawnAction.entity
        if (spawnedPokemon != null) {
            BattleBuilder.pve((player as ServerPlayerEntity), spawnedPokemon).ifErrored { it.sendTo(player) { it.red() } }
        }
    }

    // used to store the % chance increase or the value increase of certain baits
    fun getBerryBaitBonusChance(bait: ItemStack, chanceBoost: String): Double {
        val berryBoosts = mapOf(
                // "cheri_berry" to mapOf("Nature_Att" to 0.25, "Nature_Def" to 0.10),  EXAMPLE of multiple entries per berry

                // Attract Natures Tier 1 - 25%
                "cheri_berry" to mapOf("Nature_Att" to 0.25),
                "razz_berry" to mapOf("Nature_Att" to 0.25),
                "chesto_berry" to mapOf("Nature_SpA" to 0.25),
                "bluk_berry" to mapOf("Nature_SpA" to 0.25),
                "aspear_berry" to mapOf("Nature_Def" to 0.25),
                "pinap_berry" to mapOf("Nature_Def" to 0.25),
                "wepear_berry" to mapOf("Nature_SpD" to 0.25),
                "rawst_berry" to mapOf("Nature_SpD" to 0.25),
                "pecha_berry" to mapOf("Nature_Spe" to 0.25),
                "nanab_berry" to mapOf("Nature_Spe" to 0.25),

                // Attract Natures Tier 2 - 50%
                "figy_berry" to mapOf("Nature_Att" to 0.50),
                "wiki_berry" to mapOf("Nature_SpA" to 0.50),
                "iapapa_berry" to mapOf("Nature_Def" to 0.50),
                "aguav_berry" to mapOf("Nature_SpD" to 0.50),
                "mago_berry" to mapOf("Nature_Spe" to 0.50),

                // Attract Natures Tier 3 - 75%
                "touga_berry" to mapOf("Nature_Att" to 0.75),
                "cornn_berry" to mapOf("Nature_SpA" to 0.75),
                "rabuta_berry" to mapOf("Nature_Def" to 0.75),
                "nomel_berry" to mapOf("Nature_SpD" to 0.75),
                "magost_berry" to mapOf("Nature_Spe" to 0.75),

                // Attract Natures Tier 4 - 100%
                "spelon_berry" to mapOf("Nature_Att" to 1.00),
                "pamtre_berry" to mapOf("Nature_SpA" to 1.00),
                "belue_berry" to mapOf("Nature_Def" to 1.00),
                "durin_berry" to mapOf("Nature_SpD" to 1.00),
                "watmel_berry" to mapOf("Nature_Spe" to 1.00),

                // Raises IV by +5
                "lansat_berry" to mapOf("IV_Hp" to 5.00), // HP
                "liechi_berry" to mapOf("IV_Att" to 5.00), // Att
                "petaya_berry" to mapOf("IV_SpA" to 5.00), // SpA
                "ganlon_berry" to mapOf("IV_Def" to 5.00),  // Def
                "apicot_berry" to mapOf("IV_SpD" to 5.00),  // SpD
                "salac_berry" to mapOf("IV_Spe" to 5.00), // Spe

                // Attracts EV Yields
                "pomeg_berry"  to mapOf("EV_Hp" to 1.00), // HP
                "tamato_berry"  to mapOf("EV_Att" to 1.00), // Att
                "kelpsy_berry"  to mapOf("EV_SpA" to 1.00), // SpA
                "grepa_berry"  to mapOf("EV_Def" to 1.00),  // Def
                "hondew_berry"  to mapOf("EV_SpD" to 1.00),  // SpD
                "qualot_berry"  to mapOf("EV_Spe" to 1.00), // Spe

                // Reduce Bite Times
                "oran_berry" to mapOf("Bite_Time" to 0.50), // reduce by 50%
                "sitrus_berry" to mapOf("Bite_Time" to 1.00), // reduce by 100%

                // Raises Gender Odds
                "kee_berry" to mapOf("Gender_Female" to 0.25), // +25% female
                "maranga_berry" to mapOf("Gender_Male" to 0.25), // +25% male

                // Raises Average Level
                "leppa_berry" to mapOf("Level" to 5.00), // +5
                "hopo_berry" to mapOf("Level" to 10.00),  // +10

                // 5% Chance for alt Tera Type
                // for each type berries
                "occa_berry" to mapOf("Tera_Fire" to 0.05),
                "passho_berry" to mapOf("Tera_Water" to 0.05),
                "wacan_berry" to mapOf("Tera_Electric" to 0.05),
                "rindo_berry" to mapOf("Tera_Grass" to 0.05),
                "yache_berry" to mapOf("Tera_Ice" to 0.05),
                "chople_berry" to mapOf("Tera_Fighting" to 0.05),
                "kebia_berry" to mapOf("Tera_Poison" to 0.05),
                "shuca_berry" to mapOf("Tera_Ground" to 0.05),
                "coba_berry" to mapOf("Tera_Flying" to 0.05),
                "payapa_berry" to mapOf("Tera_Psychic" to 0.05),
                "tanga_berry" to mapOf("Tera_Bug" to 0.05),
                "charti_berry" to mapOf("Tera_Rock" to 0.05),
                "kasib_berry" to mapOf("Tera_Ghost" to 0.05),
                "haban_berry" to mapOf("Tera_Dragon" to 0.05),
                "colbur_berry" to mapOf("Tera_Dark" to 0.05),
                "babiri_berry" to mapOf("Tera_Steel" to 0.05),
                "chilan_berry" to mapOf("Tera_Normal" to 0.05),
                "roseli_berry" to mapOf("Tera_Fairy" to 0.05),



                // 2x Shiny Odds
                "starf_berry" to mapOf("Shiny" to 2.00),

                // 2.5% for Hidden Ability
                "enigma_berry" to mapOf("HA" to 0.025),

                // 100% Pokemon Rate
                "micle_berry"  to mapOf("Pokemon" to 1.00),

                // 2x Item Rate
                "custap_berry" to mapOf("Pokemon" to 0.70) // alter the chance of an item to spawn based on lowering the pokemon chance

//                // Heart Wooper Chance
//                "persim_berry" -> Pair("spa", 0.0)
//
//                // Giant Whiscash Chance
//                "lum_berry" -> Pair("spa", 0.0)

                // todo when spawning pokemon send in the berry used as bait and also the trait we are rerolling for into here to get the chance of increase
                // todo   pokemon.nature = getBerryBaitBonus(bait, "Nature_ATT")
                // Add default or special cases if needed
        )

        // Look up the berry and then the chanceBoost
        return berryBoosts[Registries.ITEM.getId(bait.item).path.toString()]?.get(chanceBoost) ?: 0.0 // Returns 0.0 if not found

    }

    fun modifyPokemonWithBait(pokemonEntity: PokemonEntity, bait: ItemStack) {
        val pokemon = pokemonEntity.pokemon

        // check if it attracts a certain nature
        if (checkNatureAttact(bait) == true) {
            // figure out which nature is attracted by the bait and do a calculation to see if it was successful
            alterNatureAttempt(pokemon, bait)
        }

        // check if it raises specific IV by +3
        if (checkIVRaise(bait) == true) {
            // try to alter the IVs of the pokemon based on the bait bonus
            alterIVAttempt(pokemon, bait)
        }

        // todo check if it attracts certain EV yields? Maybe this needs to be done before the spown conditions are used? This one might be hard

        // Reduce Bite time.... This can be done way earlier (And is currently in place)

        // check if it alters the gender of the pokemon
        if (checkBetterGenderOdds(bait) == true) {
            // try to alter the pokemon gender based on the bait bonus
            alterGenderAttempt(pokemon, bait)
        }

        // check if it raises the average level
        if (checkLevelBoost(bait) == true) {
            // try to alter the pokemon level based on the bait bonus
            alterLevelAttempt(pokemon, bait)
        }

        // check if it alters the tera type
        if (checkTeraType(bait) == true) {
            // try to alter the pokemon level based on the bait bonus
            alterTeraAttempt(pokemon, bait)
        }

        // check for shiny odds
        if (checkShinyOdds(bait) == true) {
            // try to alter the pokemon level based on the bait bonus
            alterShinyAttempt(pokemon, bait)
        }

        // check for HA
        if (checkHiddenAbilityOdds(bait) == true) {
            // try to alter the pokemon level based on the bait bonus
            alterHAAttempt(pokemon, bait)
        }

        // check for 100% pokemon rate (do this way earlier) (And is currently in place)

        // check for item rate (do this way earlier) (And is currently in place)

        // check for Heart Wooper (this needs to be spawn condition) (And is currently in place)

        // check for Large Whiscash (this needs to be spawn condition) (And is currently in place)
    }

    fun checkBaitSuccessRate(successChance: Double): Boolean {
        return Math.random() <= successChance
    }

    // function to return true of false if the given bait affects the attraction of certain Natures
    fun checkNatureAttact(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Nature"
    }

    // function to return true of false if the given bait affects the raising of IVs of a pokemon via fishing
    fun checkIVRaise(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "IV"
    }

    // function to return true of false if the given bait affects the pokemon with certain EV yields
    fun checkEVAttract(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "EV"
    }

    // function to return true of false if the given bait affects the pokemon gender
    fun checkBetterGenderOdds(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Gender_Chance"
    }

    // function to return true of false if the given bait affects time to expect a bite
    fun checkReduceBiteTime(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Bite_Time"
    }

    // function to return true of false if the given bait affects pokemon's level boost
    fun checkLevelBoost(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Level_Raise"
    }

    // function to return true of false if the given bait affects pokemon's tera type
    fun checkTeraType(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Tera"
    }

    // function to return true of false if the given bait affects pokemon's shiny chance
    fun checkShinyOdds(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Shiny_Reroll"
    }

    // function to return true of false if the given bait affects pokemon's Hidden Ability
    fun checkHiddenAbilityOdds(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "HA_Chance"
    }

    // function to return true of false if the given bait to make it so a Pokemon is always reeled in
    fun checkPokemonFishRate(bait: ItemStack): Boolean {
        return FishingBaits.getBaitEffect(bait) == "Pokemon_Chance"
    }


    // try to alter the nature of the spawned pokemon
    fun alterNatureAttempt(pokemon: Pokemon, bait: ItemStack) {
        // natures for each stat
        val attNaturesIds = listOf(Natures.LONELY, Natures.ADAMANT, Natures.NAUGHTY, Natures.BRAVE)
        val spaNaturesIds = listOf(Natures.MODEST, Natures.MILD, Natures.RASH, Natures.QUIET, )
        val defNaturesIds = listOf(Natures.BOLD, Natures.IMPISH, Natures.LAX, Natures.RELAXED, )
        val spdNaturesIds = listOf(Natures.CALM, Natures.GENTLE, Natures.CAREFUL, Natures.SASSY, )
        val speNaturesIds = listOf(Natures.TIMID, Natures.HASTY, Natures.JOLLY, Natures.NAIVE, )
        val neutralNaturesIds = listOf(Natures.HARDY, Natures.DOCILE, Natures.BASHFUL, Natures.QUIRKY, Natures.SERIOUS, )

        if (FishingBaits.getBaitSubcategory(bait) == "Att" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) // it is for Attack
            pokemon.nature = attNaturesIds.random() // choose a random nature from the list
        else if (FishingBaits.getBaitSubcategory(bait) == "SpA" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) // it is for Attack
            pokemon.nature = spaNaturesIds.random() // choose a random nature from the list
        else if (FishingBaits.getBaitSubcategory(bait) == "Def" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) // it is for Attack
            pokemon.nature = defNaturesIds.random() // choose a random nature from the list
        else if (FishingBaits.getBaitSubcategory(bait) == "SpD" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) // it is for Attack
            pokemon.nature = spdNaturesIds.random() // choose a random nature from the list
        else if (FishingBaits.getBaitSubcategory(bait) == "Spe" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) // it is for Attack
            pokemon.nature = speNaturesIds.random() // choose a random nature from the list
    }

    // alter the IVs based on the bait effect
    fun alterIVAttempt(pokemon: Pokemon, bait: ItemStack) {

        if (FishingBaits.getBaitSubcategory(bait) == "Hp" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if ((pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.HP] ?: 0) > 28) // if HP IV is already less than 3 away from 31
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.HP, 31)
            else
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.HP, (pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.HP] ?: 0) + getBerryBaitBonusChance(bait, "IV_Hp").toInt())
        }
        if (FishingBaits.getBaitSubcategory(bait) == "Att" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if ((pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK] ?: 0) > 28) // if Attack IV is already less than 3 away from 31
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK, 31)
            else
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK, (pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK] ?: 0) + getBerryBaitBonusChance(bait, "IV_Att").toInt())
        }
        if (FishingBaits.getBaitSubcategory(bait) == "SpA" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if ((pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK] ?: 0) > 28) // if Special Attack IV is already less than 3 away from 31
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK, 31)
            else
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK, (pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK] ?: 0) + getBerryBaitBonusChance(bait, "IV_SpA").toInt())
        }
        if (FishingBaits.getBaitSubcategory(bait) == "Def" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if ((pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE] ?: 0) > 28) // if Defence IV is already less than 3 away from 31
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE, 31)
            else
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE, (pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE] ?: 0) + getBerryBaitBonusChance(bait, "IV_Def").toInt())
        }
        if (FishingBaits.getBaitSubcategory(bait) == "SpD" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if ((pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE] ?: 0) > 28) // if Special Defence IV is already less than 3 away from 31
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE, 31)
            else
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE, (pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE] ?: 0) + getBerryBaitBonusChance(bait, "IV_SpA").toInt())
        }
        if (FishingBaits.getBaitSubcategory(bait) == "Spe" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if ((pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED] ?: 0) > 28) // if Speed IV is already less than 3 away from 31
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED, 31)
            else
                pokemon.ivs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED, (pokemon.ivs[com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED] ?: 0) + getBerryBaitBonusChance(bait, "IV_Spe").toInt())
        }
    }

    // try to alter the gender based on the bait effect
    fun alterGenderAttempt(pokemon: Pokemon, bait: ItemStack) {
        if (pokemon.gender != Gender.MALE && (FishingBaits.getBaitSubcategory(bait) == "Male" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0))) {
            pokemon.gender = Gender.MALE
        }
        else if (pokemon.gender != Gender.FEMALE && (FishingBaits.getBaitSubcategory(bait) == "Female" && checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0))) {
            pokemon.gender = Gender.FEMALE
        }
    }

    // alter the level of the pokemon
    fun alterLevelAttempt(pokemon: Pokemon, bait: ItemStack) {
        if  (checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if (pokemon.level + (FishingBaits.getBaitEffectValue(bait) ?: 0.0).toInt() > 100) // if it would go past 100 just set it to 100
                pokemon.level = 100
            else
                pokemon.level = pokemon.level + (FishingBaits.getBaitEffectValue(bait) ?: 0.0).toInt() // increase the level by the given amount
        }
    }

    // try to reroll for a shiny based on the bait effect
    fun alterTeraAttempt(pokemon: Pokemon, bait: ItemStack) {
        if (checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            pokemon.teraType = FishingBaits.getBaitSubcategory(bait)?.let { ElementalTypes.get(it.lowercase()) }!!
        }
    }

    // try to reroll for a shiny based on the bait effect
    fun alterShinyAttempt(pokemon: Pokemon, bait: ItemStack) {
        if (checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            if (!pokemon.shiny) {
                //reroll for a shiny using shiny odds
                val shinyOdds: Int = 8192
                val randomNumber = kotlin.random.Random.nextInt((FishingBaits.getBaitEffectValue(bait) ?: 0.0).toInt(), shinyOdds + 1)

                // Check if the random number indicates a shiny Pokemon
                if (randomNumber <= (FishingBaits.getBaitEffectValue(bait) ?: 0.0).toInt()) {
                    pokemon.shiny = true
                }
            }
        }
    }

    // check if the bite time is reduced based on the bait bonus
    fun alterBiteTimeAttempt(waitCountdown: Int, bait: ItemStack): Int {
        if (checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) { // if the bait currently reduces the bait time
            if (waitCountdown * (FishingBaits.getBaitEffectValue(bait) ?: 0.0) <= 0)
                return 1 // return min value
            else
                return (waitCountdown * (FishingBaits.getBaitEffectValue(bait) ?: 0.0)).toInt()
        }
        else return waitCountdown // do not alter the hookcountdown
    }

    // todo chance to alter HA based on the berry effect
    fun alterHAAttempt(pokemon: Pokemon, bait: ItemStack) {
        if (checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            giveHiddenAbility(pokemon) // todo make this give the pokemon its hidden ability
        }
    }

    // give a pokemon a random hidden ability
    private fun giveHiddenAbility(pokemon: Pokemon): Boolean {
        // This will iterate from highest to lowest priority
        pokemon.form.abilities.mapping.values.forEach { abilities ->
            abilities.filterIsInstance<HiddenAbility>()
                    .randomOrNull ()
                    ?.let { ability ->
                        // No need to force, this is legal
                        pokemon.ability = ability.template.create(false)
                        return true
                    }
        }
        // There was never a hidden ability :( possible but not by default
        return false
    }

    // check the chance of a pokemon to spawn and if it is affected by bait
    fun getPokemonSpawnChance(bait: ItemStack): Int {
        if (checkBaitSuccessRate(FishingBaits.getBaitSuccessChance(bait) ?: 0.0)) {
            return ((FishingBaits.getBaitSuccessChance(bait) ?: 0.0) * 100).toInt()
        }
        else return this.pokemonSpawnChance
    }

    companion object {
        val POKEROD_ID = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.STRING)
        val POKEBOBBER_BAIT = DataTracker.registerData(PokeRodFishingBobberEntity::class.java, TrackedDataHandlerRegistry.ITEM_STACK)
    }
}
