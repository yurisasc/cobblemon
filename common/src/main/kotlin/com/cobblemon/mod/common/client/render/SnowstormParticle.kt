/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render

import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.snowstorm.ParticleMaterial
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.cobblemon.mod.common.client.util.exists
import com.cobblemon.mod.common.util.resolveBoolean
import com.cobblemon.mod.common.util.resolveDouble
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import kotlin.math.abs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.ParticleTextureSheet.NO_RENDER
import net.minecraft.client.particle.ParticleTextureSheet.PARTICLE_SHEET_LIT
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.util.shape.VoxelShapes

class SnowstormParticle(
    val storm: ParticleStorm,
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    initialVelocity: Vec3d,
    var invisible: Boolean = false
) : Particle(world, x, y, z) {
    companion object {
        var mutablePos = BlockPos.Mutable(0, 0, 0)
        const val MAXIMUM_DISTANCE_CHANGE_PER_TICK_FOR_FRICTION = 0.005
    }

    val sprite = getSpriteFromAtlas()

    var deltaSeconds = 0F

    var ageSeconds = 0.0
    val particleTextureSheet: ParticleTextureSheet
    var angularVelocity = 0.0
    var colliding = false

    var texture = storm.effect.particle.texture

    var variableStruct = (storm.runtime.environment.structs["variable"] as VariableStruct);

    val random1 = variableStruct.map["particle_random_1"]
    val random2 = variableStruct.map["particle_random_2"]
    val random3 = variableStruct.map["particle_random_3"]
    val random4 = variableStruct.map["particle_random_4"]

    fun getSpriteFromAtlas(): Sprite {
        val atlas = MinecraftClient.getInstance().particleManager.particleAtlasTexture
        val sprite = atlas.getSprite(storm.effect.particle.texture)
        return sprite
    }

    private fun applyRandoms() {
        variableStruct.setDirectly("particle_random_1", random1)
        variableStruct.setDirectly("particle_random_2", random2)
        variableStruct.setDirectly("particle_random_3", random3)
        variableStruct.setDirectly("particle_random_4", random4)
    }

    init {
        setVelocity(initialVelocity.x, initialVelocity.y, initialVelocity.z)
        angle = storm.effect.particle.rotation.getInitialRotation(storm.runtime).toFloat()
        prevAngle = angle
        angularVelocity = storm.effect.particle.rotation.getInitialAngularVelocity(storm.runtime)
        velocityMultiplier = 1F
        storm.particles.add(this)
        gravityStrength = 0F
        particleTextureSheet = if (invisible) {
            NO_RENDER
        } else {
            PARTICLE_SHEET_LIT
        }
    }

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
        applyRandoms()
        setParticleAgeInRuntime()
        storm.effect.curves.forEach { it.apply(storm.runtime) }
        storm.runtime.execute(storm.effect.particle.renderExpressions)
//        RenderSystem.depthMask(true)
//        RenderSystem.enableBlend()
//        // TODO need to implement the other materials but not sure exactly what they are GL wise
////        RenderSystem.setShaderTexture(0, texture)
//        when (storm.effect.particle.material) {
//            ParticleMaterial.ALPHA -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
//            ParticleMaterial.OPAQUE -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ZERO)
//            ParticleMaterial.BLEND -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
//        }

        val lightMultiplier = if (storm.effect.particle.environmentLighting) {
            world.getLightLevel(mutablePos.set(x, y, z)) / 15F
        } else {
            1F
        }

        val tessellator = Tessellator.getInstance()

        vertexConsumer as BufferBuilder

//        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)

        val vec3d = camera.pos
        val f = (MathHelper.lerp(tickDelta.toDouble(), prevPosX, x) - vec3d.getX()).toFloat()
        val g = (MathHelper.lerp(tickDelta.toDouble(), prevPosY, y) - vec3d.getY()).toFloat()
        val h = (MathHelper.lerp(tickDelta.toDouble(), prevPosZ, z) - vec3d.getZ()).toFloat()
        val quaternion = storm.effect.particle.cameraMode.getRotation(prevAngle, angle, tickDelta, camera.rotation, camera.yaw, camera.pitch)

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
        val colour = storm.effect.particle.tinting.getTint(storm.runtime)
        colour.multiply(lightMultiplier)

        val spriteURange = sprite.maxU - sprite.minU
        val spriteVRange = sprite.maxV - sprite.minV

        val minU = uvs.startU * spriteURange + sprite.minU
        val maxU = uvs.endU * spriteURange + sprite.minU
        val minV = uvs.startV * spriteVRange + sprite.minV
        val maxV = uvs.endV * spriteVRange + sprite.minV

//        println("UVs: Us - $minU $maxU Vs - $minV $maxV")

        val p = getBrightness(tickDelta)
        vertexConsumer
            .vertex(particleVertices[0].x.toDouble(), particleVertices[0].y.toDouble(), particleVertices[0].z.toDouble())
            .texture(maxU, maxV)
            .color(colour.x, colour.y, colour.z, colour.w)
            .light(p)
            .next()
        vertexConsumer
            .vertex(particleVertices[1].x.toDouble(), particleVertices[1].y.toDouble(), particleVertices[1].z.toDouble())
            .texture(maxU, minV)
            .color(colour.x, colour.y, colour.z, colour.w)
            .light(p)
            .next()
        vertexConsumer
            .vertex(particleVertices[2].x.toDouble(), particleVertices[2].y.toDouble(), particleVertices[2].z.toDouble())
            .texture(minU, minV)
            .color(colour.x, colour.y, colour.z, colour.w)
            .light(p)
            .next()
        vertexConsumer
            .vertex(particleVertices[3].x.toDouble(), particleVertices[3].y.toDouble(), particleVertices[3].z.toDouble())
            .texture(minU, maxV)
            .color(colour.x, colour.y, colour.z, colour.w)
            .light(p)
            .next()

//        tessellator.draw()
    }

    override fun getMaxAge(): Int {
        return (storm.runtime.resolveDouble(storm.effect.particle.maxAge) * 20).toInt()
    }

    override fun tick() {
        applyRandoms()
        val deltaTicks = MinecraftClient.getInstance().tickDelta
        deltaSeconds = deltaTicks / 20F

        ageSeconds += deltaSeconds
        maxAge = getMaxAge()
        setParticleAgeInRuntime()

        storm.runtime.execute(storm.effect.particle.updateExpressions)
        angularVelocity += storm.effect.particle.rotation.getAngularAcceleration(storm.runtime, angularVelocity) / 20


        if (storm.runtime.resolveBoolean(storm.effect.particle.killExpression)) {
            maxAge = 0
            markDead()
            return
        } else {
            val acceleration = storm.effect.particle.motion.getAcceleration(storm.runtime, Vec3d(velocityX, velocityY, velocityZ)).multiply(1 / 20.0 * deltaTicks)
            velocityX += acceleration.x
            velocityY += acceleration.y
            velocityZ += acceleration.z

            prevAngle = angle
            angle = prevAngle + angularVelocity.toFloat()
        }

        prevPosX = x
        prevPosY = y
        prevPosZ = z

        this.move(velocityX, velocityY, velocityZ)
//        if (field_28787 && y == prevPosY) {
//            velocityX *= 1.1
//            velocityZ *= 1.1
//        }
//        velocityX *= velocityMultiplier.toDouble()
//        velocityY *= velocityMultiplier.toDouble()
//        velocityZ *= velocityMultiplier.toDouble()


//        if (onGround) {
//            velocityX *= 0.699999988079071
//            velocityZ *= 0.699999988079071
//        }
    }

    override fun move(dx: Double, dy: Double, dz: Double) {
        var dx = dx
        var dy = dy
        var dz = dz

        // field_21507 stoppedByCollision

        val collision = storm.effect.particle.collision
        val radius = storm.runtime.resolveDouble(collision.radius)
        if (storm.runtime.resolveBoolean(collision.enabled) && radius > 0.0) {
            collidesWithWorld = true
            boundingBox = Box.of(Vec3d(x, y, z), radius, radius, radius)

            val newMovement = checkCollision(Vec3d(dx, dy, dz))

            if (dead) {
                return
            }

            dx = newMovement.x
            dy = newMovement.y
            dz = newMovement.z

//            if (collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < 10000) {
//                val vec3d = Entity.adjustMovementForCollisions(
//                    null,
//                    Vec3d(dx, dy, dz),
//                    boundingBox,
//                    world,
//                    listOf()
//                )
//
//            }

            if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
                boundingBox = boundingBox.offset(dx, dy, dz)
                repositionFromBoundingBox()
            }

//            if (abs(dy) >= 9.999999747378752E-6 && abs(dy) < 9.999999747378752E-6) {
//                field_21507 = true
//            }
//            onGround = dy != dy && e < 0.0
//            if (d != dx) {
//                velocityX = 0.0
//            }
//            if (dz != dz) {
//                velocityZ = 0.0
//            }
        } else {
            collidesWithWorld =  false
            if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
                x += dx
                y += dy
                z += dz
            }
        }
    }

    private fun checkCollision(movement: Vec3d): Vec3d {
        val collision = storm.effect.particle.collision
        var box = boundingBox
        val bounciness = storm.runtime.resolveDouble(collision.bounciness)
        val friction = storm.runtime.resolveDouble(collision.friction)
        val expiresOnContact = collision.expiresOnContact

        val collisions = world.getBlockCollisions(null, box.stretch(movement))
        if (collisions.none()) {
            colliding = false
            return movement
        } else if (expiresOnContact) {
            markDead()
            return movement
        }

        var xMovement = movement.x
        var yMovement = movement.y
        var zMovement = movement.z

        var bouncing = false
        var sliding = false

        if (yMovement != 0.0) {
            yMovement = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, collisions, yMovement)
            if (yMovement != 0.0) {
                box = box.offset(0.0, yMovement, 0.0)
            } else {
                if (bounciness > 0.0 && movement.y > MAXIMUM_DISTANCE_CHANGE_PER_TICK_FOR_FRICTION) {
                    velocityY *= -1 * bounciness
                    yMovement = -1 * bounciness * movement.y
                    bouncing = true
                } else if (friction > 0.0) {
                    sliding = true
                }
            }
        }

        val bl = abs(xMovement) < abs(zMovement)
        if (bl && zMovement != 0.0) {
            zMovement = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, collisions, zMovement)
            if (zMovement != 0.0) {
                box = box.offset(0.0, 0.0, zMovement)
            } else {
                if (bounciness > 0.0 && movement.z > MAXIMUM_DISTANCE_CHANGE_PER_TICK_FOR_FRICTION) {
                    velocityZ *= -1 * bounciness
                    zMovement = -1 * bounciness * movement.z
                    bouncing = true
                } else if (friction > 0.0) {
                    sliding = true
                }
            }
        }

        if (xMovement != 0.0) {
            xMovement = VoxelShapes.calculateMaxOffset(Direction.Axis.X, box, collisions, xMovement)
            if (!bl && xMovement != 0.0) {
                box = box.offset(xMovement, 0.0, 0.0)
            } else {
                if (bounciness > 0.0 && movement.x > MAXIMUM_DISTANCE_CHANGE_PER_TICK_FOR_FRICTION) {
                    velocityX *= -1 * bounciness
                    xMovement = -1 * bounciness * movement.x
                    bouncing = true
                } else if (friction > 0.0) {
                    sliding = true
                }
            }
        }

        if (!bl && zMovement != 0.0) {
            zMovement = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, collisions, zMovement)
        } else if (!bl) {
            if (bounciness > 0.0 && movement.z > MAXIMUM_DISTANCE_CHANGE_PER_TICK_FOR_FRICTION) {
                velocityZ *= -1 * bounciness
                zMovement = -1 * bounciness * movement.z
                bouncing = true
            } else if (friction > 0.0) {
                sliding = true
            }
        }

        var newMovement = Vec3d(xMovement, yMovement, zMovement)

        if (sliding && !bouncing) {
            // If it's moving slower than the friction per second, time to stop
            newMovement = if (newMovement.length() * 20 < friction) {
                Vec3d.ZERO
            } else {
                newMovement.subtract(newMovement.normalize().multiply(friction / 20))
            }

            var velocity = Vec3d(velocityX, velocityY, velocityZ)
            if (velocity.length() * 20 < friction) {
                setVelocity(0.0, 0.0, 0.0)
            } else {
                velocity = velocity.subtract(velocity.normalize().multiply(friction / 20))
                setVelocity(velocity.x, velocity.y, velocity.z)
            }
        }

        return newMovement
    }


    private fun setParticleAgeInRuntime() {
        variableStruct.setDirectly("particle_age", DoubleValue(ageSeconds))
        variableStruct.setDirectly("particle_lifetime", DoubleValue(maxAge / 20.0))
    }

    override fun getType() = particleTextureSheet

    override fun markDead() {
        super.markDead()
        storm.particles.remove(this)
    }
}