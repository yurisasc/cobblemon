package com.cablemc.pokemoncobbled.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.EeveeModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.PokeBallModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.PokemonModel
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.ForgeHooksClient

object PokemonModelRepository {

    private val modelsBySpecies: MutableMap<Species, PokemonModel> = mutableMapOf()
    private val allModels: MutableList<PokemonModel> = mutableListOf()

    private val modelTexturesBySpecies: MutableMap<Species, ResourceLocation> = mutableMapOf()
    private val allModelTextures: MutableList<ResourceLocation> = mutableListOf()

    init {
        registerBaseSpeciesModel(PokemonSpecies.EEVEE, PokemonModel(EeveeModel.LAYER_LOCATION, EeveeModel::createBodyLayer) { EeveeModel(it) })
        registerBaseSpeciesModelTexture(PokemonSpecies.EEVEE, ResourceLocation(PokemonCobbled.MODID, "textures/pokemon/eevee-base.png"))
        registerBaseSpeciesModel(PokemonSpecies.BULBASAUR, PokemonModel(PokeBallModel.LAYER_LOCATION, PokeBallModel::createBodyLayer) { PokeBallModel(it) })
        registerBaseSpeciesModelTexture(PokemonSpecies.BULBASAUR, ResourceLocation(PokemonCobbled.MODID, "textures/pokemon/pokeball-base.png"))
    }

    private fun registerBaseSpeciesModel(species: Species, model: PokemonModel) {
        modelsBySpecies[species] = model
        allModels.add(model)
    }

    private fun registerBaseSpeciesModelTexture(species: Species, resourceLocation: ResourceLocation) {
        modelTexturesBySpecies[species] = resourceLocation
        allModelTextures.add(resourceLocation)
    }

    fun initializeModelLayers() {
        allModels.forEach { model ->
            ForgeHooksClient.registerLayerDefinition(model.layerLocation, model.layerDefinitionSupplier)
        }
    }

    fun initializeModels(context: EntityRendererProvider.Context) {
        allModels.forEach { model -> model.initialize(context) }
    }

    fun getModel(pokemon: Pokemon): PokemonModel {
        // TODO: This is just fetching by species at the moment. This will be developed further.
        return modelsBySpecies[pokemon.species] ?: throw IllegalStateException("pokemon has no appropriate model")
    }

    fun getModelTexture(pokemon: Pokemon): ResourceLocation {
        // TODO: This is just fetching by species at the moment. This will be developed further.
        return modelTexturesBySpecies[pokemon.species] ?: throw IllegalStateException("pokemon has no appropriate model texture")
    }

}