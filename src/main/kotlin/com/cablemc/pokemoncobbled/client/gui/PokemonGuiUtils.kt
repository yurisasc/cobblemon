package com.cablemc.pokemoncobbled.client.gui

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture

fun drawPokemon(
    pokemon: Pokemon,
    poseStack: PoseStack
) {
    val model = PokemonModelRepository.getModel(pokemon).entityModel
    val texture = PokemonModelRepository.getModelTexture(pokemon)
    val minecraft = Minecraft.getInstance()

    val renderType = model.renderType(texture)

    val scale = 1200F / minecraft.window.guiScale.toFloat()
    RenderSystem.applyModelViewMatrix()
    poseStack.translate(minecraft.window.guiScaledWidth / 2.0, 0.0, -100.0)
    poseStack.scale(scale, scale * 0.75F, -scale * 0.1F)
    val quaternion1 = Vector3f.YP.rotationDegrees(-32F)

    if (model is PokemonPoseableModel) {
        model.setupAnimStateless(PoseType.NONE)
        poseStack.translate(model.portraitTranslation.x, model.portraitTranslation.y, model.portraitTranslation.z)
        poseStack.scale(model.portraitScale, model.portraitScale, model.portraitScale)
    }

    poseStack.mulPose(quaternion1)
    Lighting.setupForEntityInInventory()
    val entityRenderDispatcher = minecraft.entityRenderDispatcher
    quaternion1.conj()
    entityRenderDispatcher.overrideCameraOrientation(quaternion1)
    entityRenderDispatcher.setRenderShadow(false)

    val bufferSource = minecraft.renderBuffers().bufferSource()
    val buffer = bufferSource.getBuffer(renderType)

    val packedLight = LightTexture.pack(15, 15)
    model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F)
    bufferSource.endBatch()
    entityRenderDispatcher.setRenderShadow(true)
    RenderSystem.applyModelViewMatrix()
    Lighting.setupFor3DItems()
}

