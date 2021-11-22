package com.cablemc.pokemoncobbled.client.render.models.smd.loaders

import com.cablemc.pokemoncobbled.client.render.models.smd.SmdModel
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.SmdModelFileLoader
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.schemas.SmdBoneLocationSchema
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.schemas.SmdBoneSchema
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.schemas.SmdMeshVertexSchema
import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.schemas.SmdModelSchema
import com.cablemc.pokemoncobbled.client.render.models.smd.mesh.SmdMesh
import com.cablemc.pokemoncobbled.client.render.models.smd.mesh.SmdMeshVertex
import com.cablemc.pokemoncobbled.client.render.models.smd.skeleton.SmdModelBone
import com.cablemc.pokemoncobbled.client.render.models.smd.skeleton.SmdModelSkeleton
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.resources.ResourceLocation

/**
 * Loads a [SmdModel] from a `.smd` file.
 * This loading is done synchronously by default, and should often be handled otherwise
 * in any implementations that use it.
 *
 * @author landonjw
 */
class SmdModelLoader(
    private val smdModelFileLoader: SmdModelFileLoader
) {

    /**
     * Loads a [SmdModel] from a `.smd` file.
     * This simply loads the model, it will not add any animations and will be t-posed.
     *
     * @param location the location to load `.smd` file from
     * @return an smd model from the location
     */
    fun load(location: ResourceLocation): SmdModel {
        val schema = smdModelFileLoader.load(location)

        val texture = getDefaultTexture(location)
        val vertices = getMeshVertices(schema)
        val mesh = SmdMesh(vertices, texture)

        val skeleton = getSkeleton(schema, mesh)
        return SmdModel(skeleton)
    }

    private fun getMeshVertices(schema: SmdModelSchema): List<SmdMeshVertex> {
        val meshVertices = mutableListOf<SmdMeshVertex>()
        schema.polygonMesh.forEach {
            meshVertices.add(getMeshVertexFromSchema(it.vertex1))
            meshVertices.add(getMeshVertexFromSchema(it.vertex2))
            meshVertices.add(getMeshVertexFromSchema(it.vertex3))
        }
        return meshVertices
    }

    private fun getDefaultTexture(location: ResourceLocation): ResourceLocation {
        val split = location.path.split("/")
        val parentPath = split.subList(0, split.size - 1)
            .reduce { acc, s -> "$acc/$s" }
        val fileName = split.last().replace(".smd", "")
        return ResourceLocation(PokemonCobbled.MODID, "$parentPath/textures/$fileName.png")
    }

    private fun getSkeleton(schema: SmdModelSchema, mesh: SmdMesh): SmdModelSkeleton {
        val boneLocationDefById = mutableMapOf<Int, SmdBoneLocationSchema>()
        schema.boneLocations.forEach { boneLocationDefById[it.boneId] = it }

        val modelBones = mutableListOf<SmdModelBone>()
        for (boneDef in schema.bones) {
            val boneLocDef = boneLocationDefById[boneDef.id] ?: continue
            modelBones.add(getBoneFromSchema(boneDef, boneLocDef))
        }
        linkBones(schema.bones, modelBones)
        return SmdModelSkeleton(modelBones, mesh)
    }

    private fun getBoneFromSchema(
        boneSchema: SmdBoneSchema,
        locationSchema: SmdBoneLocationSchema
    ): SmdModelBone {
        return SmdModelBone(boneSchema.id, boneSchema.name, locationSchema.location, locationSchema.orientation)
    }

    private fun linkBones(boneSchemas: List<SmdBoneSchema>, bones: List<SmdModelBone>) {
        val boneById = mutableMapOf<Int, SmdModelBone>()
        bones.forEach { boneById[it.id] = it }
        for (boneDef in boneSchemas) {
            val bone = boneById[boneDef.id] ?: continue
            val parent = boneById[boneDef.parent] ?: continue
            bone.parent = parent
        }
    }

    private fun getMeshVertexFromSchema(vertex: SmdMeshVertexSchema): SmdMeshVertex {
        return SmdMeshVertex(
            weights = vertex.links ?: mapOf(),
            basePosition = vertex.position,
            baseNormal = vertex.normal,
            u = vertex.uvMap.a,
            v = vertex.uvMap.b
        )
    }

}