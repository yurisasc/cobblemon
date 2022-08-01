package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.common.api.data.DataRegistry
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
    //val species = mutableMapOf<Species, RegisteredSpeciesRendering>()
    private val renders = hashMapOf<Identifier, RegisteredSpeciesRendering>()

    override fun registerAll() {
        // ToDo decide what to do here, ideally we don't want this to be a thing anymore and instead use a DataRegistry for the client assets
        /*
        registerSpeciesWithAnimator(PokemonSpecies.BULBASAUR) { BulbasaurModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.IVYSAUR) { IvysaurModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.VENUSAUR) { VenusaurModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.SQUIRTLE) { SquirtleModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.WARTORTLE) { WartortleModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.BLASTOISE) { BlastoiseModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.CHARMELEON) { CharmeleonModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.CHARMANDER) { CharmanderModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.CHARIZARD) { CharizardModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.CATERPIE) { CaterpieModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.METAPOD) { MetapodModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.BUTTERFREE) { ButterfreeModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.WEEDLE) { WeedleModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.KAKUNA) { KakunaModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.BEEDRILL) { BeedrillModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.RATTATA) { RattataModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.RATICATE) { RaticateModel(it) }
        registerSpeciesWithAnimator(PokemonSpecies.EEVEE) { EeveeModel(it) }

        registerBaseSpeciesModel(PokemonSpecies.PIDGEY, BlockBenchModelWrapper(PidgeyModel.LAYER_LOCATION, PidgeyModel::createBodyLayer) { PidgeyModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.PIDGEOTTO, BlockBenchModelWrapper(PidgeottoModel.LAYER_LOCATION, PidgeottoModel::createBodyLayer) { PidgeottoModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.PIDGEOT, BlockBenchModelWrapper(PidgeotModel.LAYER_LOCATION, PidgeotModel::createBodyLayer) { PidgeotModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.EKANS, BlockBenchModelWrapper(EkansModel.LAYER_LOCATION, EkansModel::createBodyLayer) { EkansModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.ZUBAT, BlockBenchModelWrapper(ZubatModel.LAYER_LOCATION, ZubatModel::createBodyLayer) { ZubatModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.DIGLETT, BlockBenchModelWrapper(DiglettModel.LAYER_LOCATION, DiglettModel::createBodyLayer) { DiglettModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.DUGTRIO, BlockBenchModelWrapper(DugtrioModel.LAYER_LOCATION, DugtrioModel::createBodyLayer) { DugtrioModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.MAGIKARP, BlockBenchModelWrapper(MagikarpModel.LAYER_LOCATION, MagikarpModel::createBodyLayer) { MagikarpModel(it) })
        registerBaseSpeciesModel(PokemonSpecies.GYARADOS, BlockBenchModelWrapper(GyaradosModel.LAYER_LOCATION, GyaradosModel::createBodyLayer) { GyaradosModel(it) })
         */
    }

    override fun initializeModelLayers() {
        super.initializeModelLayers()
        this.renders.values.forEach(RegisteredSpeciesRendering::initializeLayers)
    }

    override fun initializeModels(context: EntityRendererFactory.Context) {
        super.initializeModels(context)
        this.renders.values.forEach { it.parseModels(context) }
    }

    override fun reload() {
        super.reload()
        this.renders.values.forEach { it.reload() }
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

    fun registerSpecies(species: Species): Boolean {
        // ToDo Consider how we want to handle "safety"
        return try {
            this.renders[species.resourceIdentifier] = RegisteredSpeciesRendering(
                species,
                SpeciesAssetResolver.load("bedrock/species/${species.name}.json", species.resourceIdentifier.namespace)
            )
            true
        } catch (e: NullPointerException) {
            false
        }
    }

    private fun baseTextureFor(species: Species) = cobbledResource("textures/pokemon/${species.name}-base.png")
    private fun shinyTextureFor(species: Species) = cobbledResource("textures/pokemon/${species.name}-shiny.png")

    fun getEntityModel(species: Species, aspects: Set<String>): PokemonPoseableModel {
        this.renders[species.resourceIdentifier]?.let {
            return it.getEntityModel(aspects)
        }
        // TODO: This is just fetching by species at the moment. This will be developed further.
        return modelsBySpecies[species]?.entityModel as? PokemonPoseableModel ?: throw IllegalStateException("${species.name} has no appropriate model")
    }

    fun getModelTexture(species: Species, aspects: Set<String>): Identifier {
        this.renders[species.resourceIdentifier]?.let {
            return it.getTexture(aspects)
        }

        return modelTexturesBySpecies[species] ?: throw IllegalStateException("pokemon has no appropriate model texture")
    }

}