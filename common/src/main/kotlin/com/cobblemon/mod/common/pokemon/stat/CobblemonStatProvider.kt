/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.stat

import com.cobblemon.mod.common.api.pokemon.stats.*
import com.cobblemon.mod.common.pokemon.*
import com.cobblemon.mod.common.pokemon.adapters.CobblemonStatTypeAdapter
import net.minecraft.util.Identifier
import kotlin.random.Random

/**
 * The default implementation of a [StatProvider].
 *
 * @author Licious
 * @since November 6th, 2022
 */
object CobblemonStatProvider : StatProvider {

    override val statNetworkSerializer = CobblemonStatNetworkSerializer
    override val typeAdapter: StatTypeAdapter = CobblemonStatTypeAdapter
    private val stats = Stats.values().associateBy { it.identifier }

    override fun ofType(type: Stat.Type): Collection<Stat> = when(type) {
        Stat.Type.BATTLE_ONLY -> Stats.BATTLE_ONLY
        Stat.Type.PERMANENT -> Stats.PERMANENT
    }

    override fun provide(species: Species) {
        this.allocate(species.baseStats)
    }

    override fun provide(form: FormData) {
        form._baseStats?.let { this.allocate(it) }
    }

    override fun toShowdown(species: Species, form: FormData?): String {
        val baseStats = form?.baseStats ?: species.baseStats
        return "baseStats: { hp: ${baseStats[Stats.HP]}, atk: ${baseStats[Stats.ATTACK]}, def: ${baseStats[Stats.DEFENCE]}, spa: ${baseStats[Stats.SPECIAL_ATTACK]}, spd: ${baseStats[Stats.SPECIAL_DEFENCE]}, spe: ${baseStats[Stats.SPEED]} }"
    }

    override fun createEmptyEVs(): EVs {
        val evs = EVs()
        this.ofType(Stat.Type.PERMANENT).forEach { stat ->
            evs[stat] = evs.defaultValue
        }
        return evs
    }

    override fun createEmptyIVs(minPerfectIVs: Int): IVs {
        val ivs = IVs()

        // Initialize base random values
        for (stat in this.ofType(Stat.Type.PERMANENT)) {
            ivs[stat] = Random.nextInt(IVs.MAX_VALUE)
        }

        // Add in minimum perfect IVs
        if (minPerfectIVs > 0) {
            val perfectStats = this.ofType(Stat.Type.PERMANENT).shuffled().take(minPerfectIVs)
            for (stat in perfectStats) {
                ivs[stat] = IVs.MAX_VALUE
            }
        }
        return ivs
    }

    override fun getStatForPokemon(pokemon: Pokemon, stat: Stat): Int {
        return if (stat == Stats.HP) {
            if (pokemon.species.resourceIdentifier == Pokemon.SHEDINJA) {
                1
            } else {
                (2 * pokemon.form.baseStats[Stats.HP]!! + pokemon.ivs[Stats.HP]!! + (pokemon.evs[Stats.HP]!! / 4)) * pokemon.level / 100 + pokemon.level + 10
            }
        } else {
            pokemon.nature.modifyStat(stat, (2 * (pokemon.form.baseStats[stat] ?: 1) + pokemon.ivs.getOrDefault(stat) + pokemon.evs.getOrDefault(stat) / 4) / 100 * pokemon.level + 5)
        }
    }

    override fun fromIdentifier(identifier: Identifier): Stat? = this.stats[identifier]

    override fun fromIdentifierOrThrow(identifier: Identifier): Stat = this.fromIdentifier(identifier) ?: throw IllegalArgumentException("No stat was found with the identifier $identifier")

    private fun allocate(map: MutableMap<Stat, Int>) {
        Stats.PERMANENT.forEach { stat ->
            map.putIfAbsent(stat, 1)
        }
    }

}