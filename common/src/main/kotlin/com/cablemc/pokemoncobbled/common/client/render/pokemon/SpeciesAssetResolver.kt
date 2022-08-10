package com.cablemc.pokemoncobbled.common.client.render.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.TexturedModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.util.adapters.IdentifierAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.Vec3fAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.Vector4fAdapter
import com.google.gson.GsonBuilder
import net.minecraft.client.model.ModelPart
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f

/**
 * All the information required for rendering a Pokémon [Species] with aspects.
 *
 * @author Hiroku
 * @since May 14th, 2022
 */
class RegisteredSpeciesRendering(
    val species: Identifier,
    private val assetResolver: SpeciesAssetResolver
) {
    val posers = mutableMapOf<Pair<Identifier, Identifier>, PokemonPoseableModel>()
    val models = mutableMapOf<Identifier, ModelPart>()

    init {
        posers.clear()
        assetResolver.getAllModels().forEach { identifier ->
            models[identifier] = TexturedModel.from(identifier.path).create().createModel()
        }
    }

    fun getPoser(aspects: Set<String>): PokemonPoseableModel {
        val poserName = assetResolver.getPoser(aspects)
        val poserSupplier = PokemonModelRepository.posers[poserName] ?: throw IllegalStateException("No poser found for name: $poserName")
        val modelName = assetResolver.getModel(aspects)
        val existingEntityModel = posers[poserName to modelName]
        return if (existingEntityModel != null) {
            existingEntityModel
        } else {
            val entityModel = poserSupplier(models[modelName]!!)
            entityModel.registerPoses()
            posers[poserName to modelName] = entityModel
            entityModel
        }
    }

    fun getTexture(aspects: Set<String>): Identifier {
        PokemonModelRepository.posers[assetResolver.getPoser(aspects)] ?: throw IllegalStateException("No poser for $species")
        return assetResolver.getTexture(aspects)
    }

    fun getLayers(aspects: Set<String>): List<ModelLayer> {
        PokemonModelRepository.posers[assetResolver.getPoser(aspects)] ?: throw IllegalStateException("No poser for $species")
        return assetResolver.getLayers(aspects)
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
    val poser = Identifier("")
    val model = Identifier("")
    val texture = Identifier("")
    val layers: List<ModelLayer>? = null
    val variations = mutableListOf<ModelAssetVariation>()

    fun getPoser(aspects: Set<String>): Identifier {
        return variations.lastOrNull { it.aspects.all { it in aspects } && it.poser != null }?.poser ?: poser
    }

    fun getModel(aspects: Set<String>): Identifier {
        return variations.lastOrNull { it.aspects.all { it in aspects } && it.model != null }?.model ?: model
    }

    fun getTexture(aspects: Set<String>): Identifier {
        return variations.lastOrNull { it.aspects.all { it in aspects } && it.texture != null }?.texture ?: texture
    }

    fun getLayers(aspects: Set<String>): List<ModelLayer> {
        val allLayers = mutableListOf<ModelLayer>()
        layers?.let(allLayers::addAll)

        val variationLayers = variations.lastOrNull { it.aspects.all { it in aspects } && it.layers != null }?.layers ?: emptyList()
        variationLayers.forEach { layer ->
            allLayers.removeIf { it.name == layer.name }
            if (layer.texture != null) {
                allLayers.add(layer)
            }
        }

        return allLayers
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
            .registerTypeAdapter(Vec3f::class.java, Vec3fAdapter)
            .registerTypeAdapter(Vector4f::class.java, Vector4fAdapter)
            .disableHtmlEscaping()
            .setLenient()
            .create()
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
    val poser: Identifier? = null
    val model: Identifier? = null
    val texture: Identifier? = null
    val layers: List<ModelLayer>? = null
}

class ModelLayer {
    val name: String = ""
    val scale: Vec3f = Vec3f(1F, 1F, 1F)
    val tint: Vector4f = Vector4f(1F, 1F, 1F, 1F)
    val texture: Identifier? = null
}

/*
{
  "poser": "pokemoncobbled:squirtle",
  "model": "pokemoncobbled:bedrock/models/squirtle.geo.json",
  "texture": "pokemoncobbled:textures/pokemon/squirtle.png",
  "variations": [
    {
      "aspects": ["shiny"],
      "texture": "pokemoncobbled:textures/pokemon/squirtle_shiny.png"
    },
    {
      "aspects": ["sunglasses"],
      "layers": [
        {
          "name": "sunnies",
          "texture": "pokemoncobbled:textures/pokemon/squirtle_sunglasses.png",
          "scale": [1.05, 1, 1.05],
          "tint": [1, 0.1, 0.1, 0.6]
        }
      ]
    }
  ]
}
 */