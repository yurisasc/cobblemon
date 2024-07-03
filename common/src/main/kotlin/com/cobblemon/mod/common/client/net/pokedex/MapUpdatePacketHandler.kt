package com.cobblemon.mod.common.server.net.pokedex

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.server.pokedex.MapUpdatePacket
import net.minecraft.block.MapColor
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object MapUpdatePacketHandler : ServerNetworkPacketHandler<MapUpdatePacket> {

    override fun handle(packet: MapUpdatePacket, server: MinecraftServer, player: ServerPlayerEntity) {
        player.server.execute {
            (player.world as? ServerWorld)?.let { serverWorld ->
                updatePlayerMap(player, packet.imageBytes, serverWorld)
            }
        }
    }

    private fun updatePlayerMap(player: ServerPlayerEntity, imageBytes: ByteArray, world: ServerWorld) {
        println("Received image bytes: ${imageBytes.size}")
        val image = ImageIO.read(imageBytes.inputStream())
        if (image == null) {
            player.sendMessage(Text.literal("Failed to read image from bytes"), false)
            return
        }
        println("Image dimensions: ${image.width}x${image.height}")

        // Save the received image to file for debugging
        val receivedImageFile = File("received_image.png")
        ImageIO.write(convertToBufferedImage(image), "png", receivedImageFile)
        println("Saved received image to ${receivedImageFile.absolutePath}")

        val resizedImage = convertToBufferedImage(image.getScaledInstance(128, 128, Image.SCALE_SMOOTH))

        // Save the resized image to file for debugging
        val resizedImageFile = File("resized_image.png")
        ImageIO.write(resizedImage, "png", resizedImageFile)
        println("Saved resized image to ${resizedImageFile.absolutePath}")

        val pixels = convertPixelArray(resizedImage)
        val mapColors = expandMapColors(MapColor.COLORS.filterNotNull().toTypedArray())

        val inventory = player.inventory
        for (i in 0 until inventory.size()) {
            val stack = inventory.getStack(i)
            if (stack.item == Items.MAP) {
                val mapStack = ItemStack(Items.FILLED_MAP)
                val mapId = world.increaseAndGetMapId().id

                val nbt = NbtCompound().apply {
                    putString("dimension", world.registryKey.value.toString())
                    putInt("xCenter", 0)
                    putInt("zCenter", 0)
                    putBoolean("locked", true)
                    putBoolean("unlimitedTracking", false)
                    putBoolean("trackingPosition", false)
                    putByte("scale", 3.toByte())
                    put("banners", NbtList())
                }
                val mapState = MapState.fromNbt(nbt, world.registryManager)

                for (x in 0 until 128) {
                    for (y in 0 until 128) {
                        val color = Color(pixels[y][x], true)
                        val nearestColor = nearestColor(mapColors, color)
                        mapState.colors[x + y * 128] = nearestColor.toByte()
                        println("Processed pixel at ($x, $y): original color = (${color.red}, ${color.green}, ${color.blue}), nearest map color index = $nearestColor")
                    }
                }

                world.putMapState(MapIdComponent(mapId), mapState)
                val mapIdComponent = MapIdComponent(mapId)
                mapStack.set(DataComponentTypes.MAP_ID, mapIdComponent)

                inventory.setStack(i, mapStack)
                player.sendMessage(Text.literal("SnapPicture: Map updated with screenshot"), true)
                println("Map updated successfully with mapId: $mapId")
                return
            }
        }
        player.sendMessage(Text.literal("No empty map found in inventory"), true)
        println("No empty map found in inventory")
    }

    private fun convertToBufferedImage(img: Image): BufferedImage {
        return if (img is BufferedImage) {
            img
        } else {
            val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
            val bGr = bimage.createGraphics()
            bGr.drawImage(img, 0, 0, null)
            bGr.dispose()
            bimage
        }
    }

    private fun convertPixelArray(image: BufferedImage): Array<IntArray> {
        val width = image.width
        val height = image.height
        return Array(height) { y ->
            IntArray(width) { x -> image.getRGB(x, y) }
        }
    }

    private fun expandMapColors(mapColors: Array<MapColor>): List<Color> {
        val expandedColors = mutableListOf<Color>()
        for (color in mapColors) {
            val baseColor = Color(color.color)
            for (coeff in shadeCoeffs) {
                expandedColors.add(Color((baseColor.red * coeff).toInt(), (baseColor.green * coeff).toInt(), (baseColor.blue * coeff).toInt()))
            }
        }
        return expandedColors
    }

    private fun nearestColor(mapColors: List<Color>, color: Color): Int {
        var closestColorIndex = 0
        var minDistance = Double.MAX_VALUE

        for (i in mapColors.indices) {
            val mcColor = mapColors[i]
            val distance = colorDistance(color.red, color.green, color.blue, mcColor.red, mcColor.green, mcColor.blue)
            if (distance < minDistance) {
                minDistance = distance
                closestColorIndex = i
            }
        }
        println("Mapped color (${color.red}, ${color.green}, ${color.blue}) to index $closestColorIndex")
        return closestColorIndex
    }

    private fun colorDistance(r1: Int, g1: Int, b1: Int, r2: Int, g2: Int, b2: Int): Double {
        val dr = (r1 - r2).toDouble()
        val dg = (g1 - g2).toDouble()
        val db = (b1 - b2).toDouble()
        return Math.sqrt(dr * dr + dg * dg + db * db)
    }

    private val shadeCoeffs = doubleArrayOf(180.0 / 255.0, 220.0 / 255.0, 255.0 / 255.0, 135.0 / 255.0)
}
