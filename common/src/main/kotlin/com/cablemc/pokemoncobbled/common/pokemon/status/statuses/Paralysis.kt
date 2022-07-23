package com.cablemc.pokemoncobbled.common.pokemon.status.statuses

import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.cobbledResource

class Paralysis : PersistentStatus(name = cobbledResource("paralysis"), showdownName = "par", defaultDuration = IntRange(180, 300)) {

}