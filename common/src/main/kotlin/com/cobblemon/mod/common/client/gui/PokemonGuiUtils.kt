/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.toHex
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import org.joml.Vector3f

fun drawProfilePokemon(
    renderablePokemon: RenderablePokemon,
    matrixStack: MatrixStack,
    rotation: Quaternionf,
    poseType: PoseType = PoseType.PROFILE,
    state: PosableState,
    partialTicks: Float,
    scale: Float = 20F,
    applyProfileTransform: Boolean = true,
    applyBaseScale: Boolean = false,
    r: Float = 1F,
    g: Float = 1F,
    b: Float = 1F,
    a: Float = 1F
) = drawProfilePokemon(
    species = renderablePokemon.species.resourceIdentifier,
    aspects = renderablePokemon.aspects,
    matrixStack = matrixStack,
    rotation = rotation,
    poseType = poseType,
    state = state,
    partialTicks = partialTicks,
    scale = scale,
    applyProfileTransform = applyProfileTransform,
    applyBaseScale = applyBaseScale,
    r = r,
    g = g,
    b = b,
    a = a,
)

fun drawProfilePokemon(
    species: Identifier,
    aspects: Set<String>,
    matrixStack: MatrixStack,
    rotation: Quaternionf,
    poseType: PoseType = PoseType.PROFILE,
    state: PosableState,
    partialTicks: Float,
    scale: Float = 20F,
    applyProfileTransform: Boolean = true,
    applyBaseScale: Boolean = false,
    r: Float = 1F,
    g: Float = 1F,
    b: Float = 1F,
    a: Float = 1F
) {
    val model = PokemonModelRepository.getPoser(species, aspects)
    val texture = PokemonModelRepository.getTexture(species, aspects, state.animationSeconds)

    val context = RenderContext()
    model.context = context
    PokemonModelRepository.getTextureNoSubstitute(species, aspects, 0f).let { context.put(RenderContext.TEXTURE, it) }
    val baseScale = PokemonSpecies.getByIdentifier(species)!!.getForm(aspects).baseScale
    context.put(RenderContext.SCALE, baseScale)
    context.put(RenderContext.SPECIES, species)
    context.put(RenderContext.ASPECTS, aspects)
    context.put(RenderContext.RENDER_STATE, RenderContext.RenderState.PROFILE)
    context.put(RenderContext.POSABLE_STATE, state)

    state.currentModel = model
    state.currentAspects = aspects

    val renderType = RenderLayer.getEntityCutout(texture)

    RenderSystem.applyModelViewMatrix()
    matrixStack.scale(scale, scale, -scale)

    state.setPoseToFirstSuitable(poseType)
    state.updatePartialTicks(partialTicks)
    model.applyAnimations(null, state, 0F, 0F, 0F, 0F, 0F)

    if (applyProfileTransform) {
        matrixStack.translate(model.profileTranslation.x, model.profileTranslation.y,  model.profileTranslation.z - 4.0)
        matrixStack.scale(model.profileScale, model.profileScale, 1 / model.profileScale)
    } else {
        matrixStack.translate(0F, 0F, -4.0F)
        if (applyBaseScale) matrixStack.scale(baseScale, baseScale, 1 / baseScale)
    }
    matrixStack.multiply(rotation)
    DiffuseLighting.method_34742()
    val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
    rotation.conjugate()
    entityRenderDispatcher.rotation = rotation
    entityRenderDispatcher.setRenderShadows(true)

    val bufferSource = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
    val buffer = bufferSource.getBuffer(renderType)
    val light1 = Vector3f(-1F, 1F, 1.0F)
    val light2 = Vector3f(1.3F, -1F, 1.0F)
    RenderSystem.setShaderLights(light1, light2)
    val packedLight = LightmapTextureManager.pack(11, 7)

    val colour = toHex(r, g, b, a)
    model.withLayerContext(bufferSource, state, PokemonModelRepository.getLayers(species, aspects)) {
        model.render(context, matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, colour)
        bufferSource.draw()
    }
    model.setDefault()
    entityRenderDispatcher.setRenderShadows(true)
    DiffuseLighting.enableGuiDepthLighting()
}

