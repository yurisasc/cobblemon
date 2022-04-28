package com.cablemc.pokemoncobbled.common.client.render.models.blockbench

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import net.minecraft.client.model.geom.ModelPart

fun ModelPart.getPosition(axis: Int) = if (axis == X_AXIS) this.x else if (axis == Y_AXIS) this.y else this.z
fun ModelPart.getRotation(axis: Int) = if (axis == X_AXIS) this.xRot else if (axis == Y_AXIS) this.yRot else this.zRot
fun ModelPart.setRotation(axis: Int, angleInRadians: Float): ModelPart {
    if (axis == X_AXIS) {
        xRot = angleInRadians
    } else if (axis == Y_AXIS) {
        yRot = angleInRadians
    } else {
        zRot = angleInRadians
    }
    return this
}
fun ModelPart.setPosition(axis: Int, position: Float): ModelPart {
    if (axis == X_AXIS) {
        x = position
    } else if (axis == Y_AXIS) {
        y = position
    } else {
        z = position
    }
    return this
}
fun ModelPart.addRotation(axis: Int, differenceInRadians: Float) = setRotation(axis, getRotation(axis) + differenceInRadians)
fun ModelPart.addPosition(axis: Int, difference: Float) = setPosition(axis, getPosition(axis) + difference)
fun ModelPart.withPosition(axis: Int, position: Float) = TransformedModelPart(this).withPosition(axis, position)
fun ModelPart.withPosition(xPos: Float, yPos: Float, zPos: Float) = TransformedModelPart(this).withPosition(xPos, yPos, zPos)
fun ModelPart.withRotation(axis: Int, angleRadians: Float) = TransformedModelPart(this).withRotation(axis, angleRadians)
fun ModelPart.withRotation(xRot: Float, yRot: Float, zRot: Float) = TransformedModelPart(this).withRotation(xRot, yRot, zRot)
fun ModelPart.asTransformed() = TransformedModelPart(this)
fun ModelPart.getChildOf(vararg path: String): ModelPart {
    var part = this
    for (piece in path) {
        part = part.getChild(piece)
    }
    return part
}
fun ModelPart.childNamed(vararg path: String): Pair<String, ModelPart> {
    var final = path.last()
    return final to getChildOf(*path)
}