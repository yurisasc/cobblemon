package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.util.Identifier
import java.io.File
import java.nio.file.Files

fun Identifier.extractTo(directory : File) {
    val stream = PokemonCobbled::class.java.getResourceAsStream(String.format("/assets/%s/%s", namespace, path))
        ?: throw Exception("Could not read $this")
    Files.copy(stream, directory.toPath())
}