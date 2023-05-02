package com.cobblemon.mod.common.api.moves.animations

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.util.toVec3d
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

class ActionEffectTimeline(
    var timeline: Map<Float, MoveAnimationKeyframe> = mapOf()
)

class ActionEffectPrompt(
    val names: Set<String>,
    val flags: Set<String>,
    val sourceEntityId: Int,
    val sourceLocator: String,
    val target: Vec3d?,
    val distance: Float = 4F
) {
    companion object {
        fun decode(buffer: PacketByteBuf): ActionEffectPrompt {
            return ActionEffectPrompt(
                names = buffer.readList { it.readString() }.toSet(),
                flags = buffer.readList { it.readString() }.toSet(),
                sourceEntityId = buffer.readInt(),
                sourceLocator = buffer.readString(),
                target = buffer.readNullable { buffer.readVector3f().toVec3d() },
                distance = buffer.readFloat()
            )
        }
    }

    fun encode(buffer: PacketByteBuf) {
        buffer.writeCollection(names) { _, v -> buffer.writeString(v) }
        buffer.writeCollection(flags) { _, v -> buffer.writeString(v) }
        buffer.writeInt(sourceEntityId)
        buffer.writeString(sourceLocator)
        buffer.writeNullable(target) { _, v -> buffer.writeVector3f(v.toVector3f()) }
        buffer.writeFloat(distance)
    }
}

class AnimationWithMoments(
    val animation: Set<String> = setOf("physical"),
    val moments: Map<AnimationMoment, ActionEffect> = mapOf()
)

class ActionEffect(
    val effect: Identifier? = null,
    val locator: Set<String> = setOf("root"),
    val sound: Identifier? = null,
    val variables: List<Expression> = listOf(),
)

class MoveAnimationKeyframe(
    val sound: Identifier? = null,
    val animation: List<AnimationWithMoments> = listOf(),
    val effects: List<ActionEffect> = listOf()
)
