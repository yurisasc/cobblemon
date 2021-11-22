package com.cablemc.pokemoncobbled.client.render.models.smd.repository

import com.cablemc.pokemoncobbled.client.render.models.smd.SmdModel
import com.google.common.cache.LoadingCache
import net.minecraft.resources.ResourceLocation

class CachedModelRepository(
    private val cache: LoadingCache<ResourceLocation, SmdModel>
): ModelRepository {

    override suspend fun getModelAsync(location: ResourceLocation): SmdModel? {
        return cache[location]
    }

    override fun getModel(location: ResourceLocation): SmdModel {
        return cache[location]
    }

}