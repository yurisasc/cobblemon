package com.cablemc.pokemoncobbled.common.util

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUtils {

    /**
     * Unzips a file to the target path
     */
    fun unzipFile(source: Path, target: Path) {
        ZipInputStream(FileInputStream(source.toFile())).use { zis ->
            var zipEntry = zis.nextEntry

            // While the zip has more entries in it
            while (zipEntry != null) {
                var isDirectory = false

                // Check if the entry is a directory
                if (zipEntry.name.endsWith(File.separator)) {
                    isDirectory = true
                }

                // Verify the path to protect from "zip slip" exploit
                val newPath: Path = checkPath(zipEntry, target)

                if (!isDirectory) {
                    // Check if a parent directory needs to be created
                    if (newPath.parent != null) {
                        if (Files.notExists(newPath.parent)) {
                            Files.createDirectories(newPath.parent)
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Files.createDirectories(newPath)
                }
                // Next entry
                zipEntry = zis.nextEntry
            }
            zis.closeEntry()
        }
    }

    private fun checkPath(zipEntry: ZipEntry, targetDir: Path): Path {
        val targetDirResolved: Path = targetDir.resolve(zipEntry.name)
        val normalizePath: Path = targetDirResolved.normalize()
        if (!normalizePath.startsWith(targetDir)) {
            throw IOException("Bad zip entry: " + zipEntry.name)
        }
        return normalizePath
    }

}