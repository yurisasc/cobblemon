package com.cablemc.pokemoncobbled.common.client.render.models.blockbench

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import net.minecraft.client.model.*
import java.io.InputStreamReader

class TexturedModel(
    @SerializedName("format_version")
    val formatVersion: String,
    @SerializedName("minecraft:geometry")
    private val geometry: List<ModelGeometry>
) {

    fun getGeometry() : ModelGeometry? {
        return if (geometry != null && geometry.isNotEmpty()) geometry[0] else null
    }

    fun create() : TexturedModelData? {
        val modelData = ModelData()
        val parts = HashMap<String, ModelPartData>()
        val bones = HashMap<String, ModelBone>()

        try {
            if (getGeometry() != null && getGeometry()!!.bones != null) {
                var parentPart: ModelPartData

                for (bone in getGeometry()!!.bones!!) {
                    bones[bone.name] = bone
                    parentPart = if (bone.parent != null) parts[bone.parent]!! else modelData.root

                    val modelTransform : ModelTransform
                    when {
                        bone.parent == null -> {
                            modelTransform = ModelTransform.NONE
                        }
                        bone.rotation != null -> {
                            modelTransform = ModelTransform.of(
                                -(bones[bone.parent]!!.pivot[0] - bone.pivot[0]),
                                bones[bone.parent]!!.pivot[1] - bone.pivot[1],
                                -(bones[bone.parent]!!.pivot[2] - bone.pivot[2]),
                                Math.toRadians(bone.rotation[0].toDouble()).toFloat(),
                                Math.toRadians(bone.rotation[1].toDouble()).toFloat(),
                                Math.toRadians(bone.rotation[2].toDouble()).toFloat()
                            )
                        }
                        else -> {
                            modelTransform = ModelTransform.pivot(
                                -(bones[bone.parent]!!.pivot[0] - bone.pivot[0]),
                                bones[bone.parent]!!.pivot[1] - bone.pivot[1],
                                -(bones[bone.parent]!!.pivot[2] - bone.pivot[2])
                            )
                        }
                    }

                    val modelPart = ModelPartBuilder.create()
                    val subParts = mutableListOf<ModelPartBuilder>()
                    val modelTransforms = mutableListOf<ModelTransform>()

                    if (bone.cubes != null) {
                        var pivot: List<Float>
                        var subPart: ModelPartBuilder

                        for (cube in bone.cubes) {
                            subPart = if (cube.rotation != null) ModelPartBuilder.create() else modelPart
                            pivot = cube.pivot ?: bone.pivot

                            if (cube.uv != null) {
                                subPart.uv(
                                    cube.uv[0],
                                    cube.uv[1]
                                )
                            }
                            if (cube.mirror != null && cube.mirror == true) {
                                subPart.mirrored()
                            }
                            if (cube.size != null && cube.origin != null) {
                                subPart.cuboid(
                                    cube.origin[0] - pivot[0],
                                    pivot[1] - cube.origin[1], // This is the problem
                                    cube.origin[2] - pivot[2],
                                    cube.size[0],
                                    cube.size[1],
                                    cube.size[2],
                                    Dilation(cube.inflate ?: 0f)
                                )
                            }
                            if (cube.mirror != null && cube.mirror == true) {
                                subPart.mirrored(false)
                            }

                            if (subPart != modelPart) {
                                modelTransforms.add(ModelTransform.of(
                                    -(bone.pivot[0] - cube.pivot!![0]),
                                    bone.pivot[1] - cube.pivot[1],
                                    -(bone.pivot[2] - cube.pivot[2]),
                                    Math.toRadians(cube.rotation!![0].toDouble()).toFloat(),
                                    Math.toRadians(cube.rotation[1].toDouble()).toFloat(),
                                    Math.toRadians(cube.rotation[2].toDouble()).toFloat()
                                ))
                                subParts.add(subPart)
                            }
                        }
                    }

                    parts.put(
                        bone.name,
                        parentPart.addChild(
                            bone.name,
                            modelPart,
                            modelTransform
                        )
                    )

                    var counter = 0
                    subParts.forEachIndexed { index, part ->
                        parts[bone.name]!!.addChild(
                            bone.name + counter++.toString(),
                            part,
                            modelTransforms[index]
                        )
                    }
                }

                return TexturedModelData.of(modelData, getGeometry()!!.description.textureWidth, getGeometry()!!.description.textureHeight)
            }
        } catch (e: Exception) {
            if (getGeometry() != null) {
                PokemonCobbled.LOGGER.warn("Error creating TexturedModelData with identifier ${getGeometry()!!.description.identifier}.")
            }
            else {
                PokemonCobbled.LOGGER.warn("Error creating TexturedModelData.")
            }
            e.printStackTrace()
        }
        return null
    }

    companion object {
        val GSON = GsonBuilder()
            .setLenient()
            .create()

        fun from(pokemon: String) : TexturedModel? {
            val stream = PokemonCobbled.javaClass.getResourceAsStream("/assets/pokemoncobbled/geo/models/$pokemon.geo.json") ?: run {
                PokemonCobbled.LOGGER.error("There was no geo model found for $pokemon.")
                return null
            }
            var model: TexturedModel? = null
            try {
                model = GSON.fromJson(InputStreamReader(stream), TexturedModel::class.java)
            } catch (exception: Exception) {
                PokemonCobbled.LOGGER.error("Issue loading pokemon geo: $pokemon")
                exception.printStackTrace()
            }
            return model
        }
    }
}

class ModelGeometry(
    val description: ModelDataDescription,
    val bones: List<ModelBone>?
) { }

class ModelDataDescription(
    val identifier: String,
    @SerializedName("texture_width")
    val textureWidth: Int,
    @SerializedName("texture_height")
    val textureHeight: Int,
    @SerializedName("visible_bounds_width")
    val visibleBoundsWidth: Float,
    @SerializedName("visible_bounds_height")
    val visibleBoundsHeight: Float,
    @SerializedName("visible_bounds_offset")
    val visibleBoundsOffset: List<Float>
) { }

class ModelBone(
    val name: String,
    val parent: String?,
    val pivot: List<Float>,
    val rotation: List<Float>?,
    val cubes: List<Cube>?
) { }

class Cube(
    val origin: List<Float>?,
    val size: List<Float>?,
    val pivot: List<Float>?,
    val rotation: List<Float>?,
    val uv: List<Int>?,
    val inflate: Float?,
    val mirror: Boolean?
) { }