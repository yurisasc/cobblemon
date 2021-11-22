package com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.schemas

import com.cablemc.pokemoncobbled.common.util.math.geometry.GeometricPoint
import com.mojang.math.Vector3f
import net.minecraft.resources.ResourceLocation

data class PQCSchema(
    val animations: List<PQCAnimationSchema> = listOf(),
    val modelPath: ResourceLocation,
    val scale: Vector3f?,
    val rotationOffset: Vector3f?,
    val positionOffset: GeometricPoint?
)

data class PQCAnimationSchema(
    val name: String,
    val path: ResourceLocation
)