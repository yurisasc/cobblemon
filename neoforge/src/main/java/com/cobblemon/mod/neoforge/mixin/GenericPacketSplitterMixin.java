package com.cobblemon.mod.neoforge.mixin;

import com.cobblemon.mod.common.api.net.NetworkPacket;
import com.cobblemon.mod.common.api.net.UnsplittablePacket;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.neoforged.neoforge.network.filters.GenericPacketSplitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * See [UnsplittablePacket] for why this is necessary
 * I am concerned about how mappings will work with this, as the CustomPayloadS2CPacket stuff needs to be remapped
 * NeoForge stuff doesnt...
 */
@Mixin(GenericPacketSplitter.class)
public class GenericPacketSplitterMixin {

    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;Ljava/util/List;)V", at=@At("HEAD"), cancellable = true)
    public void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list, CallbackInfo ci) {
        if (packet instanceof CustomPayloadS2CPacket) {
            CustomPayloadS2CPacket pac = (CustomPayloadS2CPacket) packet;
            if (((CustomPayloadS2CPacket) packet).payload() instanceof UnsplittablePacket) {
                list.add(packet);
                ci.cancel();
                return;
            }
        }
        if (packet instanceof CustomPayloadC2SPacket) {
            CustomPayloadC2SPacket pac = (CustomPayloadC2SPacket) packet;
            if (((CustomPayloadC2SPacket) packet).payload() instanceof UnsplittablePacket) {
                list.add(packet);
                ci.cancel();
                return;
            }
        }
    }
}
