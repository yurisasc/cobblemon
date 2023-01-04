package com.cobblemon.mod.common.client.render

import com.cobblemon.mod.common.api.snowstorm.BedrockParticle
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.world.ClientWorld


class SnowstormParticle(val storm: ParticleStorm, world: ClientWorld, x: Double, y: Double, z: Double) : Particle(world, x, y, z) {
    var ageSeconds = 0F

    val particleTextureSheet = object : ParticleTextureSheet {
        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            RenderSystem.depthMask(true)
            RenderSystem.setShaderTexture(0, textureManager.getTexture(storm.effect.particle))
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun draw(tessellator: Tessellator) {
            tessellator.draw()
        }

        override fun toString(): String {
            return "SNOWSTORM_PARTICLE"
        }
    }

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
    }

    override fun getMaxAge(): Int {
        return super.getMaxAge()
    }

    override fun tick() {
        super.tick()
    }

    override fun getType() = particleTextureSheet
}