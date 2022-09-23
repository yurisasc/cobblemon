/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BeedrillModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BellossomModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BlastoiseModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.BulbasaurModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ButterfreeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CaterpieModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CharizardModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CharmanderModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CharmeleonModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ClefableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ClefairyModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.CleffaModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.DiglettModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.DugtrioModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.EeveeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.EkansModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ElectrodeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.GloomModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.GyaradosModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.IvysaurModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.JsonPokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.KakunaModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.KrabbyModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.LaprasModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MagikarpModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MankeyModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MetapodModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.OddishModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ParasModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ParasectModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PidgeotModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PidgeottoModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PidgeyModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PrimeapeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.RaticateModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.RattataModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.SquirtleModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.VenusaurModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.VileplumeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.VoltorbModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.WartortleModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.WeedleModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.ZubatModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MachopModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MachokeModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.MachampModel
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
        posers[cobbledResource("magikarp")] = { MagikarpModel(it) }
        posers[cobbledResource("gyarados")] = { GyaradosModel(it) }
        posers[cobbledResource("pidgey")] = { PidgeyModel(it) }
        posers[cobbledResource("pidgeotto")] = { PidgeottoModel(it) }
        posers[cobbledResource("pidgeot")] = { PidgeotModel(it) }
        posers[cobbledResource("diglett")] = { DiglettModel(it) }
        posers[cobbledResource("dugtrio")] = { DugtrioModel(it) }
        posers[cobbledResource("zubat")] = { ZubatModel(it) }
        posers[cobbledResource("cleffa")] = { CleffaModel(it) }
        posers[cobbledResource("clefable")] = { ClefableModel(it) }
        posers[cobbledResource("clefairy")] = { ClefairyModel(it) }
        posers[cobbledResource("krabby")] = { KrabbyModel(it) }
        posers[cobbledResource("paras")] = { ParasModel(it) }
        posers[cobbledResource("parasect")] = { ParasectModel(it) }
        posers[cobbledResource("mankey")] = { MankeyModel(it) }
        posers[cobbledResource("primeape")] = { PrimeapeModel(it) }
        posers[cobbledResource("oddish")] = { OddishModel(it) }
        posers[cobbledResource("gloom")] = { GloomModel(it) }
        posers[cobbledResource("vileplume")] = { VileplumeModel(it) }
        posers[cobbledResource("bellossom")] = { BellossomModel(it) }
        posers[cobbledResource("voltorb")] = { VoltorbModel(it) }
        posers[cobbledResource("electrode")] = { ElectrodeModel(it) }
        posers[cobbledResource("lapras")] = { LaprasModel(it) }
        posers[cobbledResource("ekans")] = { EkansModel(it) }
        posers[cobbledResource("machop")] = { MachopModel(it) }
        posers[cobbledResource("machoke")] = { MachokeModel(it) }
        posers[cobbledResource("machamp")] = { MachampModel(it) }
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