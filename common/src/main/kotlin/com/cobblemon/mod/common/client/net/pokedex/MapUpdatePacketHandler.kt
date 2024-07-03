package com.cobblemon.mod.common.client.net.pokedex

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.server.pokedex.MapUpdatePacket
import net.minecraft.block.MapColor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
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
        val image = ImageIO.read(imageBytes.inputStream())
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
                }
                val mapState = MapState.fromNbt(nbt, world.registryManager)

                val resizedImage = convertToBufferedImage(image.getScaledInstance(128, 128, Image.SCALE_DEFAULT))
                val pixels = convertPixelArray(resizedImage)
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

    private fun nearestColor(mapColors: Array<MapColor>, color: Color): Int {
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
    }

    private val shadeCoeffs = doubleArrayOf(180.0 / 255.0, 220.0 / 255.0, 255.0 / 255.0, 135.0 / 255.0)
}
