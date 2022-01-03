package com.cablemc.pokemoncobbled.client.render.pokemon

import com.cablemc.pokemoncobbled.client.CobbledResources
import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate.Companion.BEAM_EXTEND_TIME
import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate.Companion.BEAM_SHRINK_TIME
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix3f
import com.mojang.math.Matrix4f
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import com.mojang.math.Vector4f
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
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

    // TODO register models in a more clearly defined place
    init {
        PokemonModelRepository.initializeModels(context)
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
        val beamSourcePosition = if (beamTarget is PokeBallEntity) {
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
        val vectorBuffer =  buffer.getBuffer(RenderType.lightning()) //buffer.getBuffer(RenderType.glint())

        val ray1YRot = (totalWorldTicks + DELTA_TICKS) / 16F

        val startY1 = entity.boundingBox.ysize.toFloat() * 0.5F
        val startY2 = startY1 + entity.boundingBox.ysize.toFloat() * 0.05F

        val endY1 = startY1 - tan(glowRangeAngle) * glowLength
        val endY2 = startY2 + tan(glowRangeAngle) * glowLength

        val startX = 0F
        val endX = startX + glowLength

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





        /*
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMax, x1, z1, 1F, 0F)
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMin, x1, z1, 1F, 1F)
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMin, x2, z2, 0F, 1F)
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMax, x2, z2, 0F, 0F)
         */

//            buffer = vectorBuffer,
//            red = red,
//            green = green,
//            blue = blue,
//            alpha = alpha,
//            yMin = 0F,
//            yMax = 1F,
//            x1 = 0F,
//            x2 = 1F,
//            z1 = 0F,
//            z2 = 1F
    }


    fun renderBeaconBeam(
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        textureLocation: ResourceLocation = CobbledResources.PHASE_BEAM,
        partialTicks: Float,
        totalLevelTime: Long,
        yOffset: Float = 0F,
        height: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        beamRadius: Float,
        glowRadius: Float,
        glowAlpha: Float
    ) {
        val i = yOffset + height
        val beamRotation = Math.floorMod(totalLevelTime, 40).toFloat() + partialTicks
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(beamRotation * 2.25f - 45.0f))
        var f9 = -beamRadius
        val f12 = -beamRadius
        renderPart(
            matrixStack,
            buffer.getBuffer(RenderType.beaconBeam(textureLocation, false)),
            red,
            green,
            blue,
            alpha,
            yOffset,
            i,
            0.0f,
            beamRadius,
            beamRadius,
            0.0f,
            f9,
            0.0f,
            0.0f,
            f12
        )
        // Undo the rotation so that the glow is at a rotated offset
        matrixStack.popPose()
        val f6 = -glowRadius
        val f7 = -glowRadius
        val f8 = -glowRadius
        f9 = -glowRadius
        renderPart(
            matrixStack,
            buffer.getBuffer(RenderType.beaconBeam(textureLocation, true)),
            red,
            green,
            blue,
            glowAlpha,
            yOffset,
            i,
            f6,
            f7,
            glowRadius,
            f8,
            f9,
            glowRadius,
            glowRadius,
            glowRadius
        )
    }

    private fun renderPart(
        matrixStack: PoseStack,
        vertexBuffer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        yMin: Float,
        yMax: Float,
        p_112164_: Float,
        p_112165_: Float,
        p_112166_: Float,
        p_112167_: Float,
        p_112168_: Float,
        p_112169_: Float,
        p_112170_: Float,
        p_112171_: Float
    ) {
        val pose = matrixStack.last()
        val matrix4f = pose.pose()
        val matrix3f = pose.normal()
        renderQuad(
            matrix4f,
            matrix3f,
            vertexBuffer,
            red,
            green,
            blue,
            alpha,
            yMin,
            yMax,
            p_112164_,
            p_112165_,
            p_112166_,
            p_112167_
        )
        renderQuad(
            matrix4f,
            matrix3f,
            vertexBuffer,
            red,
            green,
            blue,
            alpha,
            yMin,
            yMax,
            p_112170_,
            p_112171_,
            p_112168_,
            p_112169_
        )
        renderQuad(
            matrix4f,
            matrix3f,
            vertexBuffer,
            red,
            green,
            blue,
            alpha,
            yMin,
            yMax,
            p_112166_,
            p_112167_,
            p_112170_,
            p_112171_
        )
        renderQuad(
            matrix4f,
            matrix3f,
            vertexBuffer,
            red,
            green,
            blue,
            alpha,
            yMin,
            yMax,
            p_112168_,
            p_112169_,
            p_112164_,
            p_112165_
        )
    }

    private fun renderQuad(
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        buffer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        yMin: Float,
        yMax: Float,
        x1: Float,
        z1: Float,
        x2: Float,
        z2: Float
    ) {
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMax, x1, z1, 1F, 0F)
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMin, x1, z1, 1F, 1F)
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMin, x2, z2, 0F, 1F)
        addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMax, x2, z2, 0F, 0F)
    }

    private fun addVertex(
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        buffer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        y: Float,
        x: Float,
        z: Float,
        texU: Float,
        texV: Float
    ) {
        buffer
            .vertex(matrixPos, x, y, z)
            .color(red, green, blue, alpha)
            .uv(texU, texV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(15728880)
            .normal(matrixNormal, 0.0f, 1.0f, 0.0f)
            .endVertex()
    }
}