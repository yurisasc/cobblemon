package com.cablemc.pokemoncobbled.common.util

import net.minecraft.resources.ResourceLocation
import java.io.File
import java.net.URI
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

/**
 * Basic functions for dealing with assets inside the mod, using the standard file visitor
 * strategy.
 *
 * @author Hiroku
 * @since February 10th, 2022
 */
object AssetLoading {
    fun ResourceLocation.toPath() = toURL()?.toPath()
    fun ResourceLocation.toURL() = javaClass.getResource(String.format("/assets/%s/%s", namespace, path))?.toURI()
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

    fun URI.toPath(): Path? {
        when (scheme) {
            "file" -> return Paths.get(this)
            "jar" -> {
                val uriEncoded = URLEncoder.encode(toString(), UTF_8.name())
                val separator = uriEncoded.indexOf("!/")
                val entryName = uriEncoded.substring(separator + 2)
                val fileURI = URI.create(uriEncoded.substring(0, separator))
                val fs = FileSystems.newFileSystem(fileURI, emptyMap<String, Any>())
                return try {
                    FileSystems.getFileSystem(fileURI).takeIf { it.isOpen }?.getPath(entryName) ?: throw Exception()
                } catch(e : Exception) {
                    fs.getPath(entryName)
                }
            }
            else -> return null
        }
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