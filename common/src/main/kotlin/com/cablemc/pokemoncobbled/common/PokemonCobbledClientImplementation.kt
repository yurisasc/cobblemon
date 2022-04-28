package com.cablemc.pokemoncobbled.common

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import java.util.function.Supplier

interface PokemonCobbledClientImplementation {
    fun registerLayer(layerLocation: ModelLayerLocation, supplier: Supplier<LayerDefinition>)
}