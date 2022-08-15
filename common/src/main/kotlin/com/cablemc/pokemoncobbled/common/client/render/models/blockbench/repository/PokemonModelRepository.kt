package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BeedrillModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BlastoiseModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BulbasaurModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ButterfreeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CaterpieModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CharizardModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CharmanderModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CharmeleonModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.DiglettModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.DugtrioModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.EeveeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.GyaradosModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.IvysaurModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.JsonPokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.KakunaModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MagikarpModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MetapodModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PidgeotModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PidgeottoModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PidgeyModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.RaticateModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.RattataModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.SquirtleModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.VenusaurModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.WartortleModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.WeedleModel
import com.cablemc.pokemoncobbled.common.client.render.pokemon.ModelLayer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.RegisteredSpeciesRendering
import com.cablemc.pokemoncobbled.common.client.render.pokemon.SpeciesAssetResolver
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.endsWith
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.io.path.Path
import kotlin.io.path.pathString
import net.minecraft.client.model.ModelPart
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

object PokemonModelRepository : ModelRepository<PokemonEntity>() {
    val posers = mutableMapOf<Identifier, (ModelPart) -> PokemonPoseableModel>()
    val renders = mutableMapOf<Identifier, RegisteredSpeciesRendering>()

    fun registerPosers(resourceManager: ResourceManager) {
        posers.clear()
        registerInBuiltPosers()
        registerJsonPosers(resourceManager)
    }

    fun registerInBuiltPosers() {
        posers[cobbledResource("bulbasaur")] = { BulbasaurModel(it) }
        posers[cobbledResource("ivysaur")] = { IvysaurModel(it) }
        posers[cobbledResource("venusaur")] = { VenusaurModel(it) }
        posers[cobbledResource("charmander")] = { CharmanderModel(it) }
        posers[cobbledResource("charmeleon")] = { CharmeleonModel(it) }
        posers[cobbledResource("charizard")] = { CharizardModel(it) }
        posers[cobbledResource("squirtle")] = { SquirtleModel(it) }
        posers[cobbledResource("wartortle")] = { WartortleModel(it) }
        posers[cobbledResource("blastoise")] = { BlastoiseModel(it) }
        posers[cobbledResource("caterpie")] = { CaterpieModel(it) }
        posers[cobbledResource("metapod")] = { MetapodModel(it) }
        posers[cobbledResource("butterfree")] = { ButterfreeModel(it) }
        posers[cobbledResource("weedle")] = { WeedleModel(it) }
        posers[cobbledResource("kakuna")] = { KakunaModel(it) }
        posers[cobbledResource("beedrill")] = { BeedrillModel(it) }
        posers[cobbledResource("rattata")] = { RattataModel(it) }
        posers[cobbledResource("raticate")] = { RaticateModel(it) }
        posers[cobbledResource("eevee")] = { EeveeModel(it) }

        // These are still substitutes in-game because we don't have these as aspect JSONs yet. Not animated.
        posers[cobbledResource("magikarp")] = { MagikarpModel(it) }
        posers[cobbledResource("gyarados")] = { GyaradosModel(it) }
        posers[cobbledResource("pidgey")] = { PidgeyModel(it) }
        posers[cobbledResource("pidgeotto")] = { PidgeottoModel(it) }
        posers[cobbledResource("pidgeot")] = { PidgeotModel(it) }
        posers[cobbledResource("diglett")] = { DiglettModel(it) }
        posers[cobbledResource("dugtrio")] = { DugtrioModel(it) }
        posers[cobbledResource("zubat")] = { DugtrioModel(it) }
    }

    fun registerJsonPosers(resourceManager: ResourceManager) {
        resourceManager.findResources(Path("bedrock/posers").pathString) { path -> path.endsWith(".json") }.forEach { identifier, resource ->
            resource.inputStream.use { stream ->
                val json = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                posers[resolvedIdentifier] = {
                    JsonPokemonPoseableModel.JsonPokemonPoseableModelAdapter.modelPart = it
                    JsonPokemonPoseableModel.gson.fromJson(json, JsonPokemonPoseableModel::class.java)
                }
            }
        }
    }

    fun registerSpeciesAssetResolvers(resourceManager: ResourceManager) {
        resourceManager.findResources(Path("bedrock/species").pathString) { path -> path.endsWith(".json") }.forEach { identifier, resource ->
            resource.inputStream.use { stream ->
                val json = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                renders[resolvedIdentifier] = RegisteredSpeciesRendering(
                    resolvedIdentifier,
                    SpeciesAssetResolver.GSON.fromJson(json, SpeciesAssetResolver::class.java)
                )
            }
        }
    }

    override fun registerAll() {
    }

    override fun reload(resourceManager: ResourceManager) {
        PokemonCobbled.LOGGER.info("Initializing Pokémon models")
        this.renders.clear()
        this.posers.clear()
        registerPosers(resourceManager)
        registerSpeciesAssetResolvers(resourceManager)
        initializeModelLayers()
    }

    fun getPoser(species: Species, aspects: Set<String>): PokemonPoseableModel {
        try {
            val poser = this.renders[species.resourceIdentifier]?.getPoser(aspects)
            if (poser != null) {
                return poser
            }
        } catch(_: IllegalStateException) { }
        return this.renders[cobbledResource("substitute")]!!.getPoser(aspects)
    }

    fun getTexture(species: Species, aspects: Set<String>): Identifier {
        try {
            val texture = this.renders[species.resourceIdentifier]?.getTexture(aspects)
            if (texture != null) {
                return texture
            }
        } catch(_: IllegalStateException) { }
        return this.renders[cobbledResource("substitute")]!!.getTexture(aspects)
    }

    fun getLayers(species: Species, aspects: Set<String>): List<ModelLayer> {
        try {
            val layers = this.renders[species.resourceIdentifier]?.getLayers(aspects)
            if (layers != null) {
                return layers
            }
        } catch(_: IllegalStateException) { }
        return this.renders[cobbledResource("substitute")]!!.getLayers(aspects)
    }
}