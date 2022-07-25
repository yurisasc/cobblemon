package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.entity.LivingEntity
import net.minecraft.text.MutableText

/**
 * A [BattleActor] backed by a [LivingEntity].
 *
 * @param T The type of the [LivingEntity].
 *
 * @param entity The entity representing the actor, [LivingEntity.uuid] will be used for [BattleActor.uuid].
 * @param pokemonList The team the entity will use.
 */
abstract class EntityBattleActor<T : LivingEntity>(val entity: T, pokemonList: MutableList<BattlePokemon>) : BattleActor(entity.uuid, pokemonList) {

    override fun getName(): MutableText = this.entity.name.copy()

}