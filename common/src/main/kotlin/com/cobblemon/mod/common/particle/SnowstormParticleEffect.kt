package com.cobblemon.mod.common.particle

import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.cobblemon.mod.common.client.render.SnowstormParticle
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Codec
import net.minecraft.block.Block
import net.minecraft.command.argument.BlockArgumentParser
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class SnowstormParticleEffect(val effect: BedrockParticleEffect) : ParticleEffect {

    override fun getType(): ParticleType<*> {
        return CobblemonParticles.SNOWSTORM_PARTICLE_TYPE.get()
    }

    override fun write(buf: PacketByteBuf) {
        TODO("Not yet implemented")
    }

    override fun asString(): String {
        TODO("Not yet implemented")
    }

    companion object {
        val PARAMETERS_FACTORY: ParticleEffect.Factory<SnowstormParticleEffect> = object : ParticleEffect.Factory<SnowstormParticleEffect> {
            @Throws(CommandSyntaxException::class)
            override fun read(
                particleType: ParticleType<SnowstormParticleEffect>,
                stringReader: StringReader
            ): SnowstormParticleEffect {
                stringReader.expect(' ')
                // TODO load from file, probably.
                return SnowstormParticleEffect(
                    BedrockParticleEffect(Identifier(""))
//                    BlockArgumentParser.block(Registry.BLOCK, stringReader, false).blockState()
                )
            }

            override fun read(
                particleType: ParticleType<SnowstormParticleEffect?>,
                packetByteBuf: PacketByteBuf
            ): SnowstormParticleEffect {
                return SnowstormParticleEffect(
                    BedrockParticleEffect(Identifier(""))
                )
            }
        }
    }

}