/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.item.CobblemonItem
import net.minecraft.text.Text
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.client.Mouse
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SpyglassItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.util.Identifier
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11


class PhotodexItem : /*CobblemonItem(Settings()),*/ SpyglassItem(Settings()) {
    private var isZooming = false
    private var zoomLevel = 1.0

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
       if (isZooming != true) {
            if (user is ServerPlayerEntity) {
                isZooming = true
                zoomLevel = 1.0


                // Send a chat message to the player
                //user.sendMessage(Text.of("Zooming In"));

                //return super.use(world, user, hand)  // Call the parent class's use method
            }

           //detectPokemon(world, user, hand)

       }
        else {
           isZooming = false
           zoomLevel = 1.0

           // Send a chat message to the player
           //user.sendMessage(Text.of("Zooming Out"));

        }
        return TypedActionResult.success(user.getStackInHand(hand))

    }

    fun detectPokemon(world: World, user: PlayerEntity, hand: Hand) {
        if (user is ServerPlayerEntity && isZooming) {
            val eyePos = user.getCameraPosVec(1.0F)
            val lookVec = user.getRotationVec(1.0F)
            val maxDistance = 100.0  // Set this to how far you want the ray to go
            var closestEntity: Entity? = null
            var closestDistance = maxDistance

            // Send a chat message to the player
            //user.sendMessage(Text.of("Trying to Detect Entity"));

            // Define a large bounding box around the player
            val boundingBox = Box(
                    user.x - 200.0, user.y - 200.0, user.z - 200.0,
                    user.x + 200.0, user.y + 200.0, user.z + 200.0
            )

            // Get all entities within the bounding box
            val entities = user.world.getEntitiesByClass(Entity::class.java, boundingBox, { it != user })

            for (entity in entities) {
                val entityBox: Box = entity.boundingBox
                val intersection = entityBox.raycast(eyePos, eyePos.add(lookVec.multiply(maxDistance)))

                if (intersection.isPresent) {
                    val distanceToEntity = eyePos.distanceTo(intersection.get())
                    if (distanceToEntity < closestDistance) {
                        closestEntity = entity
                        closestDistance = distanceToEntity
                    }
                }
            }

            if (closestEntity != null && closestEntity is PokemonEntity) {
                user.sendMessage(Text.of("You have scanned a: ${closestEntity.pokemon.species.name}"))

            }
        }
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, entity: LivingEntity, timeLeft: Int) {
        isZooming = false
    }

    fun renderPhotodexOverlay(matrixStack: MatrixStack) {
        // Assuming your texture is meant to cover the full screen or a significant portion of it.
        // These dimensions will scale the texture to fit as desired.
        val screenWidth = MinecraftClient.getInstance().window.scaledWidth
        val screenHeight = MinecraftClient.getInstance().window.scaledHeight

        // Your custom overlay texture
        val photodexOverlayTexture = Identifier("textures/item/keyitems/PhotodexLens.png")

        // Binding the texture
        MinecraftClient.getInstance().textureManager.bindTexture(photodexOverlayTexture)

        // Enable blending for transparency
        //RenderSystem.enableBlend()
        //RenderSystem.blendFuncSeparate(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA, RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ZERO)

        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        // Drawing the overlay
        drawTexture(matrixStack, 0, 0, screenWidth, screenHeight, 0f, 0f, 256, 256, 256, 256)

        // Disable blending if you enabled it earlier
        RenderSystem.disableBlend()
    }

    // Adjusted to accept float texture coordinates and dimensions
    private fun drawTexture(
            matrices: MatrixStack,
            x: Int, y: Int, width: Int, height: Int,
            u0: Float, v0: Float, regionWidth: Int, regionHeight: Int, textureWidth: Int, textureHeight: Int
    ) {
        val drawMode = VertexFormat.DrawMode.QUADS // Quads
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(drawMode, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrices.peek().positionMatrix, x.toFloat(), (y + height).toFloat(), 0f).texture(u0 / textureWidth, (v0 + regionHeight) / textureHeight).next()
        bufferBuilder.vertex(matrices.peek().positionMatrix, (x + width).toFloat(), (y + height).toFloat(), 0f).texture((u0 + regionWidth) / textureWidth, (v0 + regionHeight) / textureHeight).next()
        bufferBuilder.vertex(matrices.peek().positionMatrix, (x + width).toFloat(), y.toFloat(), 0f).texture((u0 + regionWidth) / textureWidth, v0 / textureHeight).next()
        bufferBuilder.vertex(matrices.peek().positionMatrix, x.toFloat(), y.toFloat(), 0f).texture(u0 / textureWidth, v0 / textureHeight).next()
        tessellator.draw()
    }

}
