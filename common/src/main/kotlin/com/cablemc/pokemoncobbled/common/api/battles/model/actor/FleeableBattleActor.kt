package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

interface FleeableBattleActor {
    val fleeDistance: Float
    fun getWorldAndPosition(): Pair<ServerWorld, Vec3d>?
}