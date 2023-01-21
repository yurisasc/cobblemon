package com.cobblemon.mod.common.particle

import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.particle.ParticleType

class SnowstormParticleType : ParticleType<SnowstormParticleEffect>(false, SnowstormParticleEffect.PARAMETERS_FACTORY) {
    companion object {
        val CODEC: Codec<SnowstormParticleEffect> = RecordCodecBuilder.create { instance ->
            instance.group(
                BedrockParticleEffect.CODEC.fieldOf("effect").forGetter { it.effect }
            ).apply(instance, ::SnowstormParticleEffect)
        }
    }

    override fun getCodec() = CODEC

}