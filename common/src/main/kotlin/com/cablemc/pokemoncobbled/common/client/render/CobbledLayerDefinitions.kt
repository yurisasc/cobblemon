package com.cablemc.pokemoncobbled.common.client.render

import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import java.util.function.Supplier

object CobbledTexturedModelDatas {

    val layerDefinitions = mutableMapOf<EntityModelLayer, Supplier<TexturedModelData>>()

    fun registerTexturedModelData(layerLocation: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        layerDefinitions[layerLocation] = supplier
    }
}