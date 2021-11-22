package com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.schemas

import com.cablemc.pokemoncobbled.common.util.math.geometry.GeometricPoint
import com.cablemc.pokemoncobbled.common.util.math.geometry.TransformationMatrix
import com.mojang.math.Vector3f

data class SmdAnimationSchema(
    val frames: List<SmdAnimationFrameSchema>
)

data class SmdAnimationFrameSchema(
    val frame: Int,
    val transformations: List<SmdBoneTransformationSchema>
)

data class SmdBoneTransformationSchema(
    val boneId: Int,
    val translation: GeometricPoint,
    val rotation: Vector3f
) {
    val transformation: TransformationMatrix by lazy {
        TransformationMatrix.of(translation, rotation)
    }
}