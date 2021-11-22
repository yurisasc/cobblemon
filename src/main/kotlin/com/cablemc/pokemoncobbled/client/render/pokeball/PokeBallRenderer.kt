//package ca.landonjw.kotlinmon.client.render.pokeball
//
//import ca.landonjw.kotlinmon.api.pokeball.PokeBall
//import com.cablemc.kotlinmon.client.render.models.smd.renderer.RotationOffset
//import com.cablemc.kotlinmon.client.render.models.smd.renderer.Scale
//import com.cablemc.kotlinmon.client.render.models.smd.renderer.SmdModelRenderer
//import com.cablemc.pokemoncobbled.client.render.models.smd.repository.ModelRepository
//import ca.landonjw.kotlinmon.common.pokeball.entity.DefaultPokeBallEntity
//import com.cablemc.kotlinmon.common.util.math.geometry.toRadians
//import com.mojang.blaze3d.matrix.MatrixStack
//import net.minecraft.client.renderer.IRenderTypeBuffer
//import net.minecraft.client.renderer.culling.ClippingHelper
//import net.minecraft.client.renderer.entity.EntityRenderer
//import net.minecraft.client.renderer.entity.EntityRendererManager
//import net.minecraft.resources.ResourceLocation
//import com.mojang.math.Vector3f
//
//class PokeBallRenderer<T: DefaultPokeBallEntity>(
//    manager: EntityRendererManager,
//    private val modelRepository: ModelRepository,
//    private val modelRenderer: SmdModelRenderer
//) : EntityRenderer<T>(manager) {
//
//    override fun render(
//        entity: T,
//        entityYaw: Float,
//        partialTicks: Float,
//        matrixStack: MatrixStack,
//        buffer: IRenderTypeBuffer,
//        packedLight: Int
//    ) {
//        val pokeBall: PokeBall = entity.pokeBallType
//        val pokeBallModel = modelRepository.getModel(pokeBall.modelLocation) ?: return // TODO: This is synchronous
//        val pokeBallOrientation = Vector3f(
//            entity.orientation.x.toFloat().toRadians(),
//            entity.orientation.y.toFloat().toRadians(),
//            entity.orientation.z.toFloat().toRadians()
//        )
//
//        pokeBallModel.replaceProperty(RotationOffset(pokeBallOrientation))
//        pokeBallModel.replaceProperty(Scale(Vector3f(0.1f, 0.1f, 0.1f)))
//        modelRenderer.render(matrixStack, pokeBallModel)
//    }
//
//    override fun getTextureLocation(entity: T) = null
//
//    override fun shouldRender(
//        livingEntityIn: T,
//        camera: ClippingHelper,
//        camX: Double,
//        camY: Double,
//        camZ: Double
//    ): Boolean {
//        return true
//    }
//
//}