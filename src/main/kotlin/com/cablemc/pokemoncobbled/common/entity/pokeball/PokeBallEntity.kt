package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import com.cablemc.pokemoncobbled.common.pokemon.pokeball.PokeBall
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Rotation

abstract class PokeBallEntity(
    val pokeBall: PokeBall,
    entityType: EntityType<out PokeBallEntity>,
    level: Level
) : ThrowableItemProjectile(entityType, level) {

    override fun getDefaultItem(): Item = ItemRegistry.POKE_BALL.get()

}