package com.cablemc.pokemoncobbled.client.render.models.smd.repository

import com.cablemc.pokemoncobbled.client.render.models.smd.SmdModel
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.SmdPQCLoader
import net.minecraft.resources.ResourceLocation

class DefaultModelRepository(
    private val pqcLoader: SmdPQCLoader
) : ModelRepository {

    override suspend fun getModelAsync(location: ResourceLocation): SmdModel = pqcLoader.load(location)

    override fun getModel(location: ResourceLocation): SmdModel = pqcLoader.load(location)
}