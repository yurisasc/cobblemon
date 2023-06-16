/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.snowstorm

import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.api.data.ArbitrarilyMappedSerializableCompanion
import com.cobblemon.mod.common.client.render.MatrixWrapper
import com.cobblemon.mod.common.util.math.hamiltonProduct
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.AxisAngle4d
import org.joml.Quaternionf
import org.joml.Vector3f

interface ParticleCameraMode : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<ParticleCameraMode, ParticleCameraModeType>(
        keyFromString = ParticleCameraModeType::valueOf,
        stringFromKey = { it.name },
        keyFromValue = { it.type }
    ) {
        init {
            registerSubtype(ParticleCameraModeType.ROTATE_XYZ, RotateXYZCameraMode::class.java, RotateXYZCameraMode.CODEC)
            registerSubtype(ParticleCameraModeType.ROTATE_Y, RotateYCameraMode::class.java, RotateYCameraMode.CODEC)
            registerSubtype(ParticleCameraModeType.LOOK_AT_XYZ, LookAtXYZ::class.java, LookAtXYZ.CODEC)
            registerSubtype(ParticleCameraModeType.LOOK_AT_Y, LookAtY::class.java, LookAtY.CODEC)
            registerSubtype(ParticleCameraModeType.DIRECTION_X, DirectionX::class.java, DirectionX.CODEC)
            registerSubtype(ParticleCameraModeType.DIRECTION_Y, DirectionY::class.java, DirectionY.CODEC)
            registerSubtype(ParticleCameraModeType.DIRECTION_Z, DirectionZ::class.java, DirectionZ.CODEC)
            registerSubtype(ParticleCameraModeType.LOOK_AT_DIRECTION, LookAtDirection::class.java, LookAtDirection.CODEC)
            registerSubtype(ParticleCameraModeType.EMITTER_XZ_PLANE, EmitterXZPlane::class.java, EmitterXZPlane.CODEC)
            registerSubtype(ParticleCameraModeType.EMITTER_XY_PLANE, EmitterXYPlane::class.java, EmitterXYPlane.CODEC)
            registerSubtype(ParticleCameraModeType.EMITTER_YZ_PLANE, EmitterYZPlane::class.java, EmitterYZPlane.CODEC)
        }
    }

    val type: ParticleCameraModeType
    fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf
}

class RotateXYZCameraMode : ParticleCameraMode {
    companion object {
        val CODEC: Codec<RotateXYZCameraMode> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { RotateXYZCameraMode() }
        }
    }

    override val type = ParticleCameraModeType.ROTATE_XYZ

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val i = if (angle == 0.0f) 0F else MathHelper.lerp(deltaTicks, prevAngle, angle)
        val q = Quaternionf(cameraAngle)
        q.hamiltonProduct(RotationAxis.POSITIVE_Z.rotationDegrees(i))
        return q
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class RotateYCameraMode : ParticleCameraMode {
    companion object {
        val CODEC: Codec<RotateYCameraMode> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { RotateYCameraMode() }
        }
    }

    override val type = ParticleCameraModeType.ROTATE_Y

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val i = if (angle == 0F) 0F else MathHelper.lerp(deltaTicks, prevAngle, angle)

        val q2 = RotationAxis.POSITIVE_Y.rotationDegrees(/*180 - */cameraYaw)
        q2.hamiltonProduct(RotationAxis.POSITIVE_Z.rotationDegrees(i))

        return q2
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class LookAtXYZ : ParticleCameraMode {
    companion object {
        val CODEC: Codec<LookAtXYZ> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { LookAtXYZ() }
        }
    }

    override val type = ParticleCameraModeType.LOOK_AT_XYZ
    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val i = if (angle == 0F) 0F else MathHelper.lerp(deltaTicks, prevAngle, angle)
        val rotation = Quaternionf()
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Z.rotationDegrees(i))
        return rotation
    }
}

class LookAtY : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.LOOK_AT_Y
    companion object {
        val CODEC: Codec<LookAtY> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { LookAtY() }
        }
    }
    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        /*
        val vec = matrixWrapper.getOrigin().add(particlePosition)
        val v = cameraPosition.withAxis(Direction.Axis.Y, vec.y)
*/

        val rotation = Quaternionf(0F, 0F, 0F, 1F)
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}
class DirectionZ : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.DIRECTION_Z
    companion object {
        val CODEC: Codec<DirectionZ> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { DirectionZ() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val rotation = Quaternionf(0F, 0F, 0F, 1F)
        val y = atan2(viewDirection.x, viewDirection.z)
        val x = atan2(viewDirection.y, sqrt(viewDirection.x.pow(2.0) + viewDirection.z.pow(2.0)))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_X.rotationDegrees(-x.toFloat()))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(y.toFloat()))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class EmitterYZPlane : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.EMITTER_YZ_PLANE
    companion object {
        val CODEC: Codec<EmitterYZPlane> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { EmitterYZPlane() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val rotation = Quaternionf(0F, 0F, 0F, 1F)

        val quat = AxisAngle4d(rotation)
        matrixWrapper.matrix.getRotation(quat)
        rotation.set(quat)

        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(180F)) // Don't worry about it.

        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(90F))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}

}

class EmitterXZPlane : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.EMITTER_XZ_PLANE
    companion object {
        val CODEC: Codec<EmitterXZPlane> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { EmitterXZPlane() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val rotation = Quaternionf(0F, 0F, 0F, 1F)

        val quat = AxisAngle4d(rotation)
        matrixWrapper.matrix.getRotation(quat)
        rotation.set(quat)
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(180F)) // Don't worry about it.

        rotation.hamiltonProduct(RotationAxis.POSITIVE_X.rotationDegrees(-90F))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}

}

class EmitterXYPlane : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.EMITTER_XY_PLANE
    companion object {
        val CODEC: Codec<EmitterXYPlane> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { EmitterXYPlane() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val rotation = Quaternionf(0F, 0F, 0F, 1F)

        val quat = AxisAngle4d(rotation)
        matrixWrapper.matrix.getRotation(quat)
        rotation.set(quat)

        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(180F)) // Don't worry about it.

        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}

}
class DirectionY : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.DIRECTION_Y
    companion object {
        val CODEC: Codec<DirectionY> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { DirectionY() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val rotation = Quaternionf(0F, 0F, 0F, 1F)
        val y = atan2(viewDirection.x, viewDirection.z)
        val x = atan2(viewDirection.y, sqrt(viewDirection.x.pow(2.0) + viewDirection.z.pow(2.0)))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_X.rotationDegrees(x.toFloat() - PI.toFloat()/2f))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(y.toFloat() - PI.toFloat()))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class DirectionX : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.DIRECTION_X
    companion object {
        val CODEC: Codec<DirectionX> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { DirectionX() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val rotation = Quaternionf(0F, 0F, 0F, 1F)
        val y = atan2(viewDirection.x, viewDirection.z)
        val z = atan2(viewDirection.y, sqrt(viewDirection.x.pow(2.0) + viewDirection.z.pow(2.0)))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotationDegrees(y.toFloat() - PI.toFloat()/2f))
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Z.rotationDegrees(z.toFloat()))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class LookAtDirection : ParticleCameraMode {
    override val type: ParticleCameraModeType = ParticleCameraModeType.LOOK_AT_DIRECTION
    companion object {
        val CODEC: Codec<LookAtDirection> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { LookAtDirection() }
        }
    }

    override fun getRotation(
        matrixWrapper: MatrixWrapper,
        prevAngle: Float,
        angle: Float,
        deltaTicks: Float,
        particlePosition: Vec3d,
        cameraPosition: Vec3d,
        cameraAngle: Quaternionf,
        cameraYaw: Float,
        cameraPitch: Float,
        viewDirection: Vec3d
    ): Quaternionf {
        val particleAngle = if (angle == 0.0f) 0F else MathHelper.lerp(deltaTicks, prevAngle, angle)

        // Next 4 lines are my (bad) attempts at calculating a rotation to supply on #471 based on camera displacement from particle.
        val flattenedParticlePos = particlePosition.withAxis(Direction.Axis.Y, cameraPosition.y)
        val directionToParticleXZ = flattenedParticlePos.subtract(cameraPosition).normalize().toVector3f()
        val viewDirectionFloats = Vector3f(viewDirection.x.toFloat(), viewDirection.y.toFloat(), viewDirection.z.toFloat())
        val yRot = atan2(directionToParticleXZ.z, directionToParticleXZ.x)

        val rotation = Quaternionf()

        // look_at_direction is meant to align X(?) with the view direction but only +/-Y seems to do it right. Weird.
        rotation.rotateTo(Vector3f(0F, -1F, 0F), viewDirectionFloats.mul(-1F))
        // Spin backwards 90 degrees about Z to address us using X earlier. Maybe need to spin forwards 90 degrees. Later problem.
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Z.rotation(-(PI/2).toFloat()))
        // Apply particle rotation, not really relevant to my issue and is working :D
        rotation.hamiltonProduct(RotationAxis.POSITIVE_Z.rotationDegrees(particleAngle))
        // This rotation is wrong, but around the right axis. I feel like this could be the problem area but also maybe wrong entirely.
        // Is it even possible to figure out what angle to use?

        // What's being asked is that we rotate local Y such that it is perpendicular to the camera plane. Cross product?

        rotation.hamiltonProduct(RotationAxis.POSITIVE_Y.rotation(yRot))
        return rotation
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}
enum class ParticleCameraModeType {
    ROTATE_XYZ,
    ROTATE_Y,
    LOOK_AT_XYZ,
    LOOK_AT_Y,
    LOOK_AT_DIRECTION,
    DIRECTION_X,
    DIRECTION_Y,
    DIRECTION_Z,
    EMITTER_XY_PLANE,
    EMITTER_XZ_PLANE,
    EMITTER_YZ_PLANE
}