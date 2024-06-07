package com.cobblemon.mod.common.util

import io.netty.buffer.ByteBuf
import net.minecraft.entity.EntityDimensions
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.encoding.StringEncoding
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

fun <T> ByteBuf.writeCollection(collection: Collection<T> , writer: (ByteBuf, T) -> Unit) {
    this.writeInt(collection.size)
    collection.forEach {
        writer(this, it)
    }
}

fun <T> ByteBuf.writeNullable(obj: T?, writer: (ByteBuf, T) -> Unit) {
    this.writeBoolean(obj == null)
    obj?.let {
        writer(this, it)
    }
}

fun ByteBuf.writeString(string: String): ByteBuf {
    StringEncoding.encode(this, string, 32767)
    return this
}

fun <T> ByteBuf.readCollection(reader: (ByteBuf) -> T): List<T> {
    val numElements = this.readInt()
    val collection = mutableListOf<T>()
    repeat(numElements) {
        collection.add(reader.invoke(this))
    }
    return collection
}

fun <T> ByteBuf.readList(reader: (ByteBuf) -> T) = readCollection(reader)

fun ByteBuf.readString():String {
    return StringEncoding.decode(this, 32767)
}

fun <T> ByteBuf.readNullable(reader: (ByteBuf) -> T): T? {
    val isPresent = this.readBoolean()
    if (isPresent) {
        return reader.invoke(this)
    }
    return null
}