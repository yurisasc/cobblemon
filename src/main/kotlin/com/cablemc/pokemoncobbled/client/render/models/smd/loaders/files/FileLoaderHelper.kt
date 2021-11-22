package com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import net.minecraft.resources.ResourceLocation
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

internal fun readLinesFromResource(location: ResourceLocation): List<String> {
    getResourceStream(location).use { stream ->
        stream.bufferedReader(Charsets.UTF_8).use { reader ->
            return reader.readLines()
        }
    }
}

private fun getResourceStream(location: ResourceLocation): InputStream {
    val namespace = location.namespace
    val path = location.path
    // TODO Use the 'generated' thing, data packs, etc.
    return PokemonCobbledClient::class.java.getResourceAsStream("/assets/$namespace/$path")!!
}

private val smdLineRegex: Pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*")

internal fun String.splitSmdValues(): List<String> {
    val matchList = mutableListOf<String>()
    val regexMatcher: Matcher = smdLineRegex.matcher(this)
    while (regexMatcher.find()) {
        matchList.add(regexMatcher.group(0).replace("\"", ""))
    }
    return matchList.filter { it.isNotBlank() }.map { it.trim() }
}