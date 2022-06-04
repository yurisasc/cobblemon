package com.cablemc.pokemoncobbled.common.util

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

                // Verify the path to protect from "zip slip" exploit
                val newPath = checkPath(zipEntry, target)

                if (!zipEntry.isDirectory) {
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
        val targetDirResolved = targetDir.resolve(zipEntry.name)
        val normalizePath = targetDirResolved.normalize().toAbsolutePath()
        val targetDirPath = targetDir.normalize().toAbsolutePath()
        if (!normalizePath.startsWith(targetDirPath)) {
            throw IOException("Bad zip entry: " + zipEntry.name)
        }
        return normalizePath
    }

}