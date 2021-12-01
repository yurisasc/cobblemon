package com.cablemc.pokemoncobbled.common.event

import com.cablemc.pokemoncobbled.common.entity.EntityRegistry.POKEMON
import com.cablemc.pokemoncobbled.common.util.isPokemonEntity
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object InteractListener {

    @SubscribeEvent
    fun onInteract(event : PlayerInteractEvent) {
        if(event.player.level.isClientSide) return

        if(event is PlayerInteractEvent.RightClickItem || event is PlayerInteractEvent.RightClickBlock) {
            if(event.player.isCrouching) {
                if (event.player.shoulderEntityLeft.isPokemonEntity()) {
                    event.player.respawnEntityOnShoulder(event.player.shoulderEntityLeft)
                    event.player.shoulderEntityLeft = CompoundTag()
                }
                if (event.player.shoulderEntityRight.isPokemonEntity()) {
                    event.player.respawnEntityOnShoulder(event.player.shoulderEntityRight)
                    event.player.shoulderEntityRight = CompoundTag()
                }
            }
        }
    }

}