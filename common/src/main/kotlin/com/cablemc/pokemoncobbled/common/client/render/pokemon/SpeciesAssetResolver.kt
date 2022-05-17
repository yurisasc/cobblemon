package com.cablemc.pokemoncobbled.common.client.render.pokemon

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.TexturedModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.util.adapters.IdentifierAdapter
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.google.gson.GsonBuilder
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

/**
 * All the information required for rendering a Pokémon [Species] with aspects.
 *
 * @author Hiroku
 * @since May 14th, 2022
 */
class RegisteredSpeciesRendering(
    val species: Species,
    private val assetResolver: SpeciesAssetResolver
) {
    class LayerData(val layer: EntityModelLayer, var texturedModelData: TexturedModelData)

    val animators = mutableMapOf<Pair<String, Identifier>, PokemonPoseableModel>()
    val models = mutableMapOf<Identifier, ModelPart>()
    val layers = mutableMapOf<Identifier, LayerData>()

    init {
        assetResolver.getAllModels().forEach {
            layers[it] = LayerData(
                layer = EntityModelLayer(cobbledResource(species.name), it.path),
                texturedModelData = TexturedModel.from(it.path).create()
            )
        }
    }

    fun reload() {
        animators.values.forEach {
            it.poses.clear()
            it.registerPoses()
        }
    }

    fun initializeLayers() {
        layers.values.forEach {  data ->
            PokemonCobbledClient.implementation.registerLayer(data.layer) { data.texturedModelData }
        }
    }

    fun parseModels(context: EntityRendererFactory.Context) {
        assetResolver.getAllModels().forEach { model ->
            models[model] = context.modelLoader.getModelPart(layers[model]!!.layer)
        }
    }

    fun getEntityModel(aspects: Set<String>): PokemonPoseableModel {
        val animatorName = assetResolver.getAnimator(aspects)
        val animatorSupplier = PokemonModelRepository.animators[animatorName] ?: throw IllegalStateException("No animator found for name: $animatorName")
        val modelName = assetResolver.getModel(aspects)
        val existingEntityModel = animators[animatorName to modelName]
        return if (existingEntityModel != null) {
            existingEntityModel
        } else {
            val entityModel = animatorSupplier(models[modelName]!!)
            animators[animatorName to modelName] = entityModel
            entityModel.registerPoses()
            entityModel
        }
    }

    fun getTexture(aspects: Set<String>): Identifier {
        return assetResolver.getTexture(aspects)
    }
}

/**
 * A resolver of species assets. This takes a set of assets supplied by the registered [AspectProvider]s and
 * is used to produce the most appropriate animator class, model, and texture by searching for the last matching
 * [ModelAssetVariation]. It will fall back to the top-level properties if none match the aspect conditions.
 *
 * @author Hiroku
 * @since May 14th, 2022
 */
class SpeciesAssetResolver {
    val animator = ""
    val model = Identifier("")
    val texture = Identifier("")

    val variations = mutableListOf<ModelAssetVariation>()

    fun getAnimator(aspects: Set<String>): String {
        return variations.lastOrNull { it.aspects.all { it in aspects } && it.animator != null }?.animator ?: animator
    }

    fun getModel(aspects: Set<String>): Identifier {
        return variations.lastOrNull { it.aspects.all { it in aspects } && it.model != null }?.model ?: model
    }

    fun getTexture(aspects: Set<String>): Identifier {
        return variations.lastOrNull { it.aspects.all { it in aspects } && it.texture != null }?.texture ?: texture
    }

    fun getAllModels(): Set<Identifier> {
        val models = mutableSetOf<Identifier>()
        models.add(model)
        for (variation in variations) {
            if (variation.model != null) {
                models.add(variation.model)
            }
        }
        return models
    }

    companion object {
        val GSON = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            .disableHtmlEscaping()
            .setLenient()
            .create()
        fun load(path: String): SpeciesAssetResolver {
            return GSON.fromJson(PokemonCobbled::class.java.getResourceAsStream("/assets/pokemoncobbled/$path")!!.reader(), SpeciesAssetResolver::class.java)
        }
    }
}

/**
 * A variation to the base species, which can overwrite the animator, model, texture, or any combination.
 * It contains a set of aspects that must ALL be present on a renderable for this variation to be considered.
 * If a later variation also matches, but provides different properties, both this and the other variation will
 * be used for their respective non-null properties.
 *
 * @author Hiroku
 * @since May 14th, 2022
 */
class ModelAssetVariation {
    val aspects = mutableSetOf<String>()
    val animator: String? = null
    val model: Identifier? = null
    val texture: Identifier? = null
}

/**
 * {
 *   animator: "CharizardModel",
 *   model: "charizard.json"
 *   texture: "charizard-base.json",
 *   variations: [
 *      {
 *        aspects: ["shiny"],
 *        texture: "charizard-shiny.json"
 *      }
 *   ]
 * }
 */