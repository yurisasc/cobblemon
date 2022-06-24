package com.cablemc.pokemoncobbled.common.exception

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor

class IllegalActionChoiceException(val actor: BattleActor, message: String) : IllegalArgumentException(message)