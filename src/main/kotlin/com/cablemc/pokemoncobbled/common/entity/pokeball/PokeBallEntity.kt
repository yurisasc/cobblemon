package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.entity.EntityProperty
import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.network.protocol.Packet
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.fmllegacy.network.NetworkHooks

abstract class PokeBallEntity(
    val pokeBall: PokeBall,
    entityType: EntityType<out PokeBallEntity>,
    level: Level
) : ThrowableItemProjectile(entityType, level) {
    val DIMENSIONS = EntityDimensions(0.4F, 0.4F, true)
    val entityProperties = mutableListOf<EntityProperty<*>>()

    abstract val delegate: PokeBallDelegate<out PokeBallEntity>

    override fun getDefaultItem(): Item = ItemRegistry.POKE_BALL.get()

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun tick() {
        super.tick()
        entityProperties.forEach { it.checkForUpdate() }
    }

    override fun getDimensions(pPose: Pose) = DIMENSIONS
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