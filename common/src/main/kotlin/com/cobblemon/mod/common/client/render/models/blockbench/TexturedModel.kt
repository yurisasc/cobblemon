/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.util.adapters.LocatorBoneAdapter
import com.cobblemon.mod.common.util.math.geometry.getOrigin
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.jozufozu.flywheel.core.hardcoded.PartBuilder
import com.jozufozu.flywheel.core.model.Model
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelPartData
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasHolder
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import org.joml.Vector3f

class TexturedModel {
    @SerializedName("format_version")
    val formatVersion: String = "0"
    @SerializedName("minecraft:geometry")
    val geometry: List<ModelGeometry>? = null

    fun create(isForLivingEntityRenderer: Boolean) : TexturedModelData {
        return createWithUvOverride(isForLivingEntityRenderer, 0, 0, null, null)
    }

    fun buildFromPartData(stack: MatrixStack, sprite: Sprite, builder: PartBuilder, data: ModelPartData) {
        val origin = stack.peek().positionMatrix.getOrigin().toVector3f()
        stack.translate(data.rotationData.pivotX, data.rotationData.pivotY, data.rotationData.pivotZ)
        stack.multiply(Quaternionf().rotationZYX(data.rotationData.roll, data.rotationData.yaw, data.rotationData.pitch))
        for (cube in data.cuboidData) {
            val start = Vector3f(origin.x + cube.offset.x, origin.y + cube.offset.y, origin.z + cube.offset.z)
            val rotationXYZ = stack.peek().positionMatrix.getEulerAnglesXYZ(Vector3f(0F, 0F, 0F))
            builder.cuboid()
                .start(start.x, start.y, start.z)
                .size(cube.dimensions.x, cube.dimensions.y, cube.dimensions.z)
                .sprite(sprite)
                .textureOffset(cube.textureUV.x.toInt(), cube.textureUV.y.toInt())
                .rotate(rotationXYZ.x, rotationXYZ.y, rotationXYZ.z)
                .endCuboid()
        }

        for (child in data.children.values) {
            stack.push()
            buildFromPartData(stack, sprite, builder, child)
            stack.pop()
        }
    }

    fun createFlywheelModel(atlas: SpriteAtlasHolder, textureName: Identifier, name: String): Model {
        val texture = atlas.getSprite(textureName)
        val width = ((texture.maxU * atlas.atlas.width.toFloat()) - (texture.minU * atlas.atlas.width)).toInt()
        val height =( (texture.maxV * atlas.atlas.height.toFloat()) - (texture.minV * atlas.atlas.height)).toInt()
        val newBuilder = PartBuilder(name, width, height)
        newBuilder.sprite(texture)
        val data = create(false).data.root
        val matrix = MatrixStack()
        matrix.scale(1F, -1F, -1F)
        buildFromPartData(matrix, texture, newBuilder, data)

        return newBuilder.build()
//
//
//
//        val modelBuilder = BetterPartBuilder(name, width, height)
//
//        modelBuilder.sprite(texture)
//        geometry?.forEach {
//            it.bones?.forEach { bone ->
//                var cuboidBuilder = modelBuilder.cuboid()
//                cuboidBuilder.sprite(texture)
//                bone.cubes?.forEach{cube ->
//
//                    if (cube.origin != null) {
//                        cuboidBuilder.start(cube.origin[0], cube.origin[1], cube.origin[2])
//                    }
//                    if (cube.size != null) {
//                        cuboidBuilder.size(cube.size[0], cube.size[1], cube.size[2])
//                    }
//                    if (cube.rotation != null) {
//                        cuboidBuilder.rotate(cube.rotation[0], cube.rotation[1], cube.rotation[2])
//                    }
//                    if (cube.pivot != null) {
//                        cuboidBuilder.pivot(cube.pivot[0], cube.pivot[1], cube.pivot[2])
//                    }
//                    if (cube.uv != null) {
//                        cuboidBuilder.textureOffset(cube.uv[0], cube.uv[1])
//                    }
//                    //cuboidBuilder.invertYZ()
//                    cuboidBuilder.endCuboid()
//                    cuboidBuilder = modelBuilder.cuboid()
//                }
//            }
//        }
//        return modelBuilder.build()
    }

    fun createWithUvOverride(isForLivingEntityRenderer: Boolean, u: Int, v: Int, textureWidth: Int?, textureHeight: Int?) : TexturedModelData {
        val modelData = ModelData()
        val parts = HashMap<String, ModelPartData>()
        val bones = HashMap<String, ModelBone>()

        try {
            val geometry = this.geometry!![0]
            val geometryBones = geometry.bones!!.toMutableList()
            var parentPart: ModelPartData

            // We want all the regular bones, but then we want locators to be mapped into being empty bones.
            // Reasoning there is that in many ways they really do function the same. Creating a bespoke locator
            // thing in ModelPart and holding onto it through runtime would require 500 mixins and 1000 virgin
            // sacrifices, so no thanks. This actually works startlingly well and allows us to follow DRY.
            // - Hiro
            geometryBones += geometryBones.mapNotNull { bone ->
                val locators = bone.locators ?: return@mapNotNull null
                locators.map { (name, locator) ->
                    val locatorBone = ModelBone()
                    locatorBone.name = LocatorAccess.PREFIX + name
                    locatorBone.parent = bone.name
                    locatorBone.pivot = locator.offset
                    locatorBone.rotation = locator.rotation
                    return@map locatorBone
                }
            }.flatten() + ModelBone().apply {
                this.name = LocatorAccess.PREFIX + "root"
            }

            for (bone in geometryBones) {
                bones[bone.name] = bone
                parentPart = if (bone.parent != null) parts[bone.parent]!! else modelData.root

                val boneRotation = bone.rotation
                val modelTransform : ModelTransform
                when {
                    bone.parent == null -> {
                        // The root part always has a 24 Y offset. One of life's great mysteries.
                        modelTransform = ModelTransform.pivot(0F, if (isForLivingEntityRenderer) 24F else 0F, 0F)
                    }
                    boneRotation != null -> {
                        modelTransform = ModelTransform.of(
                            -(bones[bone.parent]!!.pivot[0] - bone.pivot[0]),
                            bones[bone.parent]!!.pivot[1] - bone.pivot[1],
                            -(bones[bone.parent]!!.pivot[2] - bone.pivot[2]),
                            Math.toRadians(boneRotation[0].toDouble()).toFloat(),
                            Math.toRadians(boneRotation[1].toDouble()).toFloat(),
                            Math.toRadians(boneRotation[2].toDouble()).toFloat()
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

                val boneCubes = bone.cubes
                if (boneCubes != null) {
                    var pivot: List<Float>
                    var subPart: ModelPartBuilder

                    for (cube in boneCubes) {
                        subPart = if (cube.rotation != null) ModelPartBuilder.create() else modelPart
                        pivot = cube.pivot ?: bone.pivot

                        if (cube.uv != null) {
                            subPart.uv(
                                cube.uv[0] + u,
                                cube.uv[1] + v
                            )
                        }
                        if (cube.mirror != null && cube.mirror == true) {
                            subPart.mirrored()
                        }
                        if (cube.size != null && cube.origin != null) {
                            subPart.cuboid(
                                cube.origin[0] - pivot[0],
                                // Y is inverted in Java Edition, but that also means counting from the other side of the cube.
                                -(cube.origin[1] - pivot[1] + cube.size[1]),
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

                parts[bone.name] = parentPart.addChild(
                    bone.name,
                    modelPart,
                    modelTransform
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

            return TexturedModelData.of(
                modelData,
                textureWidth ?: geometry.description.textureWidth,
                textureHeight ?: geometry.description.textureHeight
            )
        } catch (e: Exception) {
            if (geometry != null) {
                throw IllegalArgumentException("Error creating TexturedModelData with identifier ${geometry[0].description.identifier}", e)
            } else {
                throw IllegalArgumentException("Error creating TexturedModelData", e)
            }
        }
    }

    companion object {
        val GSON = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(LocatorBone::class.java, LocatorBoneAdapter)
            .create()

        fun from(json: String) : TexturedModel {
            try {
                return GSON.fromJson(json, TexturedModel::class.java)
            } catch (exception: Exception) {
                throw IllegalStateException("Issue loading pokemon geo: $json", exception)
            }
        }
    }
}
class ModelGeometry {
    lateinit var description: ModelDataDescription
    val bones: List<ModelBone>? = null
}
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
)

class ModelBone {
    var name: String = ""
    var parent: String? = null
    var pivot: List<Float> = emptyList()
    var rotation: List<Float>? = null
    var cubes: List<Cube>? = null
    var locators: Map<String, LocatorBone>? = null
}

class Cube {
    val origin: List<Float>? = null
    val size: List<Float>? = null
    val pivot: List<Float>? = null
    val rotation: List<Float>? = null
    val uv: List<Int>? = null
    val inflate: Float? = null
    val mirror: Boolean = false
}

class LocatorBone(
    var offset: List<Float> = listOf(0F, 0F, 0F),
    var rotation: List<Float> = listOf(0F, 0F, 0F)
)