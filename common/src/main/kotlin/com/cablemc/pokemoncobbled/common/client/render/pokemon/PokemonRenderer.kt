package com.cablemc.pokemoncobbled.common.client.render.pokemon

import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate.Companion.BEAM_EXTEND_TIME
import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate.Companion.BEAM_SHRINK_TIME
import com.cablemc.pokemoncobbled.common.client.render.addVertex
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cablemc.pokemoncobbled.common.client.render.renderBeaconBeam
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import com.mojang.math.Vector4f
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.util.Mth.PI
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import kotlin.math.min
import kotlin.math.tan

class PokemonRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<PokemonEntity, EntityModel<PokemonEntity>>(context, null, 0.5f) {
    companion object {
        var DELTA_TICKS = 0F
        val glowLengthFunction = parabolaFunction(
            peak = 1F,
            period = 1F
        )
    }

    override fun getTextureLocation(pEntity: PokemonEntity) = PokemonModelRepository.getModelTexture(pEntity.pokemon)
    override fun render(entity: PokemonEntity, pEntityYaw: Float, partialTicks: Float, poseMatrix: PoseStack, buffer: MultiBufferSource, pPackedLight: Int) {
        DELTA_TICKS = partialTicks // TODO move this somewhere universal
        model = PokemonModelRepository.getModel(entity.pokemon).entityModel

        val clientDelegate = entity.delegate as PokemonClientDelegate
        val beamMode = entity.beamModeEmitter.get().toInt()
        val modelNow = model
        val s = clientDelegate.secondsSinceBeamEffectStarted
        if (modelNow is PokemonPoseableModel && beamMode != 0) {
            if (s > BEAM_EXTEND_TIME) {
                val value = (s - BEAM_EXTEND_TIME) /  BEAM_SHRINK_TIME
                val colourValue = if (beamMode == 1) {
                    0.4F + min(0.6F, value)
                } else {
                    1F - min(0.6F, value)
                }

                modelNow.green = colourValue
                modelNow.blue = colourValue
            }
        }

        val phaseTarget = clientDelegate.phaseTarget
        if (phaseTarget != null && beamMode != 0) {
            renderBeam(poseMatrix, partialTicks, entity, phaseTarget, buffer)
        }

        super.render(entity, pEntityYaw, partialTicks, poseMatrix, buffer, pPackedLight)

        if (modelNow is PokemonPoseableModel) {
            modelNow.green = 1F
            modelNow.blue = 1F
        }

        if (phaseTarget != null && beamMode != 0) {
            val glowMultiplier = if (s > BEAM_EXTEND_TIME && s < BEAM_EXTEND_TIME + BEAM_SHRINK_TIME) {
                val t = (s - BEAM_EXTEND_TIME) / BEAM_SHRINK_TIME
                glowLengthFunction(t)
            } else {
                0F
            }

            if (glowMultiplier > 0F) {
                renderGlow(
                    matrixStack = poseMatrix,
                    entity = entity,
                    buffer = buffer,
                    red = 1F,
                    green = 0F,
                    blue = 0F,
                    alpha = 1F,
                    glowLength = glowMultiplier * entity.bbWidth * 1.5F,
                    glowRangeAngle = PI / 7
                )
            }
        }
    }

    override fun scale(pEntity: PokemonEntity, pMatrixStack: PoseStack, pPartialTickTime: Float) {
        val scale = pEntity.pokemon.form.baseScale * pEntity.pokemon.scaleModifier * (pEntity.delegate as PokemonClientDelegate).entityScaleModifier
        pMatrixStack.scale(scale, scale, scale)
    }

    fun renderBeam(matrixStack: PoseStack, partialTicks: Float, entity: PokemonEntity, beamTarget: Entity, buffer: MultiBufferSource) {
        val pokemonPosition = entity.position().add(0.0, entity.bbHeight / 2.0, 0.0)
        val beamSourcePosition = if (beamTarget is EmptyPokeBallEntity) {
            beamTarget.position().let { it.add(pokemonPosition.subtract(it).normalize().multiply(0.4, 0.0, 0.4)) }
        } else {
            beamTarget as Player
            if (beamTarget.uuid == Minecraft.getInstance().player?.uuid) {
                val lookVec = beamTarget.lookAngle.yRot(PI / 2).multiply(1.0, 0.0, 1.0).normalize()
                beamTarget.getEyePosition(partialTicks).subtract(0.0, 0.4, 0.0).subtract(lookVec.scale(0.3))
            } else {
                val lookVec = beamTarget.lookAngle.yRot(PI / 2 - (beamTarget.yBodyRot - beamTarget.yHeadRot).toRadians()).multiply(1.0, 0.0, 1.0).normalize()
                beamTarget.getEyePosition(partialTicks).subtract(0.0, 0.7, 0.0).subtract(lookVec.scale(0.4))
            }
        }


        if (beamSourcePosition.distanceTo(pokemonPosition) > 20) {
            return
        }

        val clientDelegate = entity.delegate as PokemonClientDelegate

        val direction = Vector3f(pokemonPosition.subtract(beamSourcePosition))

        matrixStack.pushPose()
        with(beamSourcePosition.subtract(entity.position())) { matrixStack.translate(x, y, z) }

        val s = clientDelegate.secondsSinceBeamEffectStarted
        val ratio = if (s < BEAM_EXTEND_TIME) {
            s / BEAM_EXTEND_TIME
        } else if (s > BEAM_EXTEND_TIME + BEAM_SHRINK_TIME) {
            1 - min((s - BEAM_EXTEND_TIME - BEAM_SHRINK_TIME) / BEAM_EXTEND_TIME, 1F)
        } else {
            1F
        }

        direction.normalize()

        val yAxis = Vector3f.YP.copy()
        val dot = direction.dot(yAxis)
        val cross = yAxis.copy()
        cross.cross(direction)
        val q = Quaternion(cross.x(), cross.y(), cross.z(), 1 + dot)
        q.normalize()
        matrixStack.mulPose(q)

        renderBeaconBeam(
            matrixStack = matrixStack,
            buffer = buffer,
            partialTicks = partialTicks,
            totalLevelTime = entity.level.gameTime,
            height = pokemonPosition.distanceTo(beamSourcePosition).toFloat() * ratio,
            red = 1F,
            green = 0.1F,
            blue = 0.1F,
            alpha = 1F,
            beamRadius = 0.03F,
            glowRadius = 0.07F,
            glowAlpha = 0.4F
        )

        matrixStack.popPose()
    }

    fun renderGlow(
        matrixStack: PoseStack,
        entity: PokemonEntity,
        buffer: MultiBufferSource,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        glowLength: Float,
        glowRangeAngle: Float
    ) {
        val totalWorldTicks = entity.level.gameTime
        val vectorBuffer = buffer.getBuffer(RenderType.lightning()) //buffer.getBuffer(RenderType.glint())

        val ray1YRot = (totalWorldTicks + DELTA_TICKS) / 16F

        val startY1 = entity.boundingBox.ysize.toFloat() * 0.5F
        val startY2 = startY1 + entity.boundingBox.ysize.toFloat() * 0.05F

        val endY1 = startY1 - tan(glowRangeAngle) * glowLength
        val endY2 = startY2 + tan(glowRangeAngle) * glowLength

        val startX = 0F
        val endX = startX + glowLength

        // Draw 4 rays of red.
        repeat(times = 4) {
            matrixStack.pushPose()

            val last = matrixStack.last()
            val normal = last.normal()
            val pose = matrixStack.last().pose()

            val newStack = PoseStack()
            newStack.mulPose(Vector3f.YP.rotation(ray1YRot + (it + 1) * PI / 2))
            val nearTop = Vector4f(startX, startY2, 0F, 1F)
            val nearBottom = Vector4f(startX, startY1, 0F, 1F)
            val farTop = Vector4f(endX, endY2, 0F, 1F)
            val farBottom = Vector4f(endX, endY1, 0F, 1F)

            val poseM = newStack.last().pose()
            nearTop.transform(poseM)
            nearBottom.transform(poseM)
            farTop.transform(poseM)
            farBottom.transform(poseM)

            // "Why are you drawing two quads?" because for some weird reason, a specific vertex order
            // only shows a visible quad for 180 degrees, and which 180 degrees changes with the order.
            // No idea why, it's like the vertices rotate wrong or something, idk.

            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearTop.y(), nearTop.x(), nearTop.z(), 0F, 1F) // A
            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearBottom.y(), nearBottom.x(), nearBottom.z(), 0F, 1F) // B
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farBottom.y(), farBottom.x(), farBottom.z(), 0F, 1F) // C
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farTop.y(), farTop.x(), farTop.z(), 0F, 1F) // D

            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearBottom.y(), nearBottom.x(), nearBottom.z(), 0F, 1F) // B
            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearTop.y(), nearTop.x(), nearTop.z(), 0F, 1F) // A
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farTop.y(), farTop.x(), farTop.z(), 0F, 1F) // D
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farBottom.y(), farBottom.x(), farBottom.z(), 0F, 1F) // C

            matrixStack.popPose()
        }
    }
}