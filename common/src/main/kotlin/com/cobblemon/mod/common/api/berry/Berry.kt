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
import net.minecraft.util.shape.VoxelShape
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
    val identifier: Identifier,
    val baseYield: IntRange,
    val lifeCycles: IntRange,
    val temperatureRange: NumberRange.FloatRange,
    val temperatureBonusYield: IntRange,
    val downfallRange: NumberRange.FloatRange,
    val downfallBonusYield: IntRange,
    val interactions: Collection<PokemonEntityInteraction>,
    private val flavors: Map<Flavor, Int>
) {

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

    fun minYield() = this.baseYield.first + this.temperatureBonusYield.first + this.downfallBonusYield.first

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
        /*
        if (this.anchorPoints.size < maxYield) {
            throw IllegalArgumentException("Anchor points must have enough elements for the max possible yield ${this.identifier} can yield $maxYield you've provided ${this.anchorPointCount()} points")
        }
         */
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

}
