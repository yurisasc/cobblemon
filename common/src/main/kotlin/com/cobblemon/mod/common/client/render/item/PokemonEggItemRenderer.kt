/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.BasicBakedModel
import net.minecraft.client.render.model.CubeFace
import net.minecraft.client.render.model.json.ItemModelGenerator
import net.minecraft.client.render.model.json.ModelElement
import net.minecraft.client.render.model.json.ModelElementFace
import net.minecraft.client.render.model.json.ModelElementTexture
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

// Abandon all hope, ye who enter here
// I'm not joking this is so bad. Most of it is copied from MC classes so don't blame me :(
// "Why don't you just define models for every pattern?" - ModelOverrides aren't complex enough to do what we wanna do here, since we only want one egg item
/**
 * Generates and renders models for eggs in the inventory
 *
 * @author Apion
 * @since February 12, 2024
 **/
class PokemonEggItemRenderer : CobblemonBuiltinItemRenderer {
    //DONT FORGET TO CACHE MODELS AFTER THEY ARE GENERATED AT SOME POINT
    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val egg = Egg.fromNbt(stack.nbt?.get(DataKeys.EGG) as? NbtCompound ?: return)
        //Since we are delegating back to the item renderer, we need the matrix frame from BEFORE the item renderer first ran
        matrices.pop()
        val pattern = egg.getPattern() ?: return
        val model = getBakedModel(pattern.baseInvSpritePath, pattern.overlayInvSpritePath)
        matrices.push()
        //GREMEDYStringMarker.glStringMarkerGREMEDY("Rendering egg item")
        val isLeftHanded = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
        MinecraftClient.getInstance().itemRenderer.renderItem(stack, mode, isLeftHanded, matrices, vertexConsumers, light, overlay, model)
        matrices.pop()
        //Since the item renderer pops the frame off the we popped earlier, we need to put a frame back
        matrices.push()
    }

    fun getBakedModel(baseTexId: Identifier, overlayTexId: Identifier?): BakedModel {
        val atlas = MinecraftClient.getInstance().getSpriteAtlas(Identifier.tryParse("minecraft:textures/atlas/blocks.png"))
        val baseSprite = atlas.apply(Identifier.of(baseTexId.namespace, "item/${baseTexId.path}"))
        val uvArrOne = FloatArray(4)
        uvArrOne[0] = 0F
        uvArrOne[1] = 0F
        uvArrOne[2] = 16F
        uvArrOne[3] = 16F
        val uvArrTwo = FloatArray(4)
        uvArrTwo[0] = 16F
        uvArrTwo[1] = 0F
        uvArrTwo[2] = 0F
        uvArrTwo[3] = 16F
        //North and south faces are done separately for some reason
        val baseElementList = mutableListOf(
            ModelElement(
                Vector3f(0.0F, 0.0F, 7.5F),
                Vector3f(16.0F, 16.0F, 8.5F),
                buildMap {
                    this[Direction.SOUTH] = ModelElementFace(null, 0, "item/${baseTexId.path}", ModelElementTexture(uvArrOne, 0))
                    this[Direction.NORTH] = ModelElementFace(null, 0, "item/${baseTexId.path}", ModelElementTexture(uvArrTwo, 0))
                },
                null,
                false
            )
        )
        //This generates all of the other faces, but some more work is needed to bake them
        baseElementList.addAll(MODEL_GENERATOR.addSubComponents(baseSprite.contents, "layer0", 0))
        var overlayElementList: MutableList<ModelElement>? = null
        var overlaySprite: Sprite? = null
        overlayTexId?.let {
            overlaySprite = atlas.apply(Identifier.of(baseTexId.namespace, "item/${overlayTexId.path}"))
            if (overlaySprite == null) return@let
            overlayElementList = mutableListOf(
                ModelElement(
                    Vector3f(0.0F, 0.0F, 7.5F),
                    Vector3f(16.0F, 16.0F, 8.5F),
                    buildMap {
                        this[Direction.SOUTH] = ModelElementFace(null, 1, "item/${overlayTexId.path}", ModelElementTexture(uvArrOne, 0))
                        this[Direction.NORTH] = ModelElementFace(null, 1, "item/${overlayTexId.path}", ModelElementTexture(uvArrTwo, 0))
                    },
                    null,
                    true
                )
            )
            overlayElementList!!.addAll(MODEL_GENERATOR.addSubComponents(overlaySprite!!.contents, "layer1", 1))
        }
        //I do not know how these cull faces work
        val faceQuadMaps = Direction.values().associateWith {
            emptyList<BakedQuad>()
        }
        val quads = generateQuads(baseElementList, 0, baseSprite)
        if (overlayElementList != null) {
            quads.addAll(generateQuads(overlayElementList!!, 1, overlaySprite!!))
        }
        return BasicBakedModel(
            quads,
            faceQuadMaps,
            false,
            false,
            false,
            //This is the particle sprite, so i assume unused in an item context
            baseSprite,
            DEFAULT_MODEL_TRANSFORMS,
            ModelOverrideList.EMPTY
        )
    }

    fun generateBakedQuad(
        cornerOnePos: Vector3f,
        cornerOneUv: Vector2f,
        cornerTwoPos: Vector3f,
        cornerTwoUv: Vector2f,
        cornerThreePos: Vector3f,
        cornerThreeUv: Vector2f,
        cornerFourPos: Vector3f,
        cornerFourUv: Vector2f,
        colorIndex: Int,
        sprite: Sprite,
        face: Direction
    ): BakedQuad {
        val vertBuf = MemoryStack.stackPush()
        vertBuf.use {
            //4 verts per quad * 4 bytes per float * 8 floats per vert (only 6 are put in but MC expects 8 ints/floats per vert)
            val buf = it.malloc(4 * 4 * 8)
            packVertexData(cornerOnePos, cornerOneUv, buf)
            packVertexData(cornerTwoPos, cornerTwoUv, buf)
            packVertexData(cornerThreePos, cornerThreeUv, buf)
            packVertexData(cornerFourPos, cornerFourUv, buf)
            val vertData = IntArray(32)
            buf.rewind()
            var vertDataIndex = 0
            while (buf.hasRemaining()) {
                vertData[vertDataIndex] = buf.getInt()
                vertDataIndex++
            }
            return BakedQuad(vertData, colorIndex, face, sprite, true)
        }

    }

    //Mostly copied from BakedQuadFactory, with some consolidation
    //Creates BakedQuads from model elements, tintable using ColorProviders
    //This could be moved into its own class that deals with baking models someday
    //I would like to clean this up to not use all the cube face stuff, but that requires thought
    fun generateQuads(modelElements: List<ModelElement>, colorIndex: Int, sprite: Sprite): MutableList<BakedQuad> {
        val result = mutableListOf<BakedQuad>()
        modelElements.forEach { element ->
            element.faces.forEach { face ->
                val cornerArray = getPositionMatrix(element.from, element.to)
                //These are the index of the corner coordinate in cornerArray
                //All this cube face stuff is basically some abstractions to get the 4 quad coords
                //when we are only given 2 coords that are connected on a diagonal.
                //Could probably get rid of them if we want to do the maths
                //Numbers are drawing order
                val cornerOne = CubeFace.getFace(face.key).getCorner(0)
                val cornerTwo = CubeFace.getFace(face.key).getCorner(1)
                val cornerThree = CubeFace.getFace(face.key).getCorner(2)
                val cornerFour = CubeFace.getFace(face.key).getCorner(3)
                val uOne = sprite.getFrameU(face.value.textureData.getU(0).toDouble())
                val vOne = sprite.getFrameV(face.value.textureData.getV(0).toDouble())
                val uTwo = sprite.getFrameU(face.value.textureData.getU(1).toDouble())
                val vTwo = sprite.getFrameV(face.value.textureData.getV(1).toDouble())
                val uThree = sprite.getFrameU(face.value.textureData.getU(2).toDouble())
                val vThree = sprite.getFrameV(face.value.textureData.getV(2).toDouble())
                val uFour = sprite.getFrameU(face.value.textureData.getU(3).toDouble())
                val vFour = sprite.getFrameV(face.value.textureData.getV(3).toDouble())
                result.add(generateBakedQuad(
                    Vector3f(cornerArray[cornerOne.xSide], cornerArray[cornerOne.ySide], cornerArray[cornerOne.zSide]),
                    Vector2f(uOne, vOne),
                    Vector3f(cornerArray[cornerTwo.xSide], cornerArray[cornerTwo.ySide], cornerArray[cornerTwo.zSide]),
                    Vector2f(uTwo, vTwo),
                    Vector3f(cornerArray[cornerThree.xSide], cornerArray[cornerThree.ySide], cornerArray[cornerThree.zSide]),
                    Vector2f(uThree, vThree),
                    Vector3f(cornerArray[cornerFour.xSide], cornerArray[cornerFour.ySide], cornerArray[cornerFour.zSide]),
                    Vector2f(uFour, vFour),
                    colorIndex,
                    sprite,
                    face.key
                ))
            }
        }
        return result
    }

    //Takes a vertex and packs it into buf in the format expected by BakedQuads
    fun packVertexData(position: Vector3f, uvs: Vector2f, buf: ByteBuffer) {
        buf.putFloat(position.x)
        buf.putFloat(position.y)
        buf.putFloat(position.z)
        //Idk why mc puts this in, but it does, so dont touch it
        buf.putInt(-1)
        buf.putFloat(uvs.x)
        buf.putFloat(uvs.y)
        //Padding
        buf.position(buf.position() + 8)
    }

    companion object {
        val MODEL_GENERATOR = ItemModelGenerator()
        //These are the transformations applied depending on the ModelTransformationMode.
        //Obtained from minecraft/assets/model/item/generated.json, the parent model for all generated item models
        //The modVector function does the same transformations done in [Transformation.Deserializer] <- >:( (So dumb)
        val DEFAULT_MODEL_TRANSFORMS = ModelTransformation(
            Transformation(Vector3f(0F, 0F, 0F), modTranslationVector(Vector3f(0F, 3F, 1F)), modScaleVector(Vector3f(0.68F, 0.68F, 0.68F))),
            Transformation(Vector3f(0F, 0F, 0F), modTranslationVector(Vector3f(0F, 3F, 1F)), modScaleVector(Vector3f(0.68F, 0.68F, 0.68F))),
            Transformation(Vector3f(0F, -90F, 25F), modTranslationVector(Vector3f(1.13F, 3.2F, 1.13F)), modScaleVector(Vector3f(0.68F, 0.68F, 0.68F))),
            Transformation(Vector3f(0F, -90F, 25F), modTranslationVector(Vector3f(1.13F, 3.2F, 1.13F)), modScaleVector(Vector3f(0.68F, 0.68F, 0.68F))),
            Transformation(Vector3f(0F, 180F, 0F), modTranslationVector( Vector3f(0F, 13F, 7F)), modScaleVector(Vector3f(1F, 1F, 1F))),
            Transformation.IDENTITY,
            Transformation(Vector3f(0F, 0F, 0F), modTranslationVector(Vector3f(0F, 2F, 0F)), modScaleVector(Vector3f(0.5F, 0.5F, 0.5F))),
            Transformation(Vector3f(0F, 180F, 0F), modTranslationVector(Vector3f(0F, 0F, 0F)), modScaleVector(Vector3f(1F, 1F, 1F)))
        )

        //TODO: Turn these into VectorUtil methods
        //Random transformations that MC does (WHY???? I HAVE NO IDEA)
        //Mostly gotten from Transformation.Deserializer
        fun modTranslationVector(translationVec: Vector3f): Vector3f {
            translationVec.mul(0.0625F)
            translationVec.set(
                MathHelper.clamp(translationVec.x, -5.0f, 5.0f),
                MathHelper.clamp(translationVec.y, -5.0f, 5.0f),
                MathHelper.clamp(translationVec.z, -5.0f, 5.0f)
            )
            return translationVec
        }

        fun modScaleVector(scaleVec: Vector3f): Vector3f {
            scaleVec.set(
                MathHelper.clamp(scaleVec.x, -4.0f, 4.0f),
                MathHelper.clamp(scaleVec.y, -4.0f, 4.0f),
                MathHelper.clamp(scaleVec.z, -4.0f, 4.0f)
            )
            return scaleVec
        }

        //Copied from BakedQuadFactory
        private fun getPositionMatrix(from: Vector3f, to: Vector3f): FloatArray {
            val fs = FloatArray(Direction.values().size)
            fs[CubeFace.DirectionIds.WEST] = from.x() / 16.0f
            fs[CubeFace.DirectionIds.DOWN] = from.y() / 16.0f
            fs[CubeFace.DirectionIds.NORTH] = from.z() / 16.0f
            fs[CubeFace.DirectionIds.EAST] = to.x() / 16.0f
            fs[CubeFace.DirectionIds.UP] = to.y() / 16.0f
            fs[CubeFace.DirectionIds.SOUTH] = to.z() / 16.0f
            return fs
        }
    }
}