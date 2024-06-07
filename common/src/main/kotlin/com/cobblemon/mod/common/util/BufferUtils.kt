package com.cobblemon.mod.common.util

import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.net.IntSize
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.handler.codec.EncoderException
import net.minecraft.entity.EntityDimensions
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtEnd
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketEncoder
import net.minecraft.network.encoding.StringEncoding
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.io.IOException
import java.util.BitSet
import java.util.EnumSet
import java.util.UUID
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

fun ByteBuf.readText(): Text {
    return TextCodecs.PACKET_CODEC.decode(this)
}

fun ByteBuf.writeText(text: Text?) {
    TextCodecs.PACKET_CODEC.encode(this, text)
}

fun ByteBuf.readEntityDimensions(): EntityDimensions {
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

fun <T> ByteBuf.writeNullable(obj: T?, writer: PacketEncoder<ByteBuf, T>) {
    writeNullable(obj) { buf, otherObj ->
        writer.encode(buf, otherObj)
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

fun <K, V> ByteBuf.writeMap(map: Map<K, V>, keyWriter: (ByteBuf, K) -> Unit, valueWriter: (ByteBuf, V) -> Unit) {
    this.writeInt(map.size)
    map.forEach { (key, value) ->
        keyWriter(this, key)
        valueWriter(this, value)
    }
}

fun <K, V> ByteBuf.readMap(keyReader: (ByteBuf) -> K, valueReader: (ByteBuf) -> V): Map<K, V> {
    val map = mutableMapOf<K, V>()
    val numElements = readInt()
    repeat(numElements) {
        val key = keyReader.invoke(this)
        val value = valueReader.invoke(this)
        map[key] = value
    }
    return map
}


fun ByteBuf.readIdentifier(): Identifier {
    val str = this.readString()
    //If this is null we should be using writeNullable anyway
    return Identifier.tryParse(str)!!
}

fun ByteBuf.writeIdentifier(id: Identifier) {
    writeString(id.toString())
}

fun ByteBuf.writePartyPosition(partyPosition: PartyPosition) {
    writeSizedInt(IntSize.U_SHORT, partyPosition.slot)
}

fun ByteBuf.readPartyPosition() = PartyPosition(readSizedInt(IntSize.U_SHORT))

fun ByteBuf.writePCPosition(pcPosition: PCPosition) {
    writeSizedInt(IntSize.U_SHORT, pcPosition.box)
    writeSizedInt(IntSize.U_BYTE, pcPosition.slot)
}

fun ByteBuf.readPCPosition() = PCPosition(readSizedInt(IntSize.U_SHORT), readSizedInt(IntSize.U_BYTE))

fun ByteBuf.writeUuid(uuid: UUID) {
    writeLong(uuid.mostSignificantBits)
    writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readUuid() = UUID(readLong(), readLong())

fun ByteBuf.writeEnumConstant(value: Enum<*>) {
    this.writeInt(value.ordinal)
}

fun <T : Enum<T>> ByteBuf.readEnumConstant(clazz: Class<T>): T {
    return clazz.getEnumConstants()[this.readInt()]
}

fun ByteBuf.writeNbt(nbt: NbtElement) {
    if (nbt == null) {
        NbtIo.writeForPacket(NbtEnd.INSTANCE, ByteBufOutputStream(this))
    }

    try {
        NbtIo.writeForPacket(nbt, ByteBufOutputStream(this))
    } catch (var3: IOException) {
        val iOException = var3
        throw EncoderException(iOException)
    }
}

fun ByteBuf.readNbt(): NbtElement? {
    val iOException: IOException
    val nbtElement: NbtElement
    try {
        nbtElement = NbtIo.read(ByteBufInputStream(this), NbtSizeTracker.of(2097152L))
        if (nbtElement.type.toInt() == 0) {
            return null
        }
    } catch (var4: IOException) {
        iOException = var4
        throw EncoderException(iOException)
    }

    try {
        return nbtElement
    } catch (var3: IOException) {
        iOException = var3
        throw EncoderException(iOException)
    }
}

fun <E : Enum<E>> ByteBuf.writeEnumSet(enumSet: EnumSet<E>, type: Class<E>) {
    val enums: Array<E> = type.enumConstants as Array<E>
    val bitSet = BitSet(enums.size)

    for (i in enums.indices) {
        bitSet[i] = enumSet.contains(enums[i])
    }

    this.writeBitSet(bitSet, enums.size)
}

fun ByteBuf.writeBitSet(bitSet: BitSet, size: Int) {
    if (bitSet.length() > size) {
        val var10002 = bitSet.length()
        throw EncoderException("BitSet is larger than expected size ($var10002>$size)")
    } else {
        val bs = bitSet.toByteArray()
        this.writeBytes(bs.copyOf(MathHelper.ceilDiv(size, 8)))
    }
}

