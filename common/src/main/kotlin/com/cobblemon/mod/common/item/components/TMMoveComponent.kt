package com.cobblemon.mod.common.item.components

import com.cobblemon.mod.common.CobblemonItemComponents
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

data class TMMoveComponent(val move: MoveTemplate) {
    companion object {
        val CODEC: Codec<TMMoveComponent> = RecordCodecBuilder.create { builder -> builder.group(
            PrimitiveCodec.STRING.fieldOf("move").forGetter { it.move.name },
        ).apply(builder) { moveName -> TMMoveComponent(Moves.getByNameOrDummy(moveName)) } }

        val PACKET_CODEC: PacketCodec<ByteBuf, TMMoveComponent> = PacketCodecs.codec(CODEC)

        fun getTMMove(stack: ItemStack): MoveTemplate? {
            return stack.components.get(CobblemonItemComponents.TM_MOVE)?.move
        }

        fun setTMMove(stack: ItemStack, move: MoveTemplate): ItemStack {
            stack.set(CobblemonItemComponents.TM_MOVE, TMMoveComponent(move))
            return stack
        }

        fun removeTMMove(stack: ItemStack): ItemStack {
            stack.remove(CobblemonItemComponents.TM_MOVE)
            return stack
        }

        fun getItemColor(stack: ItemStack, tint: Int): Int {
            val moveType = getTMMove(stack)?.elementalType ?: ElementalTypes.NORMAL
            return if (tint == 0) moveType.primaryColor else moveType.secondaryColor
        }

        fun createStack(move: MoveTemplate): ItemStack {
            val stack = ItemStack(CobblemonItems.TECHNICAL_MACHINE)
            return setTMMove(stack, move)
        }
    }
}