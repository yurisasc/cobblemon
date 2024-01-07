package com.cobblemon.mod.common.net.messages.client.ui

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to attempt crafting a Blank TM using the [TMBlock]
 *
 * Handled by [CraftBlankTMPacketHandler]
 *
 * @author whatsy
 */
class CraftBlankTMPacket(
    val ingredient: ItemStack
): NetworkPacket<CraftBlankTMPacket> {
    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeItemStack(ingredient)
    }

    companion object {
        val ID = cobblemonResource("craft_blank_tm")

        fun decode(buffer: PacketByteBuf) = CraftBlankTMPacket(
            buffer.readItemStack()
        )
    }

}