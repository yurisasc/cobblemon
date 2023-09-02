package com.cobblemon.mod.common.client

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.texture.SpriteAtlasHolder
import net.minecraft.client.texture.TextureManager

class CobblemonBerryAtlas(textureManager: TextureManager) : SpriteAtlasHolder(
    textureManager,
    cobblemonResource("textures/atlas/berries.png"),
    cobblemonResource("berries")
) {
}