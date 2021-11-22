package com.cablemc.pokemoncobbled.client.render.models.smd.repository

import com.cablemc.pokemoncobbled.client.render.models.smd.SmdModel
import net.minecraft.resources.ResourceLocation

interface ModelRepository {

    suspend fun getModelAsync(location: ResourceLocation): SmdModel?

    fun getModel(location: ResourceLocation): SmdModel?

}