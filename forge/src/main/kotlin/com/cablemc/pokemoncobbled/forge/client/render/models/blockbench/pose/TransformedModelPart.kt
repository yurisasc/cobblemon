package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pose

import net.minecraft.client.model.geom.ModelPart

/**
 * Represents a [ModelPart] with some changes to position and rotation. This is to take a snapshot
 * and store mutations for the purpose of poses.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
class TransformedModelPart(
    /** The [ModelPart] that is being transformed. */
    val modelPart: ModelPart
) {
    companion object {
        const val X_AXIS = 0
        const val Y_AXIS = 1
        const val Z_AXIS = 2
    }

    var changeFactor = 1F

    val initialPosition = floatArrayOf(modelPart.x, modelPart.y, modelPart.z)
    val initialRotation = floatArrayOf(modelPart.xRot, modelPart.yRot, modelPart.zRot)

    var position = floatArrayOf(modelPart.x, modelPart.y, modelPart.z)
    var rotation = floatArrayOf(modelPart.xRot, modelPart.yRot, modelPart.zRot)

    /** Applies the transformation to the model part. */
    fun apply() {
        modelPart.setPos(position[0], position[1], position[2])
        modelPart.setRotation(rotation[0], rotation[1], rotation[2])
    }

    /** Sets the part back to its original location, prior to this transformation. */
    fun applyDefaults() {
        modelPart.setPos(initialPosition[0], initialPosition[1], initialPosition[2])
        modelPart.setRotation(initialRotation[0], initialRotation[1], initialRotation[2])
    }

    var xPos: Float
        get() = position[0]
        set(value) {
            position[0] = value
        }
    var yPos: Float
        get() = position[1]
        set(value) {
            position[1] = value
        }
    var zPos: Float
        get() = position[2]
        set(value) {
            position[2] = value
        }
    var xRot: Float
        get() = rotation[0]
        set(value) {
            rotation[0] = value
        }
    var yRot: Float
        get() = rotation[1]
        set(value) {
            rotation[1] = value
        }
    var zRot: Float
        get() = rotation[2]
        set(value) {
            rotation[2] = value
        }

    fun withPosition(axis: Int, position: Float): TransformedModelPart {
        this.position[axis] = position
        return this
    }

    fun withPosition(xPos: Float, yPos: Float, zPos: Float): TransformedModelPart {
        return withPosition(X_AXIS, xPos).withPosition(Y_AXIS, yPos).withPosition(Z_AXIS, zPos)
    }

    fun withRotation(axis: Int, angleRadians: Float): TransformedModelPart {
        this.rotation[axis] = angleRadians
        return this
    }

    fun withRotation(xRot: Float, yRot: Float, zRot: Float): TransformedModelPart {
        return withRotation(X_AXIS, xRot).withRotation(Y_AXIS, yRot).withRotation(Z_AXIS, zRot)
    }

    fun addPosition(axis: Int, distance: Float): TransformedModelPart {
        return withPosition(axis, position[axis] + distance * changeFactor)
    }

    fun addRotation(axis: Int, angleRadians: Float): TransformedModelPart {
        return withRotation(axis, rotation[axis] + angleRadians * changeFactor)
    }
}