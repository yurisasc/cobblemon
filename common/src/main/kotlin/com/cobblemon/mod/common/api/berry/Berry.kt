/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.berry

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.berry.BerryYieldCalculationEvent
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.cobblemon.mod.common.item.BerryItem
import com.cobblemon.mod.common.util.readBox
import com.cobblemon.mod.common.util.writeBox
import com.cobblemon.mod.common.world.block.BerryBlock
import com.google.gson.annotations.SerializedName
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.LivingEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import java.awt.Color

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
 * @property tintIndexes Determines tints at specific indexes if any.
 * @property flowerModelIdentifier The [Identifier] for the model of the berry in flower form.
 * @property flowerTexture The [Identifier] for the texture of the berry in flower form.
 * @property fruitModelIdentifier The [Identifier] for the model of the berry in flower form.
 * @property fruitTexture The [Identifier] for the texture of the berry in flower form.
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
    @SerializedName("sproutShape")
    private val sproutShapeBoxes: Collection<Box>,
    @SerializedName("matureShape")
    private val matureShapeBoxes: Collection<Box>,
    private val flowerShape: Collection<Box>,
    private val fruitShape: Collection<Box>,
    private val flavors: Map<Flavor, Int>,
    val tintIndexes: Map<Int, Color>,
    @SerializedName("flowerModel")
    private val flowerModelIdentifier: Identifier,
    val flowerTexture: Identifier,
    @SerializedName("fruitModel")
    private val fruitModelIdentifier: Identifier,
    val fruitTexture: Identifier
) {

    @Transient
    var identifier: Identifier = identifier
        internal set

    @Transient
    private lateinit var shapedFlower: HashMap<Int, VoxelShape>

    @Transient
    private lateinit var shapedFruit: HashMap<Int, VoxelShape>

    /**
     * The [VoxelShape] of the tree during the sprouting stages.
     */
    @Transient
    lateinit var sproutShape: VoxelShape
        private set

    /**
     * The [VoxelShape] of the tree during the mature stages.
     */
    @Transient
    lateinit var matureShape: VoxelShape
        private set

    init {
        this.validate()
    }

    /**
     * Finds the [BerryItem] that uses this berry for data.
     *
     * @return The [BerryItem] if existing.
     */
    fun item(): BerryItem? = CobblemonItems.berries()[this.identifier]?.get()

    /**
     * Finds the [BerryBlock] that uses this berry for data.
     *
     * @return The [BerryBlock] if existing.
     */
    fun block(): BerryBlock? = CobblemonBlocks.berries()[this.identifier]?.get()

    /**
     * Query the value of a certain flavor.
     *
     * @param flavor The [Flavor] being queried.
     * @return The value if any or 0.
     */
    fun flavor(flavor: Flavor): Int = this.flavors[flavor] ?: 0

    /**
     * Finds the [ModelPart] for the given [flowerModelIdentifier].
     * This should only be invoked on the client.
     *
     * @return The [ModelPart] of the fruit if existing.
     */
    fun flowerModel(): ModelPart? = BerryModelRepository.modelOf(this.flowerModelIdentifier)

    /**
     * Finds the [ModelPart] for the given [fruitModelIdentifier].
     * This should only be invoked on the client.
     *
     * @return The [ModelPart] of the fruit if existing.
     */
    fun fruitModel(): ModelPart? = BerryModelRepository.modelOf(this.fruitModelIdentifier)

    /**
     * Calculates the yield for a berry tree being planted.
     * Triggers [BerryYieldCalculationEvent].
     *
     * @param world The [World] the tree is present in.
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @param placer The [LivingEntity] planting the tree, if any.
     * @return The total berry stack count.
     */
    fun calculateYield(world: World, state: BlockState, pos: BlockPos, placer: LivingEntity? = null): Int {
        val base = this.baseYield.random()
        val bonus = this.bonusYield(world, state, pos)
        var yield = base + bonus.first
        val event = BerryYieldCalculationEvent(this, world, state, pos, placer, yield, bonus.second)
        CobblemonEvents.BERRY_YIELD.post(event) { yield = it.yield }
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
        if (this.baseYield.first < 0 || this.baseYield.last < 0) {
            throw IllegalArgumentException("A berry base yield must be a positive range")
        }
        if (this.lifeCycles.first < 0 || this.lifeCycles.last < 0) {
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
        this.shapedFlower = hashMapOf()
        this.shapedFruit = hashMapOf()
        this.sproutShape = this.createAndUniteShapes(this.sproutShapeBoxes)
        this.matureShape = this.createAndUniteShapes(this.matureShapeBoxes)
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
        return map[index] ?: VoxelShapes.empty()
    }

    internal fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.identifier)
        buffer.writeInt(this.baseYield.first)
        buffer.writeInt(this.baseYield.last)
        buffer.writeInt(this.lifeCycles.first)
        buffer.writeInt(this.lifeCycles.last)
        buffer.writeCollection(this.anchorPoints.toList())  { writer, value ->
            writer.writeDouble(value.x)
            writer.writeDouble(value.y)
            writer.writeDouble(value.z)
        }
        buffer.writeCollection(this.sproutShapeBoxes) { writer, value ->
            writer.writeBox(value)
        }
        buffer.writeCollection(this.matureShapeBoxes) { writer, value ->
            writer.writeBox(value)
        }
        buffer.writeCollection(this.flowerShape) { writer, value ->
            writer.writeBox(value)
        }
        buffer.writeCollection(this.fruitShape) { writer, value ->
            writer.writeBox(value)
        }
        buffer.writeMap(this.flavors, { writer, key -> writer.writeEnumConstant(key) }, { writer, value -> writer.writeInt(value) })
        buffer.writeMap(this.tintIndexes, { writer, key -> writer.writeInt(key) }, { writer, value -> writer.writeInt(value.rgb) })
        buffer.writeIdentifier(this.flowerModelIdentifier)
        buffer.writeIdentifier(this.flowerTexture)
        buffer.writeIdentifier(this.fruitModelIdentifier)
        buffer.writeIdentifier(this.fruitTexture)
    }

    /**
     * Calculates the bonus yield for the berry tree.
     *
     * @param world The [World] the tree is present in.
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @return The bonus yield, the growth factors that passed.
     */
    private fun bonusYield(world: World, state: BlockState, pos: BlockPos): Pair<Int, Collection<GrowthFactor>> {
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

    private fun createAnchorPointShape(box: Box, vec: Vec3d): VoxelShape = Block.createCuboidShape(vec.x + box.minX, vec.y + box.minY, vec.z + box.minZ, vec.x + box.maxX, vec.y + box.maxY, vec.z + box.maxZ)

    private fun createAndUniteShapes(boxes: Collection<Box>): VoxelShape {
        var shape: VoxelShape? = null
        boxes.forEach { box ->
            shape = if (shape == null) {
                Block.createCuboidShape(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)
            } else {
                VoxelShapes.union(shape, Block.createCuboidShape(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ))
            }
        }
        return shape ?: VoxelShapes.fullCube()
    }

    companion object {

        internal fun decode(buffer: PacketByteBuf): Berry {
            val identifier = buffer.readIdentifier()
            val baseYield = IntRange(buffer.readInt(), buffer.readInt())
            val lifeCycles = IntRange(buffer.readInt(), buffer.readInt())
            val anchorPoints = buffer.readList { reader ->
                Vec3d(reader.readDouble(), reader.readDouble(), reader.readDouble())
            }.toTypedArray()
            val sproutShapeBoxes = buffer.readList { it.readBox() }
            val matureShapeBoxes = buffer.readList { it.readBox() }
            val flowerShape = buffer.readList { it.readBox() }
            val fruitShape = buffer.readList { it.readBox() }
            val flavors = buffer.readMap({ reader -> reader.readEnumConstant(Flavor::class.java) }, { reader -> reader.readInt() })
            val tintIndexes = buffer.readMap({ reader -> reader.readInt() }, { reader -> Color(reader.readInt()) })
            val flowerModelIdentifier = buffer.readIdentifier()
            val flowerTexture = buffer.readIdentifier()
            val fruitModelIdentifier = buffer.readIdentifier()
            val fruitTexture = buffer.readIdentifier()
            return Berry(identifier, baseYield, lifeCycles, emptyList(), emptyList(), anchorPoints, sproutShapeBoxes, matureShapeBoxes, flowerShape, fruitShape, flavors, tintIndexes, flowerModelIdentifier, flowerTexture, fruitModelIdentifier, fruitTexture)
        }

    }

}
