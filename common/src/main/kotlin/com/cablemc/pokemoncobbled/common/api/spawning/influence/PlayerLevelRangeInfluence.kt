package com.cablemc.pokemoncobbled.common.api.spawning.influence

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.config
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.PokemonSpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.math.intersection
import com.cablemc.pokemoncobbled.common.util.math.intersects
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.math.max
import kotlin.math.min

/**
 * A [SpawningInfluence] that restricts spawns around a player to be within their level range.
 * This will flat out prevent Pokémon spawns that can't be within the level range, and then for
 * those that can be, it will also adjust the possible level range to be the intersection of the
 * acceptable level range and the player's level range.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
open class PlayerLevelRangeInfluence(
    player: ServerPlayerEntity,
    val variation: Int,
    val noPokemonRange: IntRange = 1 .. config.maxPokemonLevel,
    val recalculationMillis: Long = 5000L
) : SpawningInfluence {
    val uuid = player.uuid
    var lastCalculatedTime: Long = 0
    var previousRange: IntRange = noPokemonRange

    fun getPlayerLevelRange(): IntRange {
        return if (System.currentTimeMillis() - lastCalculatedTime > recalculationMillis) {
            lastCalculatedTime = System.currentTimeMillis()

            val party = PokemonCobbled.storage.getParty(uuid)
            previousRange = if (party.any()) {
                val minimumLevel = party.minOf { it.level }
                val maximumLevel = party.maxOf { it.level }
                IntRange(max(minimumLevel - variation, 1), min(maximumLevel + variation, config.maxPokemonLevel))
            } else {
                noPokemonRange
            }
            previousRange
        } else {
            previousRange
        }
    }

    override fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean {
        return if (detail !is PokemonSpawnDetail) {
            true
        } else {
            val playerRange = getPlayerLevelRange()
            val spawnRange = detail.getDerivedLevelRange()
            playerRange.intersects(spawnRange)
        }
    }

    override fun affectAction(action: SpawnAction<*>) {
        if (action is PokemonSpawnAction && action.detail is PokemonSpawnDetail && action.props.level == null) {
            action.props.level = getPlayerLevelRange().intersection(action.detail.getDerivedLevelRange()).random()
        }
    }
}