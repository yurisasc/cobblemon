package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.BlockBenchModelWrapper
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.*
import com.cablemc.pokemoncobbled.common.client.render.pokemon.RegisteredSpeciesRendering
import com.cablemc.pokemoncobbled.common.client.render.pokemon.SpeciesAssetResolver
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.util.Identifier

object PokemonModelRepository : ModelRepository<PokemonEntity>() {

    private val modelsBySpecies: MutableMap<Species, BlockBenchModelWrapper<PokemonEntity>> = mutableMapOf()
    private val modelTexturesBySpecies: MutableMap<Species, Identifier> = mutableMapOf()
    // TODO: Temporary until we decide the texture system we want to go with and its capabilities
    private val shinyModelTexturesBySpecies: MutableMap<Species, Identifier> = mutableMapOf()

    val animators = mutableMapOf<String, (ModelPart) -> PokemonPoseableModel>()
    val species = mutableMapOf<Species, RegisteredSpeciesRendering>()

    override fun registerAll() {
        registerSpeciesWithAnimator(PokemonSpecies.CHARIZARD) { CharizardModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.CHARMELEON) { CharmeleonModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.CHARMANDER) { CharmanderModel(it) }



        registerBaseSpeciesModel(PokemonSpecies.BULBASAUR, BlockBenchModelWrapper(BulbasaurModel.LAYER_LOCATION, BulbasaurModel::createBodyLayer) { BulbasaurModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.IVYSAUR, BlockBenchModelWrapper(IvysaurModel.LAYER_LOCATION, IvysaurModel::createBodyLayer) { IvysaurModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.VENUSAUR, BlockBenchModelWrapper(VenusaurModel.LAYER_LOCATION, VenusaurModel::createBodyLayer) { VenusaurModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.SQUIRTLE, BlockBenchModelWrapper(SquirtleModel.LAYER_LOCATION, SquirtleModel::createBodyLayer) { SquirtleModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.WARTORTLE, BlockBenchModelWrapper(WartortleModel.LAYER_LOCATION, WartortleModel::createBodyLayer) { WartortleModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.BLASTOISE, BlockBenchModelWrapper(BlastoiseModel.LAYER_LOCATION, BlastoiseModel::createBodyLayer) { BlastoiseModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.BUTTERFREE, BlockBenchModelWrapper(ButterfreeModel.LAYER_LOCATION, ButterfreeModel::createBodyLayer) { ButterfreeModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.PIDGEY, BlockBenchModelWrapper(PidgeyModel.LAYER_LOCATION, PidgeyModel::createBodyLayer) { PidgeyModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.PIDGEOTTO, BlockBenchModelWrapper(PidgeottoModel.LAYER_LOCATION, PidgeottoModel::createBodyLayer) { PidgeottoModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.PIDGEOT, BlockBenchModelWrapper(PidgeotModel.LAYER_LOCATION, PidgeotModel::createBodyLayer) { PidgeotModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.EKANS, BlockBenchModelWrapper(EkansModel.LAYER_LOCATION, EkansModel::createBodyLayer) { EkansModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.ZUBAT, BlockBenchModelWrapper(ZubatModel.LAYER_LOCATION, ZubatModel::createBodyLayer) { ZubatModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.DIGLETT, BlockBenchModelWrapper(DiglettModel.LAYER_LOCATION, DiglettModel::createBodyLayer) { DiglettModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.DUGTRIO, BlockBenchModelWrapper(DugtrioModel.LAYER_LOCATION, DugtrioModel::createBodyLayer) { DugtrioModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.MAGIKARP, BlockBenchModelWrapper(MagikarpModel.LAYER_LOCATION, MagikarpModel::createBodyLayer) { MagikarpModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.GYARADOS, BlockBenchModelWrapper(GyaradosModel.LAYER_LOCATION, GyaradosModel::createBodyLayer) { GyaradosModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.EEVEE, BlockBenchModelWrapper(EeveeModel.LAYER_LOCATION, EeveeModel::createBodyLayer) { EeveeModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.RATTATA, BlockBenchModelWrapper(RattataModel.LAYER_LOCATION, RattataModel::createBodyLayer) { RattataModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.RATICATE, BlockBenchModelWrapper(RaticateModel.LAYER_LOCATION, RaticateModel::createBodyLayer) { RaticateModel(it) })

    }

    override fun initializeModelLayers() {
        super.initializeModelLayers()
        species.values.forEach(RegisteredSpeciesRendering::initializeLayers)
    }

    override fun initializeModels(context: EntityRendererFactory.Context) {
        super.initializeModels(context)
        species.values.forEach { it.parseModels(context) }
    }

    override fun reload() {
        super.reload()
        species.values.forEach { it.reload() }
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
        shinyModelTexturesBySpecies[species] = /* TODO do this later or just wait until resolved texture searchin if (shinyTexture.exists()) shinyTexture else */ baseTextureFor(species)
    }

    fun registerAnimator(name: String, animatorSupplier: (ModelPart) -> PokemonPoseableModel) {
        animators[name] = animatorSupplier
    }

    fun registerSpeciesWithAnimator(species: Species, animatorSupplier: (ModelPart) -> PokemonPoseableModel) {
        registerAnimator(species.name, animatorSupplier)
        registerSpecies(species)
    }

    fun registerSpecies(species: Species) {
        this.species[species] = RegisteredSpeciesRendering(
            species,
            SpeciesAssetResolver.load("geo/species/${species.name}.json")
        )
    }

    private fun baseTextureFor(species: Species) = cobbledResource("textures/pokemon/${species.name}-base.png")
    private fun shinyTextureFor(species: Species) = cobbledResource("textures/pokemon/${species.name}-shiny.png")

    fun getEntityModel(species: Species, aspects: Set<String>): PokemonPoseableModel {
        this.species[species]?.let {
            return it.getEntityModel(aspects)
        }
        // TODO: This is just fetching by species at the moment. This will be developed further.
        return modelsBySpecies[species]?.entityModel as? PokemonPoseableModel ?: throw IllegalStateException("${species.name} has no appropriate model")
    }

    fun getModelTexture(species: Species, aspects: Set<String>): Identifier {
        this.species[species]?.let {
            return it.getTexture(aspects)
        }

        return modelTexturesBySpecies[species] ?: throw IllegalStateException("pokemon has no appropriate model texture")
    }

}