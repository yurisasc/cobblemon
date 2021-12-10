package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ModelAnimation
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.OpenAnimation
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ShakeAnimation
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.SpinAnimation
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

    var currentAnimation: ModelAnimation<PokeBallModel>? = null
        protected set

    init {
        currentAnimation = SpinAnimation()
    }

    override fun getDefaultItem(): Item = ItemRegistry.POKE_BALL.get()

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun tick() {
        super.tick()
    }

}