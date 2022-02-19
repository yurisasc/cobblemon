package com.cablemc.pokemoncobbled.common.api.spawning.influence

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.spawning.detail.PokemonSpawnAction
import com.cablemc.pokemoncobbled.common.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.util.math.intersection
import com.cablemc.pokemoncobbled.common.util.math.intersects
import net.minecraft.server.level.ServerPlayer
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
    player: ServerPlayer,
    val variation: Int,
    val noPokemonRange: IntRange = 1 .. Pokemon.MAXIMUM_LEVEL,
    val recalculationMillis: Long = 5000L
) : SpawningInfluence {
    val uuid = player.uuid
    var lastCalculatedTime: Long = 0
    var previousRange: IntRange = noPokemonRange

    fun getPlayerLevelRange(): IntRange {
        if (System.currentTimeMillis() - lastCalculatedTime > recalculationMillis) {
            lastCalculatedTime = System.currentTimeMillis()

            val party = PokemonCobbled.storage.getParty(uuid)
            previousRange = if (party.any()) {
                val minimumLevel = party.minOf { it.level }
                val maximumLevel = party.maxOf { it.level }
                IntRange(max(minimumLevel - variation, 1), min(maximumLevel + variation, Pokemon.MAXIMUM_LEVEL))
            } else {
                noPokemonRange
            }
            return previousRange
        } else {
            return previousRange
        }
    }

    override fun affectSpawnable(detail: SpawnDetail): Boolean {
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