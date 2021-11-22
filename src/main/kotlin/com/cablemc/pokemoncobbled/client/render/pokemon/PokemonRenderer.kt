package com.cablemc.pokemoncobbled.client.render.pokemon

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.render.models.smd.SmdModel
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.SmdAnimationLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.SmdModelLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.SmdPQCLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.SmdAnimationFileLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.SmdModelFileLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.SmdPQCFileLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.renderer.SmdModelRenderer
import com.cablemc.pokemoncobbled.client.render.models.smd.repository.CachedModelRepository
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation


class PokemonRenderer(
    ctx: EntityRendererProvider.Context,
    private val modelRenderer: SmdModelRenderer
) : EntityRenderer<PokemonEntity>(ctx) {

    companion object {
        val fileLoader = SmdPQCFileLoader()
        val modelFileLoader = SmdModelFileLoader()
        val modelLoader = SmdModelLoader(modelFileLoader)
        val pqcLoader = SmdPQCLoader(fileLoader, modelLoader, SmdAnimationLoader(SmdAnimationFileLoader()))
        val loadingCache: LoadingCache<ResourceLocation, SmdModel> = CacheBuilder.newBuilder()
            .build(object : CacheLoader<ResourceLocation, SmdModel>() {
                override fun load(location: ResourceLocation): SmdModel {
                    return pqcLoader.load(location)
                }
            })
        val modelRepo = CachedModelRepository(loadingCache)
    }

    override fun render(
        entity: PokemonEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
//        val clientComponent = entity.clientComponent

        val path = "pokemon/bulbasaur/forms/default/model/default.pqc"
        val modelLocation = ResourceLocation(PokemonCobbled.MODID, path)
        val texturePath = "default"
        val path2 = "pokemon/bulbasaur/forms/default/model/textures/$texturePath.png"
        val modelTextureLoc = ResourceLocation(PokemonCobbled.MODID, path2)

        val model = modelRepo.getModel(modelLocation)

        /*
         * We change the texture on the model's mesh to accommodate for different textures to be implemented
         * for a single model. After the model is rendered, we simply set the default texture back.
         */

        val defaultTexture = model.skeleton.mesh.texture
        setModelTexture(model, modelTextureLoc)

        model.setAnimation("idle")
        modelRenderer.render(poseStack, model)

        model.skeleton.mesh.texture = defaultTexture
    }

    private fun setModelTexture(model: SmdModel, texture: ResourceLocation) {
        // Check that texture exists in assets
        val modelTextureURL = PokemonCobbledClient::class.java.getResource("/assets/${texture.namespace}/${texture.path}")
        if (modelTextureURL != null) {
            model.skeleton.mesh.texture = texture
        }
    }

    override fun getTextureLocation(entity: PokemonEntity) = null

    // TODO: Make better. Should take into account player frustum to prevent unnecessary rendering.
    override fun shouldRender(
        livingEntityIn: PokemonEntity,
        camera: Frustum,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        // frustum has a function for AABB, ez once pokemon have bounding boxes
        return true
    }

}