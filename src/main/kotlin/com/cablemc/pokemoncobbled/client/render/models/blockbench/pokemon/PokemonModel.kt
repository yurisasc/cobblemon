package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider

class PokemonModel(
    val layerLocation: ModelLayerLocation,
    val layerDefinitionSupplier: () -> LayerDefinition,
    val modelFactory: (ModelPart) -> EntityModel<PokemonEntity>
) {

    lateinit var entityModel: EntityModel<PokemonEntity>

    fun initialize(context: EntityRendererProvider.Context) {
        entityModel = modelFactory(context.bakeLayer(layerLocation))
    }

    // TODO: Probably some custom rendering / animation functions

}