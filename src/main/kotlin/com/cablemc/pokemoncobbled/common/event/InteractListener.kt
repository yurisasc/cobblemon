package com.cablemc.pokemoncobbled.common.event

import com.cablemc.pokemoncobbled.common.entity.EntityRegistry.POKEMON
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object InteractListener {

    @SubscribeEvent
    fun onInteract(event : PlayerInteractEvent) {
        if(event is PlayerInteractEvent.RightClickEmpty || event is PlayerInteractEvent.RightClickItem || event is PlayerInteractEvent.RightClickBlock) {
            if(event.player.isCrouching) {
                if (event.player.shoulderEntityLeft.getString("id") == POKEMON.id.toString()) {
                    event.player.respawnEntityOnShoulder(event.player.shoulderEntityLeft)
                    event.player.shoulderEntityLeft = CompoundTag()
                }
                if (event.player.shoulderEntityRight.getString("id") == POKEMON.id.toString()) {
                    event.player.respawnEntityOnShoulder(event.player.shoulderEntityRight)
                    event.player.shoulderEntityRight = CompoundTag()
                }
            }
        }
    }

}