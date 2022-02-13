package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.FileVisitResult.SKIP_SUBTREE
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.toPath

/**
 * Basic functions for dealing with assets inside the mod, using the standard file visitor
 * strategy.
 *
 * @author Hiroku
 * @since February 10th, 2022
 */
object AssetLoading {
    fun ResourceLocation.toPath() = toURL()?.toPath()
    fun ResourceLocation.toURL() = PokemonCobbled::class.java.getResource(String.format("/assets/%s/%s", namespace, path))?.toURI()
    fun fileSearch(dir: Path, filter: (Path) -> Boolean, recursive: Boolean): List<Path> {
        val files = mutableListOf<Path>()
        Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, fileAttributes: BasicFileAttributes): FileVisitResult {
                if (filter(file)) {
                    files.add(file)
                }
                return when (recursive) {
                    true -> CONTINUE
                    false -> SKIP_SUBTREE
                }
            }
        })
        return files
    }

    fun searchFor(dir: String, suffix: String, list: MutableList<File>) {
        val file = File(dir)
        val ls = file.list() ?: return
        for (name in ls) {
            val subFile = File("$dir/$name")
            if (subFile.isFile && name.endsWith(suffix)) {
                list.add(subFile)
            } else if (subFile.isDirectory) {
                searchFor(dir = "$dir/$name", suffix = suffix, list = list)
            }
        }
    }
}