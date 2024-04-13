package com.cobblemon.mod.common.api.battles.effects

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.util.math.orMax
import com.cobblemon.mod.common.util.math.orMin
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

/**
 * Represents a field condition that can occur at battle start.
 *
 * @param identifier The [Identifier] of this condition.
 * @param biomes: A [MutableSet] of biomes that this condition will succeed in.
 * @param timeRange A [TimeRange] that this condition will succeed in.
 * @param isRaining Whether this condition requires rain to succeed. When null, will not perform any check.
 * @param isThundering Whether this condition requires thunder to succeed. When null, will not perform any check.
 * @param result The showdown ID of the field condition to apply.
 *
 * @author whatsy
 * @since April 9th, 2024
 */
data class BattleStartCondition(
    var identifier: Identifier,
    val biomes: MutableSet<RegistryLikeCondition<Biome>>,
    val timeRange: TimeRange? = null,
    val isRaining: Boolean?,
    val isThundering: Boolean?,

    val minX: Float? = null,
    val minY: Float? = null,
    val minZ: Float? = null,
    val maxX: Float? = null,
    val maxY: Float? = null,
    val maxZ: Float? = null,

    val result: String
) {
    /***
     * Checks if this [BattleStartCondition] can succeed in the context of the provided [PokemonBattle].
     *
     * @author whatsy
     * @since April 9th, 2024
     */
    fun matches(battle: PokemonBattle): Boolean {
        val player = battle.players.first()
        val world = player.world ?: return false
        if (!world.gameRules.getBoolean(CobblemonGameRules.WEATHER_AFFECTS_BATTLES)) return false
        val pos = player.blockPos ?: return false
        val biomeRegistry: Registry<Biome> by lazy { world.registryManager.get(RegistryKeys.BIOME) }
        val biome = world.getBiome(pos).value()

        if (biomes.isNotEmpty() && biomes.none { condition -> condition.fits(biome, biomeRegistry) }) {
            return false
        } else if (timeRange != null && !timeRange.contains((world.timeOfDay % 24000).toInt())) {
            return false
        } else if (world.isRaining != isRaining && isRaining != null) {
            return false
        } else if (world.isThundering != isThundering && isThundering != null) {
            return false
        } else if (pos.x < minX.orMin() || pos.x > maxX.orMax()) {
            return false
        } else if (pos.y < minY.orMin() || pos.y > maxY.orMax()) {
            return false
        } else if (pos.z < minZ.orMin() || pos.z > maxZ.orMax()) {
            return false
        }

        return true
    }
}
