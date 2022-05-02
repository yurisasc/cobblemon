package com.cablemc.pokemoncobbled.common

import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import java.util.function.Supplier

interface PokemonCobbledClientImplementation {
    fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>)
}