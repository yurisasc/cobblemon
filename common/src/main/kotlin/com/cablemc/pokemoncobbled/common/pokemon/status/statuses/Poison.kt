package com.cablemc.pokemoncobbled.common.pokemon.status.statuses

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.math.max
import kotlin.math.round
import kotlin.random.Random

class Poison : PersistentStatus(name = cobbledResource("poison"), defaultDuration = IntRange(180, 300)) {
    override fun onSecondPassed(player: ServerPlayerEntity, pokemon: Pokemon, random: Random) {
        // 1 in 15 chance to damage 5% of their HP with a minimum of 1
        if(!pokemon.isFainted() && random.nextInt(15) == 0) {
            pokemon.currentHealth -= max(1, round(pokemon.hp * 0.05).toInt())
        }
    }
}