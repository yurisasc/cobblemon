/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.struct.ArrayStruct
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.MoValue
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.api.molang.ObjectValue
import com.cobblemon.mod.common.api.scheduling.afterOnClient
import com.cobblemon.mod.common.client.ClientMoLangFunctions.setupClient
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.client.render.MatrixWrapper
import com.cobblemon.mod.common.client.render.ModelLayer
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.animation.*
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.getDoubleOrNull
import com.cobblemon.mod.common.util.getStringOrNull
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier

/**
 * A model that can be posed and animated using [StatelessAnimation]s and [StatefulAnimation]s. This
 * requires poses to be registered and should implement any [ModelFrame] interfaces that apply to this
 * model. Implementing the render functions is possible but not necessary.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PosableEntityModel<T : Entity>(
    renderTypeFunc: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout
) : EntityModel<T>(renderTypeFunc) {
    val context: RenderContext = RenderContext()

    lateinit var posableModel: PosableModel

    @Transient
    val functions = QueryStruct(hashMapOf())
        .addFunction("bedrock_primary") { params ->
            val group = params.getString(0)
            val animation = params.getString(1)
            val anim = bedrockStateful(group, animation)
            val excludedLabels = mutableSetOf<String>()
            var curve: WaveFunction = { t ->
                if (t < 0.1) {
                    t * 10
                } else if (t < 0.9) {
                    1F
                } else {
                    1F
                }
            }
            for (index in 2 until params.params.size) {
                val param = params.get<MoValue>(index)
                if (param is ObjectValue<*>) {
                    curve = param.obj as WaveFunction
                    continue
                }

                val label = params.getString(index) ?: continue
                excludedLabels.add(label)
            }

            return@addFunction ObjectValue(PrimaryAnimation(animation = anim, excludedLabels = excludedLabels, curve = curve))
        }
        .addFunction("bedrock_stateful") { params ->
            val group = params.getString(0)
            val animation = params.getString(1)
            val anim = bedrockStateful(group, animation)
            return@addFunction ObjectValue(anim)
        }
        .addFunction("bedrock") { params ->
            val group = params.getString(0)
            val animation = params.getString(1)
            val anim = bedrock(group, animation)
            return@addFunction ObjectValue(anim)
        }
        .addFunction("look") { params ->
            val boneName = params.getString(0)
            val pitchMultiplier = params.getDoubleOrNull(1) ?: 1F
            val yawMultiplier = params.getDoubleOrNull(2) ?: 1F
            val maxPitch = params.getDoubleOrNull(3) ?: 70F
            val minPitch = params.getDoubleOrNull(4) ?: -45F
            val maxYaw = params.getDoubleOrNull(5) ?: 45F
            ObjectValue(
                SingleBoneLookAnimation<T>(
                    frame = this,
                    bone = getPart(boneName),
                    pitchMultiplier = pitchMultiplier.toFloat(),
                    yawMultiplier = yawMultiplier.toFloat(),
                    maxPitch = maxPitch.toFloat(),
                    minPitch = minPitch.toFloat(),
                    maxYaw = maxYaw.toFloat()
                )
            )
        }
        .addFunction("quadruped_walk") { params ->
            val periodMultiplier = params.getDoubleOrNull(0) ?: 0.6662F
            val amplitudeMultiplier = params.getDoubleOrNull(1) ?: 1.4F
            val leftFrontLeftName = params.getStringOrNull(2) ?: "leg_front_left"
            val leftFrontRightName = params.getStringOrNull(3) ?: "leg_front_right"
            val leftBackLeftName = params.getStringOrNull(4) ?: "leg_back_left"
            val leftBackRightName = params.getStringOrNull(5) ?: "leg_back_right"

            ObjectValue(
                QuadrupedWalkAnimation<T>(
                    frame = this,
                    periodMultiplier = periodMultiplier.toFloat(),
                    amplitudeMultiplier = amplitudeMultiplier.toFloat(),
                    legFrontLeft = this.getPart(leftFrontLeftName),
                    legFrontRight = this.getPart(leftFrontRightName),
                    legBackLeft = this.getPart(leftBackLeftName),
                    legBackRight = this.getPart(leftBackRightName)
                )
            )
        }
        .addFunction("biped_walk") { params ->
            val periodMultiplier = params.getDoubleOrNull(0) ?: 0.6662F
            val amplitudeMultiplier = params.getDoubleOrNull(1) ?: 1.4F
            val leftLegName = params.getStringOrNull(2) ?: "leg_left"
            val rightLegName = params.getStringOrNull(3) ?: "leg_right"

            ObjectValue(
                BipedWalkAnimation<T>(
                    frame = this,
                    periodMultiplier = periodMultiplier.toFloat(),
                    amplitudeMultiplier = amplitudeMultiplier.toFloat(),
                    leftLeg = this.getPart(leftLegName),
                    rightLeg = this.getPart(rightLegName)
                )
            )
        }
        .addFunction("bimanual_swing") { params ->
            val swingPeriodMultiplier = params.getDoubleOrNull(0) ?: 0.6662F
            val amplitudeMultiplier = params.getDoubleOrNull(1) ?: 1F
            val leftArmName = params.getStringOrNull(2) ?: "arm_left"
            val rightArmName = params.getStringOrNull(3) ?: "arm_right"

            ObjectValue(
                BimanualSwingAnimation<T>(
                    frame = this,
                    swingPeriodMultiplier = swingPeriodMultiplier.toFloat(),
                    amplitudeMultiplier = amplitudeMultiplier.toFloat(),
                    leftArm = this.getPart(leftArmName),
                    rightArm = this.getPart(rightArmName)
                )
            )
        }
        .addFunction("sine_wing_flap") { params ->
            // verticalShift = -14F.toRadians(), period = 0.9F, amplitude = 0.9F
            val amplitude = params.getDoubleOrNull(0) ?: 0.9F
            val period = params.getDoubleOrNull(1) ?: 0.9F
            val verticalShift = params.getDoubleOrNull(2) ?: 0F
            val axis = params.getStringOrNull(3) ?: "y"
            val axisIndex = when (axis) {
                "x" -> ModelPartTransformation.X_AXIS
                "y" -> ModelPartTransformation.Y_AXIS
                "z" -> ModelPartTransformation.Z_AXIS
                else -> ModelPartTransformation.Y_AXIS
            }
            val wingLeft = params.getStringOrNull(4) ?: "wing_left"
            val wingRight = params.getStringOrNull(5) ?: "wing_right"

            ObjectValue(
                WingFlapIdleAnimation<T>(
                    frame = this,
                    rotation = sineFunction(verticalShift = verticalShift.toFloat(), period = period.toFloat(), amplitude = amplitude.toFloat()),
                    axis = axisIndex,
                    leftWing = this.getPart(wingLeft),
                    rightWing = this.getPart(wingRight)
                )
            )
        }
        .addFunction("bedrock_quirk") { params ->
            val animationGroup = params.getString(0)
            val animationNames = params.get<MoValue>(1)?.let { if (it is ArrayStruct) it.map.values.map { it.asString() } else listOf(it.asString()) } ?: listOf()
            val minSeconds = params.getDoubleOrNull(2) ?: 8F
            val maxSeconds = params.getDoubleOrNull(3) ?: 30F
            val loopTimes = params.getDoubleOrNull(4)?.toInt() ?: 1
            ObjectValue(
                quirk(
                    secondsBetweenOccurrences = minSeconds.toFloat() to maxSeconds.toFloat(),
                    condition = { true },
                    loopTimes = 1..loopTimes,
                    animation = { bedrockStateful(animationGroup, animationNames.random()) }
                )
            )
        }

    @Transient
    val runtime = MoLangRuntime().setup().setupClient().also { it.environment.getQueryStruct().addFunctions(functions.functions) }


    override fun render(
        stack: MatrixStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        val entity = context.request(RenderContext.ENTITY)
        val overlay = getOverlayTexture(entity) ?: packedOverlay
        posableModel.render(context, stack, buffer, packedLight, overlay, r, g, b, a)
    }

    open fun getOverlayTexture(entity: Entity?): Int? {
        return if (entity is LivingEntity) {
            OverlayTexture.packUv(
                OverlayTexture.getU(0F),
                OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0)
            )
        } else if (entity != null) {
            OverlayTexture.DEFAULT_UV
        } else {
            null
        }
    }

    override fun setAngles(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        setupEntityTypeContext(entity)
        if (entity is Poseable) {
            val state = entity.delegate as PosableState
            posableModel.setupAnimStateful(entity, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
        }
    }

    open fun setupEntityTypeContext(entity: Entity?) {
        entity?.let {
            context.put(RenderContext.ENTITY, entity)
        }
    }
}