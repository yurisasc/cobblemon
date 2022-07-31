package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

class ActiveBattlePokemon(val actor: BattleActor, var battlePokemon: BattlePokemon? = null): Targetable {
    val battle = actor.battle
    fun getSide() = actor.getSide()
    var position: Pair<ServerWorld, Vec3d>? = null

    override fun getAllActivePokemon() = battle.activePokemon
    override fun getActorShowdownId() = actor.showdownId
    override fun getActorPokemon() = actor.activePokemon
    override fun getSidePokemon() = getSide().activePokemon
    override fun getFormat() = battle.format
    override fun isAllied(other: Targetable) = getSide() == (other as ActiveBattlePokemon).getSide()
    override fun hasPokemon() = battlePokemon != null
    fun isGone() = battlePokemon?.gone ?: true
    fun isAlive() = (battlePokemon?.health ?: 0) > 0
}