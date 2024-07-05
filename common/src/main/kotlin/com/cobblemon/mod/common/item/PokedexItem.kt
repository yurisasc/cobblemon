/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.net.messages.client.ui.PokedexUIPacket
import com.cobblemon.mod.common.net.messages.server.pokedex.MapUpdatePacket
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.isLookingAt
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.lwjgl.glfw.GLFW
import net.minecraft.client.render.*
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.text.Text
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.server
import net.minecraft.block.BlockState
import net.minecraft.block.MapColor
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.gui.hud.InGameOverlayRenderer
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Items
import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtInt
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.*
import net.minecraft.world.RaycastContext
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.io.ByteArrayInputStream
import java.io.File

class PokedexItem(val type: String) : CobblemonItem(Settings()) {

    @JvmField
    var zoomLevel: Double = 1.0
    var isScanning = false
    var attackKeyHeldTicks = 0 // to help with detecting pressed and held states of the attack button
    var pokemonInFocus: PokemonEntity? = null
    var lastPokemonInFocus: PokemonEntity? = null
    var pokemonBeingScanned: PokemonEntity? = null
    var scanningProgress: Int = 0
    var pokedexUser: ServerPlayerEntity? = null
    var originalHudHidden: Boolean = true
    var bufferImageSnap:  Boolean = false

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int {
        return 72000  // (vanilla bows use 72000 ticks -> 1 hour of hold time)
    }

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean {
        return !isScanning && super.postHit(stack, target, attacker)
    }

    override fun postMine(stack: ItemStack?, world: World?, state: BlockState?, pos: BlockPos?, miner: LivingEntity?): Boolean {
        return !isScanning && super.postMine(stack, world, state, pos, miner)
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity?): Boolean {
        return !isScanning && super.canMine(state, world, pos, miner)
    }

    override fun use(world: World, player: PlayerEntity, usedHand: Hand): TypedActionResult<ItemStack> {
        val itemStack = player.getStackInHand(usedHand)

        if (player !is ServerPlayerEntity) return TypedActionResult.success(itemStack, world.isClient) else pokedexUser = player

        player.setCurrentHand(usedHand) // Start using the item

        /*if (player.isSneaking) {
            isScanning = true
            //return TypedActionResult.consume(itemStack)
        }
        else {
            openPokdexGUI(player)
        }*/

        return TypedActionResult.fail(itemStack)


        /*// Check if the player is interacting with a Pokémon
        val entity = player.world
                .getOtherEntities(player, Box.of(player.pos, 16.0, 16.0, 16.0))
                .filter { player.isLookingAt(it, stepDistance = 0.1F) }
                .minByOrNull { it.distanceTo(player) } as? PokemonEntity?

        if (!player.isSneaking) {
            if (entity != null) {
                val species = entity.pokemon.species.resourceIdentifier
                val form = entity.pokemon.form.formOnlyShowdownId()

                val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
                pokedexData.onPokemonSeen(species, form)
                player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData(), false))
                PokedexUIPacket(type, species).sendToPlayer(player)
                player.playSoundToPlayer(CobblemonSounds.POKEDEX_SCAN, SoundCategory.PLAYERS, 1F, 1F)
            } else {
                PokedexUIPacket(type).sendToPlayer(player)
            }
            player.playSoundToPlayer(CobblemonSounds.POKEDEX_SHOW, SoundCategory.PLAYERS, 1F, 1F)
        } else {
            inUse = true
            zoomLevel = 1.0
            changeFOV(70.0)
            player.setCurrentHand(usedHand) // Start using the item
            return TypedActionResult.consume(itemStack)
        }*/

        //return TypedActionResult.success(itemStack)
    }

    fun openPokdexGUI(player: ServerPlayerEntity) {
        // Check if the player is interacting with a Pokémon
        /*val entity = player.world
                .getOtherEntities(player, Box.of(player.pos, 16.0, 16.0, 16.0))
                .filter { player.isLookingAt(it, stepDistance = 0.1F) }
                .minByOrNull { it.distanceTo(player) } as? PokemonEntity?

        if (!player.isSneaking) {
            if (entity != null) {
                val species = entity.pokemon.species.resourceIdentifier
                val form = entity.pokemon.form.formOnlyShowdownId()

                val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
                pokedexData.onPokemonSeen(species, form)
                player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData(), false))
                PokedexUIPacket(type, species).sendToPlayer(player)
                playSound(CobblemonSounds.POKEDEX_SCAN)
                //player.playSoundToPlayer(CobblemonSounds.POKEDEX_SCAN, SoundCategory.PLAYERS, 1F, 1F)
            } else {*/
        PokedexUIPacket(type).sendToPlayer(player)
            //}
        playSound(CobblemonSounds.POKEDEX_OPEN)
        //player.playSoundToPlayer(CobblemonSounds.POKEDEX_OPEN, SoundCategory.PLAYERS, 1F, 1F)
        //}
    }

    override fun usageTick(world: World?, user: LivingEntity?, stack: ItemStack?, remainingUseTicks: Int) {
        if (user is PlayerEntity) {
            // if the item has been used for more than 1 second activate scanning mode
            if (getMaxUseTime(stack, user) - remainingUseTicks > 2) {
                // play the Scanner Open sound only once
                if (isScanning == false) {
                    playSound(CobblemonSounds.POKEDEX_SCAN_OPEN)
                    val client = MinecraftClient.getInstance()

                    // Hide the HUD during scaner mode
                    originalHudHidden = client.options.hudHidden

                    client.options.hudHidden = true
                }



               // isScanning = true

                // todo get it constantly scanning outwards to detect pokemon in focus
                MinecraftClient.getInstance().player?.let {
                    detectPokemon(it.world, it, Hand.MAIN_HAND)
                }

                // if there was a mouse click last tick and overlay is now down
                if (bufferImageSnap) {
                    MinecraftClient.getInstance().execute {
                        MinecraftClient.getInstance().player?.let {
                            //println("You have taken a picture")
                            playSound(CobblemonSounds.POKEDEX_SNAP_PICTURE)
                            //detectPokemon(it.world, it, Hand.MAIN_HAND)

                            // Todo create a "shotgun" ray cast to determine all Pokemon in the picture to be used for later

                            // take picture while overlay is down this tick
                            snapPicture(it)
                        }
                    }

                    // bring back overlay next tick
                    bufferImageSnap = false
                }

                isScanning = true

                // todo try to make it so that the player is able to walk normal speed while in scanner mode
                //user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, 3, 1, true, false, false)) // Remove slowness effect
            }
        }

        super.usageTick(world, user, stack, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack?, world: World, entity: LivingEntity, timeLeft: Int) {
        // todo if less than a second then open GUI
        if (entity is ServerPlayerEntity && (getMaxUseTime(stack, entity) - timeLeft) <= 2) {
            openPokdexGUI(entity)
        } else { // any other amount of time assume scanning mode was active
            val client = MinecraftClient.getInstance()
            // Restore the HUD
            client.options.hudHidden = originalHudHidden
            isScanning = false
            zoomLevel = 1.0
            attackKeyHeldTicks = 0
            changeFOV(70.0)
            playSound(CobblemonSounds.POKEDEX_SCAN_CLOSE)

            // todo if solution is found to boost player speed during scanning mode, you might need to end it here
            //entity.removeStatusEffect(StatusEffects.SPEED) // Remove slowness effect
        }

        super.onStoppedUsing(stack, world, entity, timeLeft)
    }

    // todo idk if we ever will need this since it is only fired when maxUseTime is reached whcich for this item is currently at 3 hours
    /*override fun finishUsing(stack: ItemStack?, world: World?, entity: LivingEntity?): ItemStack? {
        *//*if (timeLeft < 20) {
            // todo if less than a second then open GUI
            openPokdexGUI(entity as ServerPlayerEntity)
        }*//*

        if (world != null) {
            if (world.isClient && entity is PlayerEntity) {
                inUse = false
                isScanning = false
                zoomLevel = 1.0
                changeFOV(70.0)
            }
        }

        super.finishUsing(stack, world, entity)
    }*/

    // todo maybe add a feature to scan objects like blocks for more info?
    /*override fun useOnBlock() {

    }*/

    fun changeFOV(fov: Double) {
        val client = MinecraftClient.getInstance()
        val oldFov = fov.toInt()
        val newFov = (fov / zoomLevel).coerceIn(30.0, 110.0).toInt()

        if (newFov != oldFov) {
            playSound(CobblemonSounds.POKEDEX_ZOOM_INCREMENT)
        }

        // logging for testing
        //println("Setting FOV to: $newFov with zoomLevel: $zoomLevel")

        client.options.fov.setValue(newFov)
    }

    @Environment(EnvType.CLIENT)
    fun renderPhotodexOverlay(drawContext: DrawContext, scale: Float) {
        val client = MinecraftClient.getInstance()
        val screenWidth = client.window.scaledWidth
        val screenHeight = client.window.scaledHeight

        // Texture dimensions
        val textureWidth = 345
        val textureHeight = 207

        // Calculate centered position
        val x = (screenWidth - textureWidth) / 2
        val y = (screenHeight - textureHeight) / 2

        val pokedexScannerOverlayTexture = cobblemonResource("textures/gui/pokedex/pokedex_scanner.png")

        RenderSystem.enableBlend()
        drawContext.drawTexture(pokedexScannerOverlayTexture, x, y, -90, 0.0f, 0.0f, textureWidth, textureHeight, textureWidth, textureHeight)
        RenderSystem.disableBlend()

        drawContext.fill(RenderLayer.getGuiOverlay(), 0, y + textureHeight, screenWidth, screenHeight, -90, -16777216)
        drawContext.fill(RenderLayer.getGuiOverlay(), 0, 0, screenWidth, y, -90, -16777216)
        drawContext.fill(RenderLayer.getGuiOverlay(), 0, y, x, y + textureHeight, -90, -16777216)
        drawContext.fill(RenderLayer.getGuiOverlay(), x + textureWidth, y, screenWidth, y + textureHeight, -90, -16777216)
    }

    @Environment(EnvType.CLIENT)
    fun onRenderOverlay(drawContext: DrawContext) {
        if (isScanning) {
            renderPhotodexOverlay(drawContext, 1.0f)
        }
    }

    @Environment(EnvType.CLIENT)
    fun onMouseClick() {
        // Todo try to start a buffer to taking a pic to try to wait until overlay is down
        bufferImageSnap = true

        /*if (isScanning) {

            MinecraftClient.getInstance().player?.let {
                //println("You have taken a picture")
                playSound(CobblemonSounds.POKEDEX_SNAP_PICTURE)
                //detectPokemon(it.world, it, Hand.MAIN_HAND)

                // todo take picture
                snapPicture(it)
            }
        }*/
    }

    @Environment(EnvType.CLIENT)
    fun onMouseHeld() {
        if (isScanning) {
            MinecraftClient.getInstance().player?.let {
                //println("You are scanning")
                //playSound(CobblemonSounds.POKEDEX_SCAN_LOOP)

                // if pokemonInFocus is not null start scanning it
                if (pokemonInFocus != null) {
                    // todo if the pokemonInFocus is equal to pokemonBeingScanned
                    if (pokemonInFocus == pokemonBeingScanned) {
                        scanPokemon(pokemonInFocus!!, pokedexUser!!)
                    } else {
                        // reset scanning progress
                        scanningProgress = 0
                        pokemonBeingScanned = pokemonInFocus


                    }
                } else {
                    pokemonBeingScanned = null
                }
                detectPokemon(it.world, it, Hand.MAIN_HAND)
            }
        }
    }

    fun scanPokemon(pokemonEntity: PokemonEntity, player: ServerPlayerEntity) {
        // increment scan progress
        if (scanningProgress < 100)
            scanningProgress += 2

        if (scanningProgress % 5 == 0) { // 20 for 1 second

            // todo get a better (maybe shorter) looping sound so it ends nicer
            //playSound(CobblemonSounds.POKEDEX_SCAN_LOOP)

            // play this temp sound for now
            playSound(CobblemonSounds.POKEDEX_CLICK)
        }

        // if scan progress is 100 then send packet to Pokedex
        if (scanningProgress == 100) {
            val species = pokemonEntity.pokemon.species.resourceIdentifier
            val form = pokemonEntity.pokemon.form.formOnlyShowdownId()

            val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
            pokedexData.onPokemonSeen(species, form)
            player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData(), false))
            PokedexUIPacket(type, species).sendToPlayer(player)
            playSound(CobblemonSounds.POKEDEX_SCAN)

            scanningProgress = 0
        }
    }

    fun detectPokemonInView(player: PlayerEntity, maxDistance: Double): List<Species> {
        val detectedSpecies = mutableListOf<Species>()
        val eyePos = player.getCameraPosVec(1.0F)
        val lookVec = player.getRotationVec(1.0F)
        val client = MinecraftClient.getInstance()
        val fov = client.options.fov.value
        val screenWidth = client.window.scaledWidth
        val screenHeight = client.window.scaledHeight

        // Number of raycasts in each dimension
        val raycastResolution = 10
        val fovRadians = Math.toRadians(fov.toDouble())

        // Calculate the step size for each ray in terms of angle
        val angleStep = fovRadians / raycastResolution

        // Iterate over the grid within the frustum
        for (x in -raycastResolution..raycastResolution) {
            for (y in -raycastResolution..raycastResolution) {
                // Calculate the direction of the current ray
                val offsetX = x * angleStep
                val offsetY = y * angleStep

                val rayDirection = lookVec.rotatePitch(offsetY).rotateYaw(offsetX)
                val rayEnd = eyePos.add(rayDirection.multiply(maxDistance))

                val hitResult = player.world.raycast(
                    RaycastContext(
                        eyePos,
                        rayEnd,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player
                    )
                )

                if (hitResult.type == HitResult.Type.ENTITY) {
                    val entity = (hitResult as EntityHitResult).entity
                    if (entity is PokemonEntity) {
                        detectedSpecies.add(entity.pokemon.species)
                    }
                }
            }
        }

        return detectedSpecies.distinct()
    }

    // Helper extension functions for rotating vectors
    fun Vec3d.rotateYaw(angle: Double): Vec3d {
        val cos = Math.cos(angle)
        val sin = Math.sin(angle)
        return Vec3d(this.x * cos - this.z * sin, this.y, this.x * sin + this.z * cos)
    }

    fun Vec3d.rotatePitch(angle: Double): Vec3d {
        val cos = Math.cos(angle)
        val sin = Math.sin(angle)
        return Vec3d(this.x, this.y * cos - this.z * sin, this.y * sin + this.z * cos)
    }


    private fun detectPokemon(world: World, user: PlayerEntity, hand: Hand) {
        if (isScanning) {
            val eyePos = user.getCameraPosVec(1.0F)
            val lookVec = user.getRotationVec(1.0F)
            val maxDistance = 30.0  // Set this to how far we want the raycast to go
            val boundingBoxSize = 50.0
            var closestEntity: Entity? = null
            var closestDistance = maxDistance

            // Define a large bounding box around the player
            val boundingBox = Box(
                    user.x - boundingBoxSize, user.y - boundingBoxSize, user.z - boundingBoxSize,
                    user.x + boundingBoxSize, user.y + boundingBoxSize, user.z + boundingBoxSize
            )

            // Get all entities within the bounding box
            val entities = user.world.getEntitiesByClass(Entity::class.java, boundingBox) { it != user }

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
                pokemonInFocus = closestEntity

                // if detected pokemon is not the same as the last detected pokemon
                if (pokemonInFocus != lastPokemonInFocus) {
                    user.sendMessage(Text.of("${closestEntity.pokemon.species.name} is in focus!"))

                    // play sound for showing details of the focused pokemon
                    playSound(CobblemonSounds.POKEDEX_SCAN_DETAIL)
                }

                lastPokemonInFocus = pokemonInFocus
            } else {
                pokemonInFocus = null
                lastPokemonInFocus = null

                // todo play POKEDEX_DETAIL_DISSAPEAR sound here
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun snapPicture(player: ClientPlayerEntity) {
        MinecraftClient.getInstance().execute {


            val client = MinecraftClient.getInstance()

            /*// Hide the HUD
            val originalHudHidden = client.options.hudHidden
            client.options.hudHidden = true*/

            // Take a screenshot
            val framebuffer: Framebuffer = client.framebuffer
            val nativeImage: NativeImage = ScreenshotRecorder.takeScreenshot(framebuffer)

            /* // Restore the HUD
            client.options.hudHidden = originalHudHidden*/

            // Convert NativeImage to BufferedImage
            val imageBytes = nativeImage.bytes
            val bufferedImage: BufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))

            // Crop the image to a square centered on the original image
            val croppedImage = cropToSquare(bufferedImage)

            // Save the captured image to file for debugging
            val capturedImageFile = File("captured_image.png")
            ImageIO.write(croppedImage, "png", capturedImageFile)
            println("Saved captured image to ${capturedImageFile.absolutePath}")

            // Prepare the image to be sent
            val baos = ByteArrayOutputStream()
            ImageIO.write(croppedImage, "png", baos)
            val imageBytesToSend = baos.toByteArray()

            // Send the packet to the server
            MapUpdatePacket(imageBytesToSend).sendToServer()
        }
    }

    fun cropToSquare(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height
        val size = minOf(width, height)

        val x = (width - size) / 2
        val y = (height - size) / 2

        return image.getSubimage(x, y, size, size)
    }

    /*private fun updatePlayerMap(player: PlayerEntity, image: BufferedImage) {
        val inventory = player.inventory
        for (i in 0 until inventory.size()) {
            val stack = inventory.getStack(i)
            if (stack.item == Items.MAP) {
                val mapStack = ItemStack(Items.FILLED_MAP)
                val world = player.world as ServerWorld
                val mapId = world.increaseAndGetMapId().id

                val nbt = NbtCompound()
                nbt.putString("dimension", world.registryKey.value.toString())
                nbt.putInt("xCenter", 0)
                nbt.putInt("zCenter", 0)
                nbt.putBoolean("locked", true)
                nbt.putBoolean("unlimitedTracking", false)
                nbt.putBoolean("trackingPosition", false)
                nbt.putByte("scale", 3.toByte())
                val mapState = MapState.fromNbt(nbt, world.registryManager)

                val resizedImage = convertToBufferedImage(image.getScaledInstance(128, 128, Image.SCALE_DEFAULT))
                val pixels = convertPixelArray(resizedImage)
                // todo Use AccessWidener to get access to it
                val mapColors = MapColor.COLORS.filterNotNull().toTypedArray()

                for (x in 0 until 128) {
                    for (y in 0 until 128) {
                        val color = Color(pixels[y][x], true)
                        mapState.colors[x + y * 128] = nearestColor(mapColors, color).toByte()
                    }
                }

                world.putMapState(MapIdComponent(mapId), mapState)
                val mapIdComponent = MapIdComponent(mapId)
                mapStack.set(DataComponentTypes.MAP_ID, mapIdComponent)

                inventory.setStack(i, mapStack)
                player.sendMessage(Text.literal("SnapPicture: Map updated with screenshot"), true)
                return
            }
        }
        player.sendMessage(Text.literal("No empty map found in inventory"), true)
    }


    private val shadeCoeffs = doubleArrayOf(180.0 / 255.0, 220.0 / 255.0, 255.0 / 255.0, 135.0 / 255.0)

    fun convertToBufferedImage(img: Image): BufferedImage {
        if (img is BufferedImage) {
            return img
        }
        val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        val bGr = bimage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()
        return bimage
    }

    fun convertPixelArray(image: BufferedImage): Array<IntArray> {
        val width = image.width
        val height = image.height
        val pixels = Array(height) { IntArray(width) }
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y][x] = image.getRGB(x, y)
            }
        }
        return pixels
    }

    fun nearestColor(mapColors: Array<MapColor>, color: Color): Int {
        var closestColorIndex = 0
        var minDistance = Double.MAX_VALUE

        for (i in mapColors.indices) {
            val mcColor = mapColorToRGBColor(mapColors[i])
            val distance = colorDistance(color.red, color.green, color.blue, mcColor[0], mcColor[1], mcColor[2])
            if (distance < minDistance) {
                minDistance = distance
                closestColorIndex = i
            }
        }

        return closestColorIndex
    }

    private fun mapColorToRGBColor(color: MapColor): IntArray {
        val mcColor = color.color
        val mcColorVec = intArrayOf(
            ColorHelper.Argb.getRed(mcColor),
            ColorHelper.Argb.getGreen(mcColor),
            ColorHelper.Argb.getBlue(mcColor)
        )

        val coeff = shadeCoeffs[color.id and 3]
        return intArrayOf(
            (mcColorVec[0] * coeff).toInt(),
            (mcColorVec[1] * coeff).toInt(),
            (mcColorVec[2] * coeff).toInt()
        )
    }

    private fun colorDistance(r1: Int, g1: Int, b1: Int, r2: Int, g2: Int, b2: Int): Double {
        val dr = (r1 - r2).toDouble()
        val dg = (g1 - g2).toDouble()
        val db = (b1 - b2).toDouble()
        return Math.sqrt(dr * dr + dg * dg + db * db)
    }*/

    fun playSound(soundEvent: SoundEvent) {
        MinecraftClient.getInstance().execute {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0F))
        }
    }

    /*@Environment(EnvType.CLIENT)
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
                if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                    println("Mouse Button 1 Left Pressed")
                    MinecraftClient.getInstance().player?.let {
                        if (it.world.isClient) {
                            detectPokemon(it.world, it, Hand.MAIN_HAND)
                        }
                    }
                } else if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {
                    println("Mouse Button 1 Left Released")
                    // Implement your logic for release here
                }

                if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS) {
                    println("Mouse Button 2 Right Pressed")
                } else if (inUse && button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE) {
                    println("Mouse Button 2 Right Released")
                    inUse = false
                    // Implement your logic for release here
                }
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
    }*/





    /*override fun use(world: World, player: PlayerEntity, usedHand: Hand): TypedActionResult<ItemStack> {
        val itemStack = player.getStackInHand(usedHand)

        if (player !is ServerPlayerEntity) return TypedActionResult.success(itemStack, world.isClient)

        // Check if the player is interacting with a Pokémon
        val entity = player.world
            .getOtherEntities(player, Box.of(player.pos, 16.0, 16.0, 16.0))
            .filter { player.isLookingAt(it, stepDistance = 0.1F) }
            .minByOrNull { it.distanceTo(player) } as? PokemonEntity?

        if (!player.isSneaking) {
            if (entity != null) {
                val species = entity.pokemon.species.resourceIdentifier
                val form = entity.pokemon.form.formOnlyShowdownId()

                val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
                pokedexData.onPokemonSeen(species, form)
                player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData(), false))
                PokedexUIPacket(type, species).sendToPlayer(player)
                player.playSoundToPlayer(CobblemonSounds.POKEDEX_SCAN, SoundCategory.PLAYERS, 1F, 1F)
            } else {
                PokedexUIPacket(type).sendToPlayer(player)
            }
            player.playSoundToPlayer(CobblemonSounds.POKEDEX_SHOW, SoundCategory.PLAYERS, 1F, 1F)
        }

        return TypedActionResult.success(itemStack, world.isClient)
    }*/
}