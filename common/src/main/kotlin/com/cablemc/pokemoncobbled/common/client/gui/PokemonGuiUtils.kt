package com.cablemc.pokemoncobbled.common.client.gui

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture

fun drawProfilePokemon(
    pokemon: Pokemon,
    poseStack: PoseStack,
    rotation: Quaternion,
    scale: Float = 20F
) {
    val model = PokemonModelRepository.getModel(pokemon).entityModel
    val texture = PokemonModelRepository.getModelTexture(pokemon)

    val renderType = model.renderType(texture)

    RenderSystem.applyModelViewMatrix()
    poseStack.scale(scale, scale, -scale)

    if (model is PokemonPoseableModel) {
        model.setupAnimStateless(PoseType.PROFILE)
        poseStack.translate(model.profileTranslation.x, model.profileTranslation.y, -10.0)
        poseStack.scale(model.profileScale, model.profileScale, 0.01F)
    }

    poseStack.mulPose(rotation)
    Lighting.setupForEntityInInventory()
    val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
    rotation.conj()
    entityRenderDispatcher.overrideCameraOrientation(rotation)
    entityRenderDispatcher.setRenderShadow(false)

    val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
    val buffer = bufferSource.getBuffer(renderType)

    val packedLight = LightTexture.pack(15, 15)
    model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F)
    bufferSource.endBatch()
    entityRenderDispatcher.setRenderShadow(true)
    Lighting.setupFor3DItems()
}

