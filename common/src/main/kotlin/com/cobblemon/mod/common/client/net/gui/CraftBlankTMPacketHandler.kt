package com.cobblemon.mod.common.client.net.gui

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.net.messages.client.ui.CraftBlankTMPacket
import com.cobblemon.mod.common.util.playSoundServer
import net.minecraft.item.Items
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory

object CraftBlankTMPacketHandler : ServerNetworkPacketHandler<CraftBlankTMPacket> {
    override fun handle(packet: CraftBlankTMPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val screen = player.currentScreenHandler as TMMScreenHandler
        val ingredientSlot = screen.input.getStack(2)

        if (ingredientSlot.isOf(Items.AMETHYST_SHARD) && ingredientSlot.count >= 1) {
            screen.input.removeStack(2, 1)
            screen.result.setStack(0, CobblemonItems.BLANK_TM.defaultStack)

            screen.input.markDirty()
            screen.result.markDirty()
            player.currentScreenHandler.syncState()

            player.world.playSoundServer(player.pos, CobblemonSounds.TMM_CRAFT_BLANK, SoundCategory.BLOCKS)
        }
    }
}