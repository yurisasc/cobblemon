package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ModelAnimation
import com.cablemc.pokemoncobbled.common.entity.EntityProperty
import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.network.protocol.Packet
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.fmllegacy.network.NetworkHooks

abstract class PokeBallEntity(
    val pokeBall: PokeBall,
    entityType: EntityType<out PokeBallEntity>,
    level: Level
) : ThrowableItemProjectile(entityType, level) {

    val entityProperties = mutableListOf<EntityProperty<*>>()

    var currentAnimation: ModelAnimation<PokeBallModel>? = null
        protected set

    val delegate = if (level.isClientSide) {
        // Don't import because scanning for imports is a CI job we'll do later to detect errant access to client from server
        com.cablemc.pokemoncobbled.client.entity.PokeBallClientDelegate()
    } else {
        PokeBallServerDelegate()
    }

    override fun getDefaultItem(): Item = ItemRegistry.POKE_BALL.get()

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun tick() {
        super.tick()
        delegate.tick(this)
        entityProperties.forEach { it.checkForUpdate() }
    }

    fun <T> addEntityProperty(accessor: EntityDataAccessor<T>, initialValue: T): EntityProperty<T> {
        val property = EntityProperty(
            entityData = entityData,
            accessor = accessor,
            initialValue = initialValue
        )
        entityProperties.add(property)
        return property
    }
}