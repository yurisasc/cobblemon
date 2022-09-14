package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import net.minecraft.entity.LivingEntity

/**
 * Allows a [BattleActor] to attach a [LivingEntity] to itself.
 *
 * @param T The type of [LivingEntity].
 */
interface EntityBackedBattleActor<T : LivingEntity> {

    /**
     * The [LivingEntity] attached to the [BattleActor].
     */
    val entity: T?

}