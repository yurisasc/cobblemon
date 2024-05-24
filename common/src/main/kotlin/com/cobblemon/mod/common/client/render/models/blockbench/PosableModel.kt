/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.struct.ArrayStruct
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.MoValue
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.api.molang.ObjectValue
import com.cobblemon.mod.common.api.scheduling.afterOnClient
import com.cobblemon.mod.common.client.ClientMoLangFunctions.setupClient
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.client.render.ModelLayer
import com.cobblemon.mod.common.client.render.models.blockbench.animation.*
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.SimpleQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.getDoubleOrNull
import com.cobblemon.mod.common.util.getStringOrNull
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

abstract class PosableModel: ModelFrame {
    val context: RenderContext = RenderContext()

    /** Whether the renderer that will process this is going to do the weird -1.5 Y offset bullshit that the living entity renderer does. */
    open var isForLivingEntityRenderer = true

    val poses = mutableMapOf<String, Pose>()
    lateinit var locatorAccess: LocatorAccess

    open var portraitScale = 1F
    open var portraitTranslation = Vec3d(0.0, 0.0, 0.0)

    open var profileScale = 1F
    open var profileTranslation = Vec3d(0.0, 0.0, 0.0)

    var red = 1F
    var green = 1F
    var blue = 1F
    var alpha = 1F

    @Transient
    var currentLayers: Iterable<ModelLayer> = listOf()

    @Transient
    var bufferProvider: VertexConsumerProvider? = null

    @Transient
    var currentState: PosableState? = null

    /**
     * A list of [ModelPartTransformation] that record the original
     * This allows the original rotations to be reset.
     */
    val defaultPositions = mutableListOf<ModelPartTransformation>()

    val relevantParts = mutableListOf<ModelPart>()
    val relevantPartsByName = mutableMapOf<String, ModelPart>()

    @Transient
    val functions = QueryStruct(hashMapOf())
        .addFunction("exclude_labels") { params ->
            val labels = params.params.map { it.asString() }
            return@addFunction ObjectValue(ExcludedLabels(labels))
        }
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
                    val obj = param.obj
                    if (obj is ExcludedLabels) {
                        excludedLabels.addAll(obj.labels)
                    } else {
                        curve = param.obj as WaveFunction
                    }
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
                SingleBoneLookAnimation(
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
                QuadrupedWalkAnimation(
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
                BipedWalkAnimation(
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
                BimanualSwingAnimation(
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
                WingFlapIdleAnimation(
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

    val animations = mutableMapOf<String, ExpressionLike>()

    /** Registers the different poses this model is capable of ahead of time. Should use [registerPose] religiously. */
    abstract fun registerPoses()

    fun getAnimation(state: PosableState, name: String, runtime: MoLangRuntime): StatefulAnimation? {
        val entity = state.getEntity()
        val poseAnimations = state.currentPose?.let(this::getPose)?.animations ?: mapOf()
        val animation = resolveFromAnimationMap(poseAnimations, name, runtime)
            ?: resolveFromAnimationMap(animations, name, runtime)
            ?: if (name == "cry") {
                cryAnimation.invoke(state)
            } else if (name == "faint") {
                getFaintAnimation(state)
            } else {
                try {
                    name.asExpressionLike().resolveObject(runtime).obj as StatefulAnimation
                } catch (exception: Exception) {
                    extractAnimation(name)
                }
            }
        return animation
    }

    open fun getFaintAnimation(state: PosableState): StatefulAnimation? = null
    open fun getEatAnimation(state: PosableState): StatefulAnimation? = null

    open val cryAnimation: CryProvider = CryProvider { null }

    /**
     * Animation group : animation name [: primary]
     * e.g. "particle_dummy:animation.particle_dummy.dragon_claw_target:primary"
     * e.g. "particle_dummy:animation.particle.dummy.stat_up
     */
    fun extractAnimation(string: String): StatefulAnimation? {
        val group = string.substringBefore(":")
        val animationName = string.substringAfter(":").substringBefore(":")
        val isPrimary = string.endsWith(":primary")
        if (animationName.isNotBlank() && animationName != string) {
            val animation = BedrockAnimationRepository.tryGetAnimation(group, animationName) ?: return null
            return if (isPrimary) {
                PrimaryAnimation(BedrockStatefulAnimation(animation))
            } else {
                BedrockStatefulAnimation(animation)
            }
        } else {
            return null
        }
    }

    private fun resolveFromAnimationMap(map: Map<String, ExpressionLike>, name: String, runtime: MoLangRuntime): StatefulAnimation? {
        val animationExpression = map[name] ?: return null
        return try {
            animationExpression.resolveObject(runtime).obj as StatefulAnimation
        } catch (e: Exception) {
            Cobblemon.LOGGER.error("Failed to create animation by name $name, most likely something wrong in the MoLang")
            e.printStackTrace()
            null
        }
    }

    fun withLayerContext(
        buffer: VertexConsumerProvider,
        state: PosableState,
        layers: Iterable<ModelLayer>,
        action: () -> Unit
    ) {
        setLayerContext(buffer, state, layers)
        action()
        resetLayerContext()
    }

    fun setLayerContext(buffer: VertexConsumerProvider, state: PosableState, layers: Iterable<ModelLayer>) {
        currentLayers = layers
        bufferProvider = buffer
        currentState = state
    }

    fun resetLayerContext() {
        currentLayers = emptyList()
        bufferProvider = null
        currentState = null
    }

    /**
     * Registers a pose for this model.
     *
     * @param poseType The type of pose it is, as a [PoseType]
     * @param condition The condition for this pose to apply
     * @param idleAnimations The stateless animations to use as idles unless a [StatefulAnimation] prevents it.
     * @param transformedParts All the transformed forms of parts of the body that define this pose.
     */
    fun registerPose(
        poseType: PoseType,
        condition: ((RenderContext) -> Boolean)? = null,
        transformTicks: Int = 10,
        animations: MutableMap<String, ExpressionLike> = mutableMapOf(),
        onTransitionedInto: (PosableState) -> Unit = {},
        idleAnimations: Array<StatelessAnimation> = emptyArray(),
        transformedParts: Array<ModelPartTransformation> = emptyArray(),
        quirks: Array<ModelQuirk<*>> = emptyArray()
    ): Pose {
        return Pose(
            poseType.name,
            setOf(poseType),
            condition,
            onTransitionedInto,
            transformTicks,
            animations,
            idleAnimations,
            transformedParts,
            quirks
        ).also {
            poses[poseType.name] = it
        }
    }

    fun registerPose(
        poseName: String,
        poseTypes: Set<PoseType>,
        condition: ((RenderContext) -> Boolean)? = null,
        transformTicks: Int = 10,
        animations: MutableMap<String, ExpressionLike> = mutableMapOf(),
        onTransitionedInto: (PosableState) -> Unit = {},
        idleAnimations: Array<StatelessAnimation> = emptyArray(),
        transformedParts: Array<ModelPartTransformation> = emptyArray(),
        quirks: Array<ModelQuirk<*>> = emptyArray()
    ): Pose {
        return Pose(
            poseName,
            poseTypes,
            condition,
            onTransitionedInto,
            transformTicks,
            animations,
            idleAnimations,
            transformedParts,
            quirks
        ).also {
            poses[poseName] = it
        }
    }

    fun registerPose(
        poseName: String,
        poseType: PoseType,
        condition: ((RenderContext) -> Boolean)? = null,
        transformTicks: Int = 10,
        animations: MutableMap<String, ExpressionLike> = mutableMapOf(),
        onTransitionedInto: (PosableState) -> Unit = {},
        idleAnimations: Array<StatelessAnimation> = emptyArray(),
        transformedParts: Array<ModelPartTransformation> = emptyArray(),
        quirks: Array<ModelQuirk<*>> = emptyArray()
    ): Pose {
        return Pose(
            poseName,
            setOf(poseType),
            condition,
            onTransitionedInto,
            transformTicks,
            animations,
            idleAnimations,
            transformedParts,
            quirks
        ).also {
            poses[poseName] = it
        }
    }

    /** Registers the same configuration for both left and right shoulder poses. */
    fun registerShoulderPoses(
        transformTicks: Int = 30,
        idleAnimations: Array<StatelessAnimation>,
        transformedParts: Array<ModelPartTransformation> = emptyArray()
    ) {
        registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            transformTicks = transformTicks,
            idleAnimations = idleAnimations,
            transformedParts = transformedParts
        )

        registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            transformTicks = transformTicks,
            idleAnimations = idleAnimations,
            transformedParts = transformedParts
        )
    }

    fun ModelPart.registerChildWithAllChildren(name: String): ModelPart {
        val child = this.getChild(name)!!
        registerRelevantPart(name to child)
        loadAllNamedChildren(child)
        return child
    }

    fun ModelPart.registerChildWithSpecificChildren(name: String, nameList: Iterable<String>): ModelPart {
        val child = getChild(name)
        registerRelevantPart(name to child)
        loadSpecificNamedChildren(child, nameList)
        return child
    }

    fun initializeLocatorAccess() {
        locatorAccess = LocatorAccess.resolve(rootPart) ?: LocatorAccess(rootPart)
    }

    fun getPart(name: String) = relevantPartsByName[name]!!

    private fun loadSpecificNamedChildren(modelPart: ModelPart, nameList: Iterable<String>) {
        for ((name, child) in modelPart.children.entries) {
            if (name in nameList) {
                val default = ModelPartTransformation.derive(child)
                relevantParts.add(child)
                relevantPartsByName[name] = child
                defaultPositions.add(default)
                loadAllNamedChildren(child)
            }
        }
    }

    fun loadAllNamedChildren(bone: Bone) {
        if (bone is ModelPart) loadAllNamedChildren(bone)
    }

    fun loadAllNamedChildren(modelPart: ModelPart) {
        for ((name, child) in modelPart.children.entries) {
            val default = ModelPartTransformation.derive(child)
            relevantParts.add(child)
            relevantPartsByName[name] = child
            defaultPositions.add(default)
            loadAllNamedChildren(child)
        }
    }

    fun registerRelevantPart(name: String, part: ModelPart): ModelPart {
        val default = ModelPartTransformation.derive(part)
        relevantParts.add(part)
        relevantPartsByName[name] = part
        defaultPositions.add(default)
        return part
    }

    fun registerRelevantPart(pairing: Pair<String, ModelPart>) = registerRelevantPart(pairing.first, pairing.second)

    fun render(
        context: RenderContext,
        stack: MatrixStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        rootPart.render(
            context,
            stack,
            buffer,
            packedLight,
            packedOverlay,
            red * r,
            green * g,
            blue * b,
            alpha * a
        )

        val provider = bufferProvider
        if (provider != null) {
            for (layer in currentLayers) {
                val texture = layer.texture?.invoke(currentState?.animationSeconds ?: 0F) ?: continue
                val renderLayer = getLayer(texture, layer.emissive, layer.translucent)
                val consumer = provider.getBuffer(renderLayer)
                stack.push()
                rootPart.render(
                    context,
                    stack,
                    consumer,
                    packedLight,
                    packedOverlay,
                    layer.tint.x,
                    layer.tint.y,
                    layer.tint.z,
                    layer.tint.w
                )
                stack.pop()
            }
        }
    }

    fun makeLayer(texture: Identifier, emissive: Boolean, translucent: Boolean): RenderLayer {
        val multiPhaseParameters: RenderLayer.MultiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .program(
                when {
                    emissive && translucent -> RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM
                    !emissive && translucent -> RenderPhase.ENTITY_TRANSLUCENT_PROGRAM
                    !emissive && !translucent -> RenderPhase.ENTITY_CUTOUT_PROGRAM
                    else -> RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM // This one should be changed to maybe a custom shader? Translucent stuffs with things
                }
            )
            .texture(RenderPhase.Texture(texture, false, false))
            .transparency(if (translucent) RenderPhase.TRANSLUCENT_TRANSPARENCY else RenderPhase.NO_TRANSPARENCY)
            .cull(RenderPhase.ENABLE_CULLING)
            .writeMaskState(RenderPhase.ALL_MASK)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .build(false)

        return RenderLayer.of(
            "cobblemon_entity_layer",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            translucent,
            multiPhaseParameters
        )
    }

    fun getLayer(texture: Identifier, emissive: Boolean, translucent: Boolean): RenderLayer {
        return if (!emissive && !translucent) {
            RenderLayer.getEntityCutout(texture)
        } else if (!emissive) {
            RenderLayer.getEntityTranslucent(texture)
        } else {
            makeLayer(texture, emissive = emissive, translucent = translucent)
        }
    }


    /** Applies the given pose type to the model, if there is a matching pose. */
    fun applyPose(pose: String, intensity: Float) = getPose(pose)?.transformedParts?.forEach { it.apply(intensity) }
    fun getPose(pose: PoseType) = poses.values.firstOrNull { pose in it.poseTypes }
    fun getPose(name: String) = poses[name]

    /** Puts the model back to its original location and rotations. */
    fun setDefault() = defaultPositions.forEach { it.set() }

    val quirks = mutableListOf<ModelQuirk<*>>()

    fun setupAnimStateful(
        entity: Entity?,
        state: PosableState,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        context.put(RenderContext.ENTITY, entity)
        setupEntityTypeContext(entity)
        state.currentModel = this
        setDefault()
        state.preRender()
        updateLocators(state)
        var poseName = state.getPose()
        var pose = poseName?.let { getPose(it) }
        val entityPoseType = if (entity is Poseable) entity.getCurrentPoseType() else null

        if (entity != null && (poseName == null || pose == null || !pose.isSuitable(context) || entityPoseType !in pose.poseTypes)) {
            val desirablePose = poses.values.firstOrNull {
                (entityPoseType == null || entityPoseType in it.poseTypes) && it.isSuitable(context)
            }
                ?: Pose("none", setOf(PoseType.NONE), null, {}, 0, mutableMapOf(), emptyArray(), emptyArray(), emptyArray())

            // If this condition matches then it just no longer fits this pose
            if (pose != null && poseName != null) {
                if (state.primaryAnimation == null) {
                    moveToPose(context, state, desirablePose)
                }
            } else {
                pose = desirablePose
                poseName = desirablePose.poseName
                state.setPose(poseName)
            }
        } else {
            poseName = poseName ?: poses.values.first().poseName
        }

        val currentPose = getPose(poseName)
        applyPose(poseName, 1F)

        val primaryAnimation = state.primaryAnimation

        if (currentPose != null && primaryAnimation == null) {
            // Remove any quirk animations that don't exist in our current pose
            state.quirks.keys.filterNot(currentPose.quirks::contains).forEach(state.quirks::remove)
            // Tick all the quirks
            currentPose.quirks.forEach {
                it.tick(context, this, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, 1F)
            }
        }

        if (primaryAnimation != null) {
            state.primaryOverridePortion = 1 - primaryAnimation.curve((state.animationSeconds - primaryAnimation.started) / primaryAnimation.duration)
            if (!primaryAnimation.run(context, this, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, 1 - state.primaryOverridePortion)) {
                state.primaryAnimation = null
                state.primaryOverridePortion = 1F
            }
        }

        val removedStatefuls = state.statefulAnimations.toList()
            .filterNot { it.run(context, this, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, 1F) }
        state.statefulAnimations.removeAll(removedStatefuls)
        state.currentPose?.let { getPose(it) }
            ?.idleStateful(context, this, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
        updateLocators(state)
    }

    //This is used to set additional entity type specific context
    open fun setupEntityTypeContext(entity: Entity?) {}

    fun moveToPose(context: RenderContext, state: PosableState, desirablePose: Pose) {
        val previousPose = state.getPose()?.let { getPose(it) } ?: run {
            return state.setPose(desirablePose.poseName)
        }

        val desirablePoseType = desirablePose.poseTypes.first()

        if (state.statefulAnimations.none { it.isTransform }) {
            val transition = previousPose.transitions[desirablePose.poseName]
            if (transition == null && previousPose.transformTicks > 0) {
                val primaryAnimation = PrimaryAnimation(
                    PoseTransitionAnimation(
                        beforePose = previousPose,
                        afterPose = desirablePose,
                        durationTicks = previousPose.transformTicks
                    ),
                    curve = { 1F }
                )
                state.addPrimaryAnimation(primaryAnimation)
                afterOnClient(seconds = primaryAnimation.duration) {
                    state.setPose(desirablePose.poseName)
                    if (state.primaryAnimation == primaryAnimation) {
                        state.primaryAnimation = null
                    }
                }
            } else if (transition != null) {
                val animation = transition(previousPose, desirablePose)
                val primaryAnimation = PrimaryAnimation(animation, curve = { 1F })
                state.addPrimaryAnimation(primaryAnimation)
                afterOnClient(seconds = primaryAnimation.duration) {
                    state.setPose(desirablePose.poseName)
                }
            } else {
                state.setPose(poses.values.first { desirablePoseType in it.poseTypes && (it.condition == null || it.condition.invoke(context)) }.poseName)
            }
        }
    }

    /**
     * Figures out where all of this model's locators are in real space, so that they can be
     * found and used from other client-side systems.
     */
    fun updateLocators(state: PosableState) {
        val entity = context.request(RenderContext.ENTITY) ?: return
        val matrixStack = MatrixStack()
        // We could improve this to be generalized for other entities
        if (entity is PokemonEntity) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - entity.bodyYaw))
            matrixStack.push()
            matrixStack.scale(-1F, -1F, 1F)
            val scale = entity.pokemon.form.baseScale * entity.pokemon.scaleModifier * (entity.delegate as PokemonClientDelegate).entityScaleModifier
            matrixStack.scale(scale, scale, scale)
        } else if (entity is EmptyPokeBallEntity) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.yaw))
            matrixStack.push()
            matrixStack.scale(1F, -1F, -1F)
            matrixStack.scale(0.7F, 0.7F, 0.7F)
        } else if (entity is GenericBedrockEntity) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.yaw))
            matrixStack.push()
            // Not 100% convinced we need the -1 on Y but if we needed it for the Poke Ball then probably?
            matrixStack.scale(1F, -1F, 1F)
        } else if (entity is NPCEntity) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - entity.bodyYaw))
            matrixStack.push()
            matrixStack.scale(-1F, -1F, 1F)
        }

        if (isForLivingEntityRenderer) {
            // Standard living entity offset, only God knows why Mojang did this.
            matrixStack.translate(0.0, -1.5, 0.0)
        }

        locatorAccess.update(matrixStack, state.locatorStates)
    }

    fun ModelPart.translation(
        function: WaveFunction,
        axis: Int,
        timeVariable: (state: PosableState, limbSwing: Float, ageInTicks: Float) -> Float?
    ) = TranslationFunctionStatelessAnimation(
        part = this,
        function = function,
        axis = axis,
        timeVariable = timeVariable
    )

    fun ModelPart.rotation(
        function: WaveFunction,
        axis: Int,
        timeVariable: (state: PosableState, limbSwing: Float, ageInTicks: Float) -> Float?
    ) = RotationFunctionStatelessAnimation(
        part = this,
        function = function,
        axis = axis,
        timeVariable = timeVariable
    )

    fun bedrock(
        animationGroup: String,
        animation: String,
        animationPrefix: String = "animation.$animationGroup"
    ) = BedrockStatelessAnimation(
        BedrockAnimationRepository.getAnimation(animationGroup, "$animationPrefix.$animation")
    )

    fun bedrockStateful(
        animationGroup: String,
        animation: String,
        animationPrefix: String = "animation.$animationGroup"
    ) = BedrockStatefulAnimation(BedrockAnimationRepository.getAnimation(animationGroup, "$animationPrefix.$animation"))

    fun quirk(
        secondsBetweenOccurrences: Pair<Float, Float> = 8F to 30F,
        loopTimes: IntRange = 1..1,
        condition: (context: RenderContext) -> Boolean = { true },
        animation: (state: PosableState) -> StatefulAnimation
    ) = SimpleQuirk(
        secondsBetweenOccurrences = secondsBetweenOccurrences,
        loopTimes = loopTimes,
        condition = condition,
        animations = { listOf(animation(it)) }
    )

    fun quirkMultiple(
        secondsBetweenOccurrences: Pair<Float, Float> = 8F to 30F,
        loopTimes: IntRange = 1..1,
        condition: (state: RenderContext) -> Boolean = { true },
        animations: (state: PosableState) -> List<StatefulAnimation>
    ) = SimpleQuirk(
        secondsBetweenOccurrences = secondsBetweenOccurrences,
        loopTimes = loopTimes,
        condition = condition,
        animations = { animations(it) }
    )



}