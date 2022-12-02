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
import net.minecraft.block.BlockState
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
 * @property growthFactors A collection of [GrowthFactor]s that will affect this berry.
 * @property interactions A collection of [PokemonEntityInteraction]s this berry will have in item form.
 * @property sproutShape A collection of [Box]es that make up the tree [VoxelShape] during the sprouting stages.
 * @property matureShape A collection of [Box]es that make up the tree [VoxelShape] during the mature stages.
 * @property flowerShape A collection of [Box]es used to dynamically create [VoxelShape]s for flowering berries tied to [anchorPoints].
 * @property fruitShape A collection of [Box]es used to dynamically create [VoxelShape]s for flowering berries tied to [anchorPoints].
 * @property flavors The [Flavor] values.
 *
 * @throws IllegalArgumentException if the any yield range argument is not a positive range.
 */
class Berry(
    identifier: Identifier,
    val baseYield: IntRange,
    val lifeCycles: IntRange,
    val growthFactors: Collection<GrowthFactor>,
    val interactions: Collection<PokemonEntityInteraction>,
    private val anchorPoints: Array<Vec3d>,
    sproutShape: Collection<Box>,
    matureShape: Collection<Box>,
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

    /**
     * The [VoxelShape] of the tree during the sprouting stages.
     */
    @Transient
    val sproutShape: VoxelShape = this.createAndUniteShapes(sproutShape)

    /**
     * The [VoxelShape] of the tree during the mature stages.
     */
    @Transient
    val matureShape: VoxelShape = this.createAndUniteShapes(matureShape)

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
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @param player The [ServerPlayerEntity] planting the tree, if any.
     * @return The total berry stack count.
     */
    fun calculateYield(world: WorldView, state: BlockState, pos: BlockPos, player: ServerPlayerEntity? = null): Int {
        val base = this.baseYield.random()
        val bonus = this.bonusYield(world, state, pos)
        var yield = base + bonus.first
        if (player != null) {
            val event = BerryYieldCalculationEvent(this, player, yield, bonus.second)
            CobblemonEvents.BERRY_YIELD.post(event) { yield = it.yield }
        }
        return yield
    }

    /**
     * Calculates and returns the minimum possible yield by summing [baseYield] and [growthFactors] minimum values.
     *
     * @return The minimum possible yield.
     */
    fun minYield() = this.baseYield.first + this.growthFactors.sumOf { it.minYield() }

    /**
     * Calculates and returns the maximum possible yield by summing [baseYield] and [growthFactors] max values.
     *
     * @return The maximum possible yield.
     */
    fun maxYield() = this.baseYield.last + this.growthFactors.sumOf { it.maxYield() }

    // A cheat since gson doesn't invoke init block
    internal fun validate() {
        if (this.baseYield.first < 1 || this.baseYield.last < 1) {
            throw IllegalArgumentException("A berry base yield must be a positive range")
        }
        if (this.lifeCycles.first < 1 || this.lifeCycles.last < 1) {
            throw IllegalArgumentException("A berry life cycle must be a positive range")
        }
        this.growthFactors.forEach { it.validateArguments() }
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
            val shapeParts = boxes.map { this.createAnchorPointShape(it, vec) }
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
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @return The bonus yield, the growth factors that passed.
     */
    private fun bonusYield(world: WorldView, state: BlockState, pos: BlockPos): Pair<Int, Collection<GrowthFactor>> {
        var bonus = 0
        val passed = arrayListOf<GrowthFactor>()
        this.growthFactors.forEach { factor ->
            if (factor.isValid(world, state, pos)) {
                bonus += factor.yield()
                passed += factor
            }
        }
        return bonus to passed
    }

    private fun createAnchorPointShape(box: Box, vec: Vec3d): VoxelShape = VoxelShapes.cuboid(vec.x + box.minX, vec.y + box.minY, vec.z + box.minZ, vec.x + box.maxX, vec.y + box.maxY, vec.z + box.maxZ)

    private fun createAndUniteShapes(boxes: Collection<Box>): VoxelShape {
        var shape: VoxelShape? = null
        boxes.forEach { box ->
            shape = if (shape == null) {
                VoxelShapes.cuboid(box)
            } else {
                VoxelShapes.union(shape, VoxelShapes.cuboid(box))
            }
        }
        return shape ?: VoxelShapes.fullCube()
    }

}
