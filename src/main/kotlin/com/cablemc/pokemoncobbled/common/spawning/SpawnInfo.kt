package com.cablemc.pokemoncobbled.common.spawning

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

data class SpawnInfo(
    val player: Player,
    var spawnPos: Vec3
)
