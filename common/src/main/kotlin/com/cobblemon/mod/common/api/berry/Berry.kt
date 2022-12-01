/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.berry

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.berry.BerryYieldCalculationEvent
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import net.minecraft.predicate.NumberRange
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome

/**
 * Represents the data behind a berry.
 *
 * @property identifier The [Identifier] of this berry.
 * @property baseYield The [IntRange] possible for the berry tree before [bonusYield] is calculated.
 * @property lifeCycles The [IntRange] possible for the berry to live for between harvests.
 * @property temperatureRange The [NumberRange.FloatRange] possible for the [Biome.getTemperature] in order to yield a bonus berry.
 * @property temperatureBonusYield The [IntRange] for the bonus amount of berries if the [temperatureRange] is satisfied.
 * @property downfallRange The [NumberRange.FloatRange] possible for the [Biome.getDownfall] in order to yield a bonus berry.
 * @property downfallBonusYield The [IntRange] for the bonus amount of berries if the [downfallRange] is satisfied.
 * @property flavors The [Flavor] values.
 *
 * @throws IllegalArgumentException if the any yield range argument is not a positive range.
 */
class Berry(
    identifier: Identifier,
    val baseYield: IntRange,
    val lifeCycles: IntRange,
    val temperatureRange: NumberRange.FloatRange,
    val temperatureBonusYield: IntRange,
    val downfallRange: NumberRange.FloatRange,
    val downfallBonusYield: IntRange,
    val interactions: Collection<PokemonEntityInteraction>,
    private val anchorPoints: Array<Vec3d>,
    private val flowerShape: Collection<Box>,
    private val fruitShape: Collection<Box>,
    private val flavors: Map<Flavor, Int>
) {

    @Transient
    var identifier: Identifier = identifier
        internal set

    @Transient
    private val shapedFlower = hashMapOf<Int, VoxelShape>()
    @Transient
    private val shapedFruit = hashMapOf<Int, VoxelShape>()

    init {
        this.validate()
    }

    /**
     * Query the value of a certain flavor.
     *
     * @param flavor The [Flavor] being queried.
     * @return The value if any or 0.
     */
    fun flavor(flavor: Flavor): Int = this.flavors[flavor] ?: 0

    /**
     * Calculates the yield for a berry tree being harvested.
     * This will trigger [BerryYieldCalculationEvent] if the [player] argument is not null.
     *
     * @param world The [WorldView] the tree is present in.
     * @param pos The [BlockPos] of the tree.
     * @param player The [ServerPlayerEntity] planting the tree, if any.
     * @return The total berry stack count.
     */
    fun calculateYield(world: WorldView, pos: BlockPos, player: ServerPlayerEntity? = null): Int {
        val base = this.baseYield.random()
        val bonus = this.bonusYield(world, pos)
        var yield = base + bonus.first
        if (player != null) {
            val event = BerryYieldCalculationEvent(this, player, yield, bonus.second, bonus.third)
            CobblemonEvents.BERRY_YIELD.post(event) { yield = it.yield }
        }
        return yield
    }

    /**
     * Calculates and returns the minimum possible yield by summing [baseYield], [temperatureBonusYield] and [downfallBonusYield] minimum values.
     *
     * @return The minimum possible yield.
     */
    fun minYield() = this.baseYield.first + this.temperatureBonusYield.first + this.downfallBonusYield.first

    /**
     * Calculates and returns the maximum possible yield by summing [baseYield], [temperatureBonusYield] and [downfallBonusYield] max values.
     *
     * @return The maximum possible yield.
     */
    fun maxYield() = this.baseYield.last + this.temperatureBonusYield.last + this.downfallBonusYield.last

    // A cheat since gson doesn't invoke init block
    internal fun validate() {
        if (this.baseYield.first < 1 || this.baseYield.last < 1) {
            throw IllegalArgumentException("A berry base yield must be a positive range")
        }
        if (this.lifeCycles.first < 1 || this.lifeCycles.last < 1) {
            throw IllegalArgumentException("A berry life cycle must be a positive range")
        }
        if (this.temperatureBonusYield.first < 1 || this.temperatureBonusYield.last < 1) {
            throw IllegalArgumentException("A berry temperature bonus yield must be a positive range")
        }
        if (this.downfallBonusYield.first < 1 || this.downfallBonusYield.last < 1) {
            throw IllegalArgumentException("A berry downfall bonus yield must be a positive range")
        }
        val maxYield = this.maxYield()
        if (this.anchorPoints.size < maxYield) {
            throw IllegalArgumentException("Anchor points must have enough elements for the max possible yield of $maxYield you've provided ${this.anchorPoints.size} points")
        }
        if (this.flowerShape.isEmpty()) {
            throw IllegalArgumentException("A flower shape must be provided")
        }
        if (this.fruitShape.isEmpty()) {
            throw IllegalArgumentException("A fruit shape must be provided")
        }
    }

    /**
     * Find the [VoxelShape] at the provided [index].
     *
     * @param index The index the [VoxelShape] is expected to be at.
     * @param isFlower If the shape being queried is the flower or fruit variant.
     * @return The [VoxelShape] at the queried index.
     * @throws IndexOutOfBoundsException If the [index] is invalid.
     */
    internal fun shapeAt(index: Int, isFlower: Boolean): VoxelShape {
        val map = if (isFlower) this.shapedFlower else this.shapedFruit
        if (!map.containsKey(index)) {
            val vec = this.anchorPoints[index]
            val boxes = if (isFlower) this.flowerShape else this.fruitShape
            val shapeParts = boxes.map { this.createShape(it, vec) }
            return if (shapeParts.size > 1) {
                var shape: VoxelShape? = null
                for (element in shapeParts) {
                    shape = if (shape == null) {
                        element
                    } else {
                        VoxelShapes.union(shape, element)
                    }
                }
                shape!!
            } else {
                shapeParts.first()
            }
        }
        return map[index]!!
    }

    /**
     * Calculates the bonus yield for the berry tree.
     *
     * @param world The [WorldView] the tree is present in.
     * @param pos The [BlockPos] of the tree.
     * @return The bonus yield, if temp range passed, if downfall range passed.
     */
    private fun bonusYield(world: WorldView, pos: BlockPos): Triple<Int, Boolean, Boolean> {
        var bonus = 0
        var passedTemperature = false
        var passedDownfall = false
        val biome = world.getBiome(pos).value()
        if (this.temperatureRange.test(biome.temperature.toDouble())) {
            bonus += this.temperatureBonusYield.random()
            passedTemperature = true
        }
        if (this.downfallRange.test(biome.downfall.toDouble())) {
            bonus += this.downfallBonusYield.random()
            passedDownfall = true
        }
        return Triple(bonus, passedTemperature, passedDownfall)
    }

    private fun createShape(box: Box, vec: Vec3d): VoxelShape = VoxelShapes.cuboid(vec.x + box.minX, vec.y + box.minY, vec.z + box.minZ, vec.x + box.maxX, vec.y + box.maxY, vec.z + box.maxZ)

}
