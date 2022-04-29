package com.cablemc.pokemoncobbled.common.client.gui

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Quaternion

fun drawProfilePokemon(
    pokemon: Pokemon,
    matrixStack: MatrixStack,
    rotation: Quaternion,
    scale: Float = 20F
) {
    val model = PokemonModelRepository.getModel(pokemon).entityModel
    val texture = PokemonModelRepository.getModelTexture(pokemon)

    val renderType = model.getLayer(texture)

    RenderSystem.applyModelViewMatrix()
    matrixStack.scale(scale, scale, -scale)

    if (model is PokemonPoseableModel) {
        model.setupAnimStateless(PoseType.PROFILE)
        matrixStack.translate(model.profileTranslation.x, model.profileTranslation.y, -10.0)
        matrixStack.scale(model.profileScale, model.profileScale, 0.01F)
    }

    matrixStack.multiply(rotation)
    DiffuseLighting.method_34742()
    val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
    rotation.conjugate()
    entityRenderDispatcher.rotation = rotation
    entityRenderDispatcher.setRenderShadows(false)

    val bufferSource = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
    val buffer = bufferSource.getBuffer(renderType)
    val packedLight = LightmapTextureManager.pack(15, 15)
    model.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
    bufferSource.draw()
    entityRenderDispatcher.setRenderShadows(true)
    DiffuseLighting.enableGuiDepthLighting()
}

