package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.Battle
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BattleRegistry() {

    private val battleMap: ConcurrentHashMap<UUID, Battle> = ConcurrentHashMap()

    fun startBattle() {

    }

}