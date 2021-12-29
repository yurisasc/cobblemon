package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.common.util.math.geometry.toDegrees
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.util.Mth.PI
import org.junit.jupiter.api.Test
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos

internal class RootPokeBallLookAnimationTest {
    @Test
    fun `attempts at getting rotation logic working, i am not done trying angryface`() {
        val xRot = PI / 3
        val yRot = PI / 3
        val zRot = 0F

        val desiredZ = PI / 3

        val poseStack = PoseStack()
        poseStack.mulPose(Vector3f.ZP.rotation(zRot))
        poseStack.mulPose(Vector3f.YP.rotation(yRot))
        poseStack.mulPose(Vector3f.XP.rotation(xRot))
        val zAxis = Vector3f.ZP.copy()
        zAxis.transform(poseStack.last().normal())
        poseStack.mulPose(zAxis.rotation(desiredZ))

        val y = Vector3f.YP.copy()
        y.transform(poseStack.last().normal())
        val py1 = y.x()

        val z = Vector3f.ZP.copy()
        z.transform(poseStack.last().normal())
        val pz1 = z.x()
        val pz3 = z.z()

//        val beta = atan2(d.y(), d.x())
//        val alpha = asin(d.z())


        val beta = asin(pz1)
        val alpha = acos(pz3 / cos(beta))
        val gamma = asin(-py1 / cos(beta))

        val poseStack2 = PoseStack()
        poseStack2.mulPose(Vector3f.ZP.rotation(gamma))
        poseStack2.mulPose(Vector3f.YP.rotation(beta))
        poseStack2.mulPose(Vector3f.XP.rotation(alpha))
        val y2 = Vector3f(3F, 0F, -4F)
        y2.transform(poseStack.last().normal())
        println(y2)

        val alphaD = alpha.toDegrees()
        val betaD = beta.toDegrees()
        val gammaD = gamma.toDegrees()

        println("xRot: $alphaD, yRot: $betaD, zRot: $gammaD")
    }
}