package com.cobblemon.mod.common.entity.throwable

import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World


class StickyGlobEntity : ThrownItemEntity {
    constructor(entityType: EntityType<out StickyGlobEntity?>?, world: World?) : super(entityType, world)

    constructor(world: World?, owner: LivingEntity?) : super(EntityType.SNOWBALL, owner, world)

    constructor(world: World?, x: Double, y: Double, z: Double) : super(EntityType.SNOWBALL, x, y, z, world)

    override fun getDefaultItem(): Item {
        return Items.SNOWBALL
    }

    private val particleParameters: ParticleEffect
        get() {
            val itemStack = this.item
            return (if (itemStack.isEmpty) ParticleTypes.ITEM_SNOWBALL else ItemStackParticleEffect(
                ParticleTypes.ITEM,
                itemStack
            )) as ParticleEffect
        }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 3) {
            val particleEffect = this.particleParameters

            for (i in 0..7) {
                world.addParticle(particleEffect, this.x, this.y, this.z, 0.0, 0.0, 0.0)
            }
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        world.playSound(
            null as PlayerEntity?,
            this.x,
            this.y,
            this.z,
            SoundEvents.ENTITY_SLIME_SQUISH,
            SoundCategory.NEUTRAL,
            0.6f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )

        super.onEntityHit(entityHitResult)
        val entity = entityHitResult.entity
        val i = if (entity is BlazeEntity) 3 else 0
        entity.damage(this.damageSources.thrown(this, this.owner), i.toFloat())
    }

    override fun onCollision(hitResult: HitResult) {
        world.playSound(
            null as PlayerEntity?,
            this.x,
            this.y,
            this.z,
            SoundEvents.ENTITY_SLIME_SQUISH,
            SoundCategory.NEUTRAL,
            0.6f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )

        super.onCollision(hitResult)
        if (!world.isClient) {
            world.sendEntityStatus(this, 3.toByte())
            this.discard()
        }
    }
}
