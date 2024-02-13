package com.cobblemon.mod.common.entity.fallingstar

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.text.text
import net.minecraft.entity.Entity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World

class FallingStarEntity(world: World) : Entity(CobblemonEntities.FALLING_STAR_ENTITY, world) {
    override fun initDataTracker() {
        TODO("Not yet implemented")
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
        TODO("Not yet implemented")
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
        TODO("Not yet implemented")
    }

    override fun tick() {
        val closestPlayer = world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, pos.x, pos.y, pos.z)
        closestPlayer?.sendMessage("hello bro".text())
        super.tick()
    }

    companion object {
        val CLOSE_PLAYER_PREDICATE: TargetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(3.0)
    }
}