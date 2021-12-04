package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ShakeAnimation
import com.cablemc.pokemoncobbled.common.entity.animation.AnimationController
import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fmllegacy.network.NetworkHooks

abstract class PokeBallEntity(
    val pokeBall: PokeBall,
    entityType: EntityType<out PokeBallEntity>,
    level: Level
) : ThrowableItemProjectile(entityType, level) {

    val animationController = AnimationController<PokeBallEntity>()
    val shakeAnimation = ShakeAnimation()

    init {
        animationController.registerAnimation(EventPriority.HIGH, ShakeAnimation()) { true }
    }

    override fun getDefaultItem(): Item = ItemRegistry.POKE_BALL.get()

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun tick() {
        setDeltaMovement(0.0, 0.0, 0.0)
        super.tick()
    }

}