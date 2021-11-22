package com.cablemc.pokemoncobbled.client.render.models.smd.renderer

import com.cablemc.pokemoncobbled.client.render.models.smd.SmdModel
import com.cablemc.pokemoncobbled.client.render.models.smd.mesh.SmdMeshVertex
import com.cablemc.pokemoncobbled.common.util.math.geometry.GeometricPoint
import com.google.common.collect.ImmutableMap
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat.ELEMENT_NORMAL
import com.mojang.blaze3d.vertex.DefaultVertexFormat.ELEMENT_PADDING
import com.mojang.blaze3d.vertex.DefaultVertexFormat.ELEMENT_POSITION
import com.mojang.blaze3d.vertex.DefaultVertexFormat.ELEMENT_UV0
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft

/**
 * The renderer used to render `.smd` models.
 *
 * @author landonjw
 */
class SmdModelRenderer {

    private val meshFormat = VertexFormat(
        ImmutableMap.of(
            VertexFormatElement.Usage.POSITION.getName(), ELEMENT_POSITION,
            VertexFormatElement.Usage.UV.getName(), ELEMENT_UV0,
            VertexFormatElement.Usage.NORMAL.getName(), ELEMENT_NORMAL,
            VertexFormatElement.Usage.PADDING.getName(), ELEMENT_PADDING
        )
    )

    fun render(matrix: PoseStack, model: SmdModel) {
        // Get the next frame of current animation, if there is one
        // TODO: Remove this and make a separate animation handler
//        if (model.currentAnimation != null) model.currentAnimation?.animate()

        /* Apply transformations that apply to every vertex in the model
         *
         * Transformations of this fashion are done using the origin matrix rather
         * than our in-house TransformationMatrix in order to save on computation of
         * several matrix multiplication operations, resulting in higher frames,
         * and it also makes more sense to apply once than on every vertex. - landonjw
         */
        applyGlobalTransforms(matrix, model.renderProperties)

        // TODO: Check if this has performance impact
        // ANSWER: Almost fucking certainly, texture binding is expensive and more expensive than everything else
        Minecraft.getInstance().textureManager.bindForSetup(model.skeleton.mesh.texture)

        // Start drawing every vertex in the model's mesh
        val buffer = Tesselator.getInstance().builder
        RenderSystem.enableDepthTest()
        buffer.begin(VertexFormat.Mode.TRIANGLES, meshFormat)

        model.skeleton.mesh.vertices.forEach { vertex ->
            renderVertex(matrix, buffer, vertex)
        }
        Tesselator.getInstance().end()
    }

    private fun renderVertex(
        matrix: PoseStack,
        buffer: BufferBuilder,
        vertex: SmdMeshVertex
    ) {
        buffer
            .vertex(matrix.last().pose(), vertex.position.x, vertex.position.y, vertex.position.z)
            .uv(vertex.u, vertex.v)
            .normal(matrix.last().normal(), vertex.normal.x, vertex.normal.y, vertex.normal.z)
            .endVertex()
    }

    private fun applyGlobalTransforms(matrix: PoseStack, properties: List<SmdRenderProperty<*>>) {
        val globalTranslation = getProperty<PositionOffset>(properties)?.value
        if (globalTranslation != null) applyGlobalTranslation(matrix, globalTranslation)

        val globalRotation = getProperty<RotationOffset>(properties)?.value
        if (globalRotation != null) applyGlobalRotation(matrix, globalRotation)

        val globalScalars = getProperty<Scale>(properties)?.value
        if (globalScalars != null) applyGlobalScale(matrix, globalScalars)
    }

    private fun applyGlobalTranslation(matrix: PoseStack, translation: GeometricPoint) {
        matrix.translate(translation.x.toDouble(), translation.y.toDouble(), translation.z.toDouble())
    }

    private fun applyGlobalRotation(matrix: PoseStack, rotation: Vector3f) {
        matrix.mulPose(Quaternion(Vector3f.ZP, rotation.z(), false))
        matrix.mulPose(Quaternion(Vector3f.YP, rotation.y(), false))
        matrix.mulPose(Quaternion(Vector3f.XP, rotation.x(), false))
    }

    private fun applyGlobalScale(matrix: PoseStack, scalars: Vector3f) {
        matrix.scale(scalars.x(), scalars.y(), scalars.z())
    }

    private inline fun <reified T : SmdRenderProperty<*>> getProperty(properties: List<SmdRenderProperty<*>>): T? {
        return properties.firstOrNull { it is T } as? T
    }

}