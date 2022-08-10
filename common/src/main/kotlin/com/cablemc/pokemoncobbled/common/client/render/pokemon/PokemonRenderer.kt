package com.cablemc.pokemoncobbled.common.client.render.pokemon

import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate.Companion.BEAM_EXTEND_TIME
import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate.Companion.BEAM_SHRINK_TIME
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemoncobbled.common.client.render.addVertex
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cablemc.pokemoncobbled.common.client.render.renderBeaconBeam
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.isLookingAt
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import kotlin.math.min
import kotlin.math.tan
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathConstants.PI
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f

class PokemonRenderer(
    context: EntityRendererFactory.Context
) : MobEntityRenderer<PokemonEntity, EntityModel<PokemonEntity>>(context, null, 0.5f) {
    companion object {
        var DELTA_TICKS = 0F
        val glowLengthFunction = parabolaFunction(
            peak = 1F,
            period = 1F
        )
    }

    override fun getTexture(entity: PokemonEntity): Identifier {
        return PokemonModelRepository.getTexture(entity.pokemon.species, entity.aspects.get())
    }

    override fun render(
        entity: PokemonEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseMatrix: MatrixStack,
        buffer: VertexConsumerProvider,
        packedLight: Int
    ) {
        DELTA_TICKS = partialTicks // TODO move this somewhere universal // or just fecking remove it
        model = PokemonModelRepository.getPoser(entity.pokemon.species, entity.aspects.get())

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

        if (modelNow is PokemonPoseableModel) {
            modelNow.setLayerContext(buffer, PokemonModelRepository.getLayers(entity.pokemon.species, entity.aspects.get()))
        }

        super.render(entity, entityYaw, partialTicks, poseMatrix, buffer, packedLight)

        if (modelNow is PokemonPoseableModel) {
            modelNow.green = 1F
            modelNow.blue = 1F

            modelNow.resetLayerContext()
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
                    glowLength = glowMultiplier * entity.width * 1.5F,
                    glowRangeAngle = PI / 7
                )
            }
        }

        if (!MinecraftClient.getInstance().options.hudHidden) {
            MinecraftClient.getInstance().player?.let { player ->
                if (player.isLookingAt(entity) && phaseTarget == null) {
                    renderLabel(poseMatrix, partialTicks, entity, player, buffer)
                }
            }
        }
    }

    override fun scale(pEntity: PokemonEntity, pMatrixStack: MatrixStack, pPartialTickTime: Float) {
        val scale = pEntity.pokemon.form.baseScale * pEntity.pokemon.scaleModifier * (pEntity.delegate as PokemonClientDelegate).entityScaleModifier
        pMatrixStack.scale(scale, scale, scale)
    }

    fun renderBeam(matrixStack: MatrixStack, partialTicks: Float, entity: PokemonEntity, beamTarget: Entity, buffer: VertexConsumerProvider) {
        val clientDelegate = entity.delegate as PokemonClientDelegate
        val pokemonPosition = entity.pos.add(0.0, entity.height / 2.0 * clientDelegate.entityScaleModifier.toDouble(), 0.0)
        val beamSourcePosition = if (beamTarget is EmptyPokeBallEntity) {
            beamTarget.pos.let { it.add(pokemonPosition.subtract(it).normalize().multiply(0.4, 0.0, 0.4)) }
        } else {
            beamTarget as PlayerEntity
            if (beamTarget.uuid == MinecraftClient.getInstance().player?.uuid) {
                val lookVec = beamTarget.rotationVector.rotateY(PI / 2).multiply(1.0, 0.0, 1.0).normalize()
                beamTarget.getCameraPosVec(partialTicks).subtract(0.0, 0.4, 0.0).subtract(lookVec.multiply(0.3))
            } else {
                val lookVec = beamTarget.rotationVector.rotateY(PI / 2 - (beamTarget.bodyYaw - beamTarget.pitch).toRadians()).multiply(1.0, 0.0, 1.0).normalize()
                beamTarget.getCameraPosVec(partialTicks).subtract(0.0, 0.7, 0.0).subtract(lookVec.multiply(0.4))
            }
        }


        if (beamSourcePosition.distanceTo(pokemonPosition) > 20) {
            return
        }

        val direction = Vec3f(pokemonPosition.subtract(beamSourcePosition))

        matrixStack.push()
        with(beamSourcePosition.subtract(entity.pos)) { matrixStack.translate(x, y, z) }

        val s = clientDelegate.secondsSinceBeamEffectStarted
        val ratio = if (s < BEAM_EXTEND_TIME) {
            s / BEAM_EXTEND_TIME
        } else if (s > BEAM_EXTEND_TIME + BEAM_SHRINK_TIME) {
            1 - min((s - BEAM_EXTEND_TIME - BEAM_SHRINK_TIME) / BEAM_EXTEND_TIME, 1F)
        } else {
            1F
        }

        direction.normalize()

        val yAxis = Vec3f.POSITIVE_Y.copy()
        val dot = direction.dot(yAxis)
        val cross = yAxis.copy()
        cross.cross(direction)
        val q = Quaternion(cross.x, cross.y, cross.z, 1 + dot)
        q.normalize()
        matrixStack.multiply(q)

        renderBeaconBeam(
            matrixStack = matrixStack,
            buffer = buffer,
            partialTicks = partialTicks,
            totalLevelTime = entity.world.time,
            height = pokemonPosition.distanceTo(beamSourcePosition).toFloat() * ratio,
            red = 1F,
            green = 0.1F,
            blue = 0.1F,
            alpha = 1F,
            beamRadius = 0.03F,
            glowRadius = 0.07F,
            glowAlpha = 0.4F
        )

        matrixStack.pop()
    }

    fun renderLabel(poseStack: MatrixStack, partialTicks: Float, entity: PokemonEntity, player: PlayerEntity, multiBufferSource: VertexConsumerProvider) {
        if (entity.isInvisible) {
            return
        }

        val mc = MinecraftClient.getInstance()

        val stepMultiplier = 0.5F
        val toPlayer = player.getCameraPosVec(partialTicks)
            .subtract(entity.getLerpedPos(partialTicks).add(0.0, entity.boundingBox.yLength + 0.5, 0.0))
            .multiply(stepMultiplier.toDouble())

        poseStack.push()
        poseStack.translate(0.0, entity.boundingBox.yLength + 0.5, 0.0)
        poseStack.translate(toPlayer.x, toPlayer.y, toPlayer.z)
        poseStack.multiply(dispatcher.rotation)
        poseStack.scale(-0.025f * stepMultiplier, -0.025f * stepMultiplier, 1f)
        val matrix4f = poseStack.peek().positionMatrix
        val g = mc.options.getTextBackgroundOpacity(0.25f)
        val k = (g * 255.0f).toInt() shl 24
        val label = entity.pokemon.species.translatedName
        var h = (-textRenderer.getWidth(label) / 2).toFloat()
        val y = 0F
        val seeThrough = true
        val packedLight = LightmapTextureManager.pack(15, 15)
        textRenderer.draw(label, h, y, 0x20FFFFFF, false, matrix4f, multiBufferSource, seeThrough, k, packedLight)
        textRenderer.draw(label, h, y, -1, false, matrix4f, multiBufferSource, false, 0, packedLight)

        if (entity.canBattle(player)) {
            val sendOutBinding = PartySendBinding.currentKey().localizedText
            val battlePrompt = lang("challenge_label", sendOutBinding)
            h = (-textRenderer.getWidth(battlePrompt) / 2).toFloat()
            textRenderer.draw(battlePrompt, h, y + 10, 0x20FFFFFF, false, matrix4f, multiBufferSource, seeThrough, k, packedLight)
            textRenderer.draw(battlePrompt, h, y + 10, -1, false, matrix4f, multiBufferSource, false, 0, packedLight)
        }
        poseStack.pop()

    }

    fun renderGlow(
        matrixStack: MatrixStack,
        entity: PokemonEntity,
        buffer: VertexConsumerProvider,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        glowLength: Float,
        glowRangeAngle: Float
    ) {
        val clientDelegate = entity.delegate as PokemonClientDelegate
        val totalWorldTicks = entity.world.time
        val vectorBuffer = buffer.getBuffer(RenderLayer.getLightning()) //buffer.getBuffer(RenderType.glint())

        val ray1YRot = (totalWorldTicks + DELTA_TICKS) / 16F

        val startY1 = entity.boundingBox.yLength.toFloat() * 0.5F * clientDelegate.entityScaleModifier
        val startY2 = startY1 + entity.boundingBox.yLength.toFloat() * 0.05F * clientDelegate.entityScaleModifier

        val endY1 = startY1 - tan(glowRangeAngle) * glowLength
        val endY2 = startY2 + tan(glowRangeAngle) * glowLength

        val startX = 0F
        val endX = startX + glowLength

        // Draw 4 rays of red.
        repeat(times = 4) {
            matrixStack.push()

            val last = matrixStack.peek()
            val normal = last.normalMatrix
            val pose = matrixStack.peek().positionMatrix

            val newStack = MatrixStack()
            newStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(ray1YRot + (it + 1) * PI / 2))
            val nearTop = Vector4f(startX, startY2, 0F, 1F)
            val nearBottom = Vector4f(startX, startY1, 0F, 1F)
            val farTop = Vector4f(endX, endY2, 0F, 1F)
            val farBottom = Vector4f(endX, endY1, 0F, 1F)

            val poseM = newStack.peek().positionMatrix
            nearTop.transform(poseM)
            nearBottom.transform(poseM)
            farTop.transform(poseM)
            farBottom.transform(poseM)

            // "Why are you drawing two quads?" because for some weird reason, a specific vertex order
            // only shows a visible quad for 180 degrees, and which 180 degrees changes with the order.
            // No idea why, it's like the vertices rotate wrong or something, idk.

            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearTop.y, nearTop.x, nearTop.z, 0F, 1F) // A
            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearBottom.y, nearBottom.x, nearBottom.z, 0F, 1F) // B
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farBottom.y, farBottom.x, farBottom.z, 0F, 1F) // C
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farTop.y, farTop.x, farTop.z, 0F, 1F) // D

            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearBottom.y, nearBottom.x, nearBottom.z, 0F, 1F) // B
            addVertex(pose, normal, vectorBuffer, red, green, blue, alpha, nearTop.y, nearTop.x, nearTop.z, 0F, 1F) // A
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farTop.y, farTop.x, farTop.z, 0F, 1F) // D
            addVertex(pose, normal, vectorBuffer, red, green, blue, 0F, farBottom.y, farBottom.x, farBottom.z, 0F, 1F) // C

            matrixStack.pop()
        }
    }

    override fun getLyingAngle(entity: PokemonEntity?) = 0F
}