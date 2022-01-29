package com.cablemc.pokemoncobbled.common.util

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.nio.file.Files

fun ResourceLocation.extractTo(directory : File) {
    ifServer {

    }
    ifClient {
        val resource = Minecraft.getInstance().resourceManager.getResources(this).firstOrNull()
        if (resource != null) {
            Files.copy(resource.inputStream, directory.toPath())
        }
    }
}