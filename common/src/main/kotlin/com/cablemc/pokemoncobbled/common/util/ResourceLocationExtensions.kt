package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import net.minecraft.util.Identifier

fun Identifier.extractTo(directory : File) {
    val stream = PokemonCobbled::class.java.getResourceAsStream(String.format("/assets/%s/%s", namespace, path))
        ?: throw Exception("Could not read $this")
    Files.copy(stream, directory.toPath(), StandardCopyOption.REPLACE_EXISTING)
    stream.close()
}

/**
 * Creates an identifier from this string, if a namespace is not present the default [namespace] will be used.
 *
 * @param namespace The namespace that will default if none is present, defaults to [PokemonCobbled.MODID].
 */
fun String.asIdentifierDefaultingNamespace(namespace: String = PokemonCobbled.MODID): Identifier {
    val id = this.lowercase()
    val data = arrayOf(namespace, id)
    val i = data.indexOf(":")
    return if (id.contains(":")) Identifier(id.substringBefore(":"), id.substringAfter(":")) else Identifier(namespace, id)
}