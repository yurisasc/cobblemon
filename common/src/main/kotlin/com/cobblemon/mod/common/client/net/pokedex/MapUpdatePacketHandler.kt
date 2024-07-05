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
        val pictureEdgeTrim = 8
    //    println("Received image bytes: ${imageBytes.size}")
        val image = ImageIO.read(imageBytes.inputStream())
        if (image == null) {
            player.sendMessage(Text.literal("Failed to read image from bytes"), false)
            return
        }
    //    println("Image dimensions: ${image.width}x${image.height}")

        // Save the received image to file for debugging
        val receivedImageFile = File("received_image.png")
        ImageIO.write(convertToBufferedImage(image), "png", receivedImageFile)
    //    println("Saved received image to ${receivedImageFile.absolutePath}")

        // Resize the image to 128x128
        val resizedImage = convertToBufferedImage(image.getScaledInstance(128, 128, Image.SCALE_SMOOTH))

        // Save the resized image to file for debugging
        val resizedImageFile = File("resized_image.png")
        ImageIO.write(resizedImage, "png", resizedImageFile)
    //    println("Saved resized image to ${resizedImageFile.absolutePath}")

        // Convert the resized image to a pixel array and trim the edges
        val pixels = trimPixelArray(convertPixelArray(resizedImage), pictureEdgeTrim)
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

                for (x in 0 until 128 - 2 * pictureEdgeTrim) {
                    for (y in 0 until 128 - 2 * pictureEdgeTrim) {
                        val color = Color(pixels[y][x], true)
                        val nearestColor = nearestColor(mapColors, color)
                        mapState.colors[x + pictureEdgeTrim + (y + pictureEdgeTrim) * 128] = nearestColor.toByte()
    //                    println("Processed pixel at ($x, $y): original color = (${color.red}, ${color.green}, ${color.blue}), nearest map color index = $nearestColor")
                    }
                }

                world.putMapState(MapIdComponent(mapId), mapState)
                val mapIdComponent = MapIdComponent(mapId)
                mapStack.set(DataComponentTypes.MAP_ID, mapIdComponent)

                inventory.setStack(i, mapStack)
                player.sendMessage(Text.literal("SnapPicture: Map updated with screenshot"), true)
    //            println("Map updated successfully with mapId: $mapId")
                return
            }
        }
        player.sendMessage(Text.literal("No empty map found in inventory"), true)
        println("No empty map found in inventory")
    }

    private fun trimPixelArray(pixels: Array<IntArray>, trim: Int): Array<IntArray> {
        val trimmedWidth = pixels[0].size - 2 * trim
        val trimmedHeight = pixels.size - 2 * trim
        val trimmedPixels = Array(trimmedHeight) { IntArray(trimmedWidth) }

        for (y in 0 until trimmedHeight) {
            for (x in 0 until trimmedWidth) {
                trimmedPixels[y][x] = pixels[y + trim][x + trim]
            }
        }

        return trimmedPixels
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

        // Check if the color is close to black
        if (color.red < 20 && color.green < 20 && color.blue < 20) {
            return mapColors.indexOfFirst { it.red < 20 && it.green < 20 && it.blue < 20 }
        }

        for (i in mapColors.indices) {
            val mcColor = mapColors[i]

            // Skip transparent colors
            if (mcColor.alpha == 0) continue

            val distance = colorDistance(color.red, color.green, color.blue, mcColor.red, mcColor.green, mcColor.blue)
            if (distance < minDistance) {
                minDistance = distance
                closestColorIndex = i
            }
        }

        // If the chosen color is transparent, set it to black
        if (mapColors[closestColorIndex].alpha == 0) {
            closestColorIndex = mapColors.indexOfFirst { it.red == 0 && it.green == 0 && it.blue == 0 }
        }

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
