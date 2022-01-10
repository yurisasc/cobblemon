package com.cablemc.pokemoncobbled.common.event

import com.cablemc.pokemoncobbled.common.util.isPokemonEntity
import com.cablemc.pokemoncobbled.mod.config.CobbledConfig
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object InteractListener {

    @SubscribeEvent
    fun onInteract(event : PlayerInteractEvent) {
        if (event.player.level.isClientSide) return

        if (event is PlayerInteractEvent.RightClickItem || event is PlayerInteractEvent.RightClickBlock) {
            // TODO: Remove as just testing config
            event.player.sendMessage(TextComponent(CobbledConfig.testString!!.get()), Util.NIL_UUID)

            if (event.player.isCrouching) {
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