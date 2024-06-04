package com.cobblemon.mod.common.util

import com.cobblemon.mod.common.Cobblemon
import net.minecraft.entity.EntityDimensions
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import kotlin.jvm.optionals.getOrNull

fun PacketByteBuf.readItemStack(): ItemStack {
    val dataResult = NbtOps.INSTANCE.withDecoder(ItemStack.CODEC).apply(this.readNbt()).result().getOrNull()
        ?: throw IllegalArgumentException("Failed to read item from packet")
    return dataResult.first
}

fun PacketByteBuf.writeItemStack(itemStack: ItemStack) {
    this.writeNbt(ItemStack.CODEC.encode(itemStack, NbtOps.INSTANCE, null).getOrThrow {
        return@getOrThrow IllegalArgumentException("Failed to encode item to nbt")
    })
}

fun PacketByteBuf.readText(): Text {
    return TextCodecs.PACKET_CODEC.decode(this)
}

fun PacketByteBuf.writeText(text: Text?) {
    TextCodecs.PACKET_CODEC.encode(this, text)
}

fun PacketByteBuf.readEntityDimensions(): EntityDimensions {
    val isFixed = this.readBoolean()
    return if (isFixed) {
        EntityDimensions.fixed(this.readFloat(), this.readFloat())
    }
    else {
        EntityDimensions.changing(this.readFloat(), this.readFloat())
    }
}