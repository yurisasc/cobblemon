package com.cobblemon.mod.common.client.render

import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.snowstorm.ParticleMaterial
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.cobblemon.mod.common.util.resolveBoolean
import com.cobblemon.mod.common.util.resolveDouble
import com.cobblemon.mod.common.util.resolveInt
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
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f

class SnowstormParticle(
    val storm: ParticleStorm,
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    initialVelocity: Vec3d
) : Particle(world, x, y, z) {
    var ageSeconds = 0F

    init {
        setVelocity(initialVelocity.x, initialVelocity.y, initialVelocity.z)
    }

    val particleTextureSheet = object : ParticleTextureSheet {
        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            storm.runtime.environment.setValue("variable.particle_age", DoubleValue(ageSeconds))
            storm.runtime.execute(storm.effect.particle.renderExpressions)
            RenderSystem.depthMask(true)
            RenderSystem.setShaderTexture(0, storm.effect.particle.texture)
            RenderSystem.enableBlend()
            // TODO need to implement the other materials but not sure exactly what they are GL wise
            when (storm.effect.particle.material) {
                ParticleMaterial.ALPHA -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
                ParticleMaterial.OPAQUE -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ZERO)
                ParticleMaterial.BLEND -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
            }

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
        storm.runtime.environment.setValue("variable.particle_age", DoubleValue(ageSeconds))
        val vec3d = camera.pos
        val f = (MathHelper.lerp(tickDelta.toDouble(), prevPosX, x) - vec3d.getX()).toFloat()
        val g = (MathHelper.lerp(tickDelta.toDouble(), prevPosY, y) - vec3d.getY()).toFloat()
        val h = (MathHelper.lerp(tickDelta.toDouble(), prevPosZ, z) - vec3d.getZ()).toFloat()

        // This currently always faces the player. There are different settings that should be possible.
        val quaternion = if (angle == 0.0f) {
            camera.rotation
        } else {
            val i = MathHelper.lerp(tickDelta, prevAngle, angle)
            val q = Quaternion(camera.rotation)
            q.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(i))
            q
        }

        val vec3f = Vec3f(-1.0f, -1.0f, 0.0f)
        vec3f.rotate(quaternion)

        val xSize = storm.runtime.resolveDouble(storm.effect.particle.sizeX).toFloat()
        val ySize = storm.runtime.resolveDouble(storm.effect.particle.sizeY).toFloat()

        val particleVertices = arrayOf(
            Vec3f(-xSize/2, -ySize/2, 0.0f),
            Vec3f(-xSize/2, ySize/2, 0.0f),
            Vec3f(xSize/2, ySize/2, 0.0f),
            Vec3f(xSize/2, -ySize/2, 0.0f)
        )

        for (k in 0..3) {
            val vertex = particleVertices[k]
            vertex.rotate(quaternion)
            vertex.add(f, g, h)
        }

        val uvs = storm.effect.particle.uvMode.get(storm.runtime, ageSeconds, storm.effect.particle.maxAge)

        val minU: Float = uvs.startU.toFloat()
        val maxU: Float = (uvs.startU + uvs.uSize).toFloat()
        val minV: Float = uvs.startV.toFloat()
        val maxV: Float = (uvs.startV + uvs.vSize).toFloat()

        val p = getBrightness(tickDelta)
        vertexConsumer
            .vertex(particleVertices[0].x.toDouble(), particleVertices[0].y.toDouble(), particleVertices[0].z.toDouble())
            .texture(maxU, maxV)
            .color(red, green, blue, alpha)
            .light(p)
            .next()
        vertexConsumer
            .vertex(particleVertices[1].x.toDouble(), particleVertices[1].y.toDouble(), particleVertices[1].z.toDouble())
            .texture(maxU, minV)
            .color(red, green, blue, alpha)
            .light(p)
            .next()
        vertexConsumer
            .vertex(particleVertices[2].x.toDouble(), particleVertices[2].y.toDouble(), particleVertices[2].z.toDouble())
            .texture(minU, minV)
            .color(red, green, blue, alpha)
            .light(p)
            .next()
        vertexConsumer
            .vertex(particleVertices[3].x.toDouble(), particleVertices[3].y.toDouble(), particleVertices[3].z.toDouble())
            .texture(minU, maxV)
            .color(red, green, blue, alpha)
            .light(p)
            .next()
    }

    override fun getMaxAge(): Int {
        return super.getMaxAge()
    }

    override fun tick() {
        storm.runtime.environment.setValue("variable.particle_age", DoubleValue(ageSeconds))
        storm.runtime.execute(storm.effect.particle.updateExpressions)
        maxAge = storm.runtime.resolveInt(storm.effect.particle.maxAge)
        if (storm.runtime.resolveBoolean(storm.effect.particle.killExpression)) {
            maxAge = 0
        }

        super.tick()
    }

    override fun getType() = particleTextureSheet
}