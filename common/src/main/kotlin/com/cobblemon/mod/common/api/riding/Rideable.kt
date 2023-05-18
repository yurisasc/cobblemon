package com.cobblemon.mod.common.api.riding

import net.minecraft.entity.player.PlayerEntity

interface Rideable {

    fun canRide(player: PlayerEntity): Boolean


}