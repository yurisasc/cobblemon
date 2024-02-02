/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SpyglassItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.world.World
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.glfw.GLFWScrollCallback
import java.util.logging.LogManager


class PhotodexItem : /*CobblemonItem(Settings()),*/ SpyglassItem(Settings()) {
    private var inUse = false
    private var isZooming = false
    private var zoomLevel = 1.0
    private var scrollCallback = -1L
    private var mouseCallback = -1L

    private var isScrollCallbackRegistered = false
    private var isMouseButtonCallbackRegistered = false

    fun changeFOV(double: Double) {
        val client = MinecraftClient.getInstance()
        val newFov = (70 / zoomLevel).coerceIn(30.0, 110.0).toInt() // Clamping the FOV value
        client.options.fov.setValue(newFov)
    }

    // todo use method to toggle on functionality of camera
    /*override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient) {
            inUse = !inUse // toggle inUse state

            if (inUse) {
                //registerInputHandlers() // Activate input handling
                zoomLevel = 1.0 // Initialize zoom level
            } else {
                //unregisterInputHandlers() // Deactivate input handling
                zoomLevel = 1.0 // Reset zoom level
                changeFOV(70.0) // Reset FOV
            }
            return TypedActionResult.success(user.getStackInHand(hand))
        }
        return TypedActionResult.pass(user.getStackInHand(hand))
    }*/

    // todo use method for holding down right click to enable camera like the spyglass
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {

        if (world.isClient) {
            if (!inUse) {
                inUse = true // Activate only on initial click
                registerInputHandlers() // Activate input handling
                zoomLevel = 1.0 // Initialize zoom level
                changeFOV(70.0) // Set initial FOV
            }
            return TypedActionResult.success(user.getStackInHand(hand))
        }
        return TypedActionResult.pass(user.getStackInHand(hand))
    }

    // todo onStoppedUsing for ending the camera use when letting go of the Right Click
    override fun onStoppedUsing(stack: ItemStack?, world: World, entity: LivingEntity, timeLeft: Int) {
        super.onStoppedUsing(stack, world, entity, timeLeft)
        if (world.isClient && entity is PlayerEntity) {
            inUse = false // Reset on release
            unregisterInputHandlers()
            zoomLevel = 1.0
            changeFOV(70.0) // Reset FOV
        }
    }

    @Environment(EnvType.CLIENT)
    private fun registerInputHandlers() {
        val windowHandle = MinecraftClient.getInstance().window.handle

        if (!isScrollCallbackRegistered) {
            // Register scroll callback
            GLFW.glfwSetScrollCallback(windowHandle) { _, _, yOffset ->
                println("Scroll Callback Triggered: yOffset = $yOffset")

                if (yOffset != 0.0) {
                    zoomLevel += yOffset * 0.05 // Smaller increment
                    zoomLevel = zoomLevel.coerceIn(1.0, 4.0) // More controlled zoom range
                    changeFOV(70 / zoomLevel)
                }
            }
            isScrollCallbackRegistered = true
        }

        if (!isMouseButtonCallbackRegistered) {
            // Register mouse button callback
            GLFW.glfwSetMouseButtonCallback(windowHandle) { _, button, action, _ ->
                //println("Mouse Button Callback Triggered")
                //println("inUse is " + inUse.toString())

                // todo NOTE: BUTTON_1 is apparently LEFT BUTTON_2 is apparently RIGHT but we might need to check


                if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    println("Mouse Button 1 Left Pressed")
                    MinecraftClient.getInstance().player?.let {
                        if (it.world.isClient) {
                            detectPokemon(it.world, it, Hand.MAIN_HAND)
                        }
                    }
                }
                else if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {
                    println("Mouse Button 1 Left Released")
                    // Implement your logic for release here

                }

                if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS) {
                    println("Mouse Button 2 Right Pressed")

                }
                else if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE) {
                    println("Mouse Button 2 Right Released")
                    inUse = false
                    // Implement your logic for release here
                }

                /*if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    println("Mouse Button Callback Triggered")
                    MinecraftClient.getInstance().player?.let {
                        if (it.world.isClient) {
                            detectPokemon(it.world, it, Hand.MAIN_HAND)
                        }
                    }
                }*/
            }
            isMouseButtonCallbackRegistered = true
        }
    }

    private fun unregisterInputHandlers() {
        val windowHandle = MinecraftClient.getInstance().window.handle

        if (isScrollCallbackRegistered) {
            GLFW.glfwSetScrollCallback(windowHandle, null)?.free()
            isScrollCallbackRegistered = false
        }

        if (isMouseButtonCallbackRegistered) {
            GLFW.glfwSetMouseButtonCallback(windowHandle, null)?.free()
            isMouseButtonCallbackRegistered = false
        }
    }

    fun detectPokemon(world: World, user: PlayerEntity, hand: Hand) {
        if (user is ServerPlayerEntity && inUse) {
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

    /*override fun onStoppedUsing(stack: ItemStack, world: World, entity: LivingEntity, timeLeft: Int) {
        inUse = false
    }*/

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

//        RenderSystem.enableBlend()
//        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        // Drawing the overlay
        drawTexture(matrixStack, 0, 0, screenWidth, screenHeight, 0f, 0f, 256, 256, 256, 256)

        // Disable blending if you enabled it earlier
//        RenderSystem.disableBlend()
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
