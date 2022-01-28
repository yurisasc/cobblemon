package com.cablemc.pokemoncobbled.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.client.render.models.blockbench.BlockBenchModelWrapper
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.BlastoiseModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.BulbasaurModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.ButterfreeModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.CharizardModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.CharmanderModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.CharmeleonModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.DiglettModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.DugtrioModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.EeveeModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.EkansModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.GyaradosModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.IvysaurModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.MagikarpModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.SquirtleModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.VenusaurModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.WartortleModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.ZubatModel
import com.cablemc.pokemoncobbled.client.util.exists
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.resources.ResourceLocation

object PokemonModelRepository : ModelRepository<PokemonEntity>() {

    private val modelsBySpecies: MutableMap<Species, BlockBenchModelWrapper<PokemonEntity>> = mutableMapOf()
    private val modelTexturesBySpecies: MutableMap<Species, ResourceLocation> = mutableMapOf()
    // TODO: Temporary until we decide the texture system we want to go with and its capabilities
    private val shinyModelTexturesBySpecies: MutableMap<Species, ResourceLocation> = mutableMapOf()

    override fun registerAll() {
        registerBaseSpeciesModel(PokemonSpecies.BULBASAUR, BlockBenchModelWrapper(BulbasaurModel.LAYER_LOCATION, BulbasaurModel::createBodyLayer) { BulbasaurModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.IVYSAUR, BlockBenchModelWrapper(IvysaurModel.LAYER_LOCATION, IvysaurModel::createBodyLayer) { IvysaurModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.VENUSAUR, BlockBenchModelWrapper(VenusaurModel.LAYER_LOCATION, VenusaurModel::createBodyLayer) { VenusaurModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.CHARMANDER, BlockBenchModelWrapper(CharmanderModel.LAYER_LOCATION, CharmanderModel::createBodyLayer) { CharmanderModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.CHARMELEON, BlockBenchModelWrapper(CharmeleonModel.LAYER_LOCATION, CharmeleonModel::createBodyLayer) { CharmeleonModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.CHARIZARD, BlockBenchModelWrapper(CharizardModel.LAYER_LOCATION, CharizardModel::createBodyLayer) { CharizardModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.SQUIRTLE, BlockBenchModelWrapper(SquirtleModel.LAYER_LOCATION, SquirtleModel::createBodyLayer) { SquirtleModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.WARTORTLE, BlockBenchModelWrapper(WartortleModel.LAYER_LOCATION, WartortleModel::createBodyLayer) { WartortleModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.BLASTOISE, BlockBenchModelWrapper(BlastoiseModel.LAYER_LOCATION, BlastoiseModel::createBodyLayer) { BlastoiseModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.BUTTERFREE, BlockBenchModelWrapper(ButterfreeModel.LAYER_LOCATION, ButterfreeModel::createBodyLayer) { ButterfreeModel(it) })

        registerBaseSpeciesModel(PokemonSpecies.EKANS, BlockBenchModelWrapper(EkansModel.LAYER_LOCATION, EkansModel::createBodyLayer) { EkansModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.ZUBAT, BlockBenchModelWrapper(ZubatModel.LAYER_LOCATION, ZubatModel::createBodyLayer) { ZubatModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.DIGLETT, BlockBenchModelWrapper(DiglettModel.LAYER_LOCATION, DiglettModel::createBodyLayer) { DiglettModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.DUGTRIO, BlockBenchModelWrapper(DugtrioModel.LAYER_LOCATION, DugtrioModel::createBodyLayer) { DugtrioModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.MAGIKARP, BlockBenchModelWrapper(MagikarpModel.LAYER_LOCATION, MagikarpModel::createBodyLayer) { MagikarpModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.GYARADOS, BlockBenchModelWrapper(GyaradosModel.LAYER_LOCATION, GyaradosModel::createBodyLayer) { GyaradosModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.EEVEE, BlockBenchModelWrapper(EeveeModel.LAYER_LOCATION, EeveeModel::createBodyLayer) { EeveeModel(it) })
    }

    override fun clear() {
        super.clear()
        modelsBySpecies.clear()
        modelTexturesBySpecies.clear()
    }

    private fun registerBaseSpeciesModel(species: Species, model: BlockBenchModelWrapper<PokemonEntity>) {
        modelsBySpecies[species] = model
        addModel(model)
        registerBaseSpeciesModelTexture(species)
        registerShinySpeciesModelTexture(species)
    }

    private fun registerBaseSpeciesModelTexture(species: Species) {
        modelTexturesBySpecies[species] = baseTextureFor(species)
    }

    private fun registerShinySpeciesModelTexture(species: Species) {
        val shinyTexture = shinyTextureFor(species)
        shinyModelTexturesBySpecies[species] = if (shinyTexture.exists()) shinyTexture else baseTextureFor(species)
    }

    private fun baseTextureFor(species: Species) = cobbledResource("textures/pokemon/${species.name}-base.png")
    private fun shinyTextureFor(species: Species) = cobbledResource("textures/pokemon/${species.name}-shiny.png")

    fun getModel(pokemon: Pokemon): BlockBenchModelWrapper<PokemonEntity> {
        // TODO: This is just fetching by species at the moment. This will be developed further.
        return modelsBySpecies[pokemon.species] ?: throw IllegalStateException("pokemon has no appropriate model")
    }

    fun getModelTexture(pokemon: Pokemon): ResourceLocation {
        // TODO: This is just fetching by species at the moment. This will be developed further.
        if (pokemon.shiny) {
            return shinyModelTexturesBySpecies[pokemon.species] ?: throw IllegalStateException("pokemon has no appropriate shiny model texture")
        }
        return modelTexturesBySpecies[pokemon.species] ?: throw IllegalStateException("pokemon has no appropriate model texture")
    }

}