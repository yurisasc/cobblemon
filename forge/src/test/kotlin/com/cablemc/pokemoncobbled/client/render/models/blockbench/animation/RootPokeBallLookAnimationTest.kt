package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.common.utils.math.geometry.toDegrees
import com.cablemc.pokemoncobbled.common.util.toVec3
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.util.Mth.PI
import org.junit.jupiter.api.Test
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos

internal class RootPokeBallLookAnimationTest {
    @Test
    fun `attempts at getting rotation logic working, i am not done trying angryface`() {
        val xRot = 0F
        val yRot = PI / 3
        val zRot = 0F

        val desiredZ = PI / 3F //PI / 3

        val poseStack = PoseStack()
        poseStack.mulPose(Vector3f.ZP.rotation(zRot))
        val yAxisCop = Vector3f.YP.copy()
        yAxisCop.transform(poseStack.last().normal())
        poseStack.mulPose(yAxisCop.rotation(yRot))
        val xAxisCop = Vector3f.XP.copy()
        xAxisCop.transform(poseStack.last().normal())
        poseStack.mulPose(xAxisCop.rotation(xRot))
        val zAxis = Vector3f.ZP.copy()
        zAxis.transform(poseStack.last().normal())

        println(zAxis.toVec3())

        poseStack.mulPose(zAxis.rotation(desiredZ))
        val y = Vector3f.YP.copy()
        y.transform(poseStack.last().normal())
        val py1 = y.x()

        val z = Vector3f.ZP.copy()
        z.transform(poseStack.last().normal())
        val pz1 = z.x()
        val pz3 = z.z()

        val beta = asin(pz1)
        val alpha = acos(pz3 / cos(beta))
        val gamma = asin(-py1 / cos(beta))

        val poseStack2 = PoseStack()
        poseStack2.mulPose(Vector3f.XP.rotation(alpha))
        poseStack2.mulPose(Vector3f.YP.rotation(beta))
        poseStack2.mulPose(Vector3f.ZP.rotation(gamma))

        val alphaD = alpha.toDegrees()
        val betaD = beta.toDegrees()
        val gammaD = gamma.toDegrees()

        println("xRot: $alphaD, yRot: $betaD, zRot: $gammaD")
    }
}