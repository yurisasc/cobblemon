package com.cablemc.pokemoncobbled.client.render.models.smd.loaders

import net.minecraft.resources.ResourceLocation


internal fun getParentPath(location: ResourceLocation): String {
    val locationPathArgs = location.path
        .split("/")

    return locationPathArgs
        .subList(0, locationPathArgs.size - 1)
        .reduce { acc, s -> "$acc/$s" }
}