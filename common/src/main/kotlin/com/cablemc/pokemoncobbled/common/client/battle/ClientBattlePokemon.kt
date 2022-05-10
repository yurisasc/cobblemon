package com.cablemc.pokemoncobbled.common.client.battle

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
    var species: Species,
    var form: FormData,
    var level: Int,
    var gender: Gender,
    var hpRatio: Float,
    var status: PersistentStatus?,
    var statChanges: MutableMap<Stat, Int>
) {
    lateinit var actor: ClientBattleActor
}