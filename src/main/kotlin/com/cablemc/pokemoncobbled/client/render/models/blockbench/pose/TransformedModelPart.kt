package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

import net.minecraft.client.model.geom.ModelPart

class TransformedModelPart(
    val modelPart: ModelPart
) {
    companion object {
        const val X_AXIS = 0
        const val Y_AXIS = 1
        const val Z_AXIS = 2
    }

    val initialPosition = floatArrayOf(modelPart.x, modelPart.y, modelPart.z)
    val initialRotation = floatArrayOf(modelPart.xRot, modelPart.yRot, modelPart.zRot)

    var position = floatArrayOf(modelPart.x, modelPart.y, modelPart.z)
    var rotation = floatArrayOf(modelPart.xRot, modelPart.yRot, modelPart.zRot)

    fun apply() {
        modelPart.setPos(position[0], position[1], position[2])
        modelPart.setRotation(rotation[0], rotation[1], rotation[2])
    }

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
}