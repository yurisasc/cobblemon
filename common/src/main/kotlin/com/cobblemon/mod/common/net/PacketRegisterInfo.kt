package com.cobblemon.mod.common.net

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.PacketHandler
import net.minecraft.network.RegistryByteBuf
import net.minecraft.util.Identifier

data class PacketRegisterInfo<T : NetworkPacket<T>>(
    val id: Identifier,
    val decoder: (RegistryByteBuf) -> T,
    val handler: PacketHandler<T>
)