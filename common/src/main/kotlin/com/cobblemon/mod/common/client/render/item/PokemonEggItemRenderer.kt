package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.util.DataKeys
import com.ibm.icu.impl.duration.impl.DataRecord.EGender.F
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.BasicBakedModel
import net.minecraft.client.render.model.ModelRotation
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GREMEDYStringMarker
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.awt.Color
import java.nio.ByteBuffer
import java.nio.IntBuffer


class PokemonEggItemRenderer : CobblemonBuiltinItemRenderer {
    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val egg = Egg.fromNbt(stack.nbt?.get(DataKeys.EGG) as? NbtCompound ?: return)
        if (mode == ModelTransformationMode.GUI) {
            renderGui(egg, stack, matrices, vertexConsumers, light, overlay)
        }
    }

    //We also need to optimize this a little bit, kinda bad for perf
    //Need to use VBOs like we do for BERs, but what do we attach them to?
    fun renderGui(
        egg: Egg,
        stack: ItemStack,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        //Since we are delegating back to the item renderer, we need the matrix frame from BEFORE the item renderer first ran
        matrices.pop()
        val pattern = egg.getPattern() ?: return
        val model = getBakedModel(pattern.baseInvSpritePath, pattern.overlayInvSpritePath)
        matrices.push()
        //GREMEDYStringMarker.glStringMarkerGREMEDY("Rendering egg item")
        MinecraftClient.getInstance().itemRenderer.renderItem(stack, ModelTransformationMode.GUI, false, matrices, vertexConsumers, light, overlay, model)
        matrices.pop()
        //Since the item renderer pops the frame off the we popped earlier, we need to put a frame back
        matrices.push()
    }

    fun getBakedModel(baseTexId: Identifier, overlayTexId: Identifier?): BakedModel {
        val atlas = MinecraftClient.getInstance().getSpriteAtlas(Identifier.tryParse("minecraft:textures/atlas/blocks.png"))
        val baseSprite = atlas.apply(Identifier.of(baseTexId.namespace, "item/${baseTexId.path}"))
        val baseFrontQuad = generateBakedQuad(
            Vector3f(0F, 1F, 0.4675F),
            Vector2f (baseSprite.minU, baseSprite.minV),
            Vector3f(0F, 0F, 0.4675F),
            Vector2f (baseSprite.minU, baseSprite.maxV),
            Vector3f(1F, 0F, 0.4675F),
            Vector2f (baseSprite.maxU, baseSprite.maxV),
            Vector3f(1F, 1F, 0.4675F),
            Vector2f(baseSprite.maxU, baseSprite.minV),
            0,
            baseSprite,
            Direction.UP
        )
        var overlayFrontQuad: BakedQuad? = null
        overlayTexId?.let {
            val overlaySprite = atlas.apply(Identifier.of(baseTexId.namespace, "item/${overlayTexId.path}"))
            overlayFrontQuad = generateBakedQuad(
                Vector3f(0F, 1F, 0.4675F),
                Vector2f (overlaySprite.minU, overlaySprite.minV),
                Vector3f(0F, 0F, 0.4675F),
                Vector2f (overlaySprite.minU, overlaySprite.maxV),
                Vector3f(1F, 0F, 0.4675F),
                Vector2f (overlaySprite.maxU, overlaySprite.maxV),
                Vector3f(1F, 1F, 0.4675F),
                Vector2f(overlaySprite.maxU, overlaySprite.minV),
                1,
                baseSprite,
                Direction.UP
            )
        }
        val faceQuadMaps = Direction.values().associateWith {
            emptyList<BakedQuad>()
        }
        return BasicBakedModel(
            listOfNotNull(baseFrontQuad, overlayFrontQuad),
            faceQuadMaps,
            false,
            false,
            false,
            //This is the particle sprite, so i assume unused
            baseSprite,
            ModelTransformation.NONE,
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

    //Takes one vertex and packs it into buf so we can make baked quads
    fun packVertexData(position: Vector3f, uvs: Vector2f, buf: ByteBuffer) {
        buf.putFloat(position.x)
        buf.putFloat(position.y)
        buf.putFloat(position.z)
        //Idk why mc puts this in, but it does, so dont touch it
        buf.putInt(-1)
        buf.putFloat(uvs.x)
        buf.putFloat(uvs.y)
        buf.position(buf.position() + 8)
    }
}