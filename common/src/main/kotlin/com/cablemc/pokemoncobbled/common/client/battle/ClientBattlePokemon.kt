package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.pokemon.FormData
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import net.minecraft.text.MutableText
import java.util.UUID

class ClientBattlePokemon(
    val uuid: UUID,
    var displayName: MutableText,
    var properties: PokemonProperties,
    var hpRatio: Float,
    var status: PersistentStatus?,
    var statChanges: MutableMap<Stat, Int>
) {
    lateinit var actor: ClientBattleActor
    val species: Species
        get() = PokemonSpecies.getByName(properties.species!!)!!
}