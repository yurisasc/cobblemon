package com.cobblemon.mod.common.client.render.model

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.util.Identifier

class CobblemonModelLoader {
    val bakedModelMap = mutableMapOf<Identifier, BakedModel>()

    class CobblemonBaker : Baker{
        override fun getOrLoadModel(id: Identifier?): UnbakedModel {
            TODO("Not yet implemented")
        }

        override fun bake(id: Identifier?, settings: ModelBakeSettings?): BakedModel? {
            val unbakedModel = getOrLoadModel(id)
            val bakedModel = unbakedModel.bake(this, null, settings, id)
            TODO("Not yet implemented")
        }

    }
}