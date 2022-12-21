package com.cobblemon.mod.common.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.io.path.nameWithoutExtension

internal class FormScrapper {

    @Test
    fun scrap() {
        val root = Paths.get("src")
            .resolve("main")
            .resolve("resources")
            .resolve("data")
            .resolve("cobblemon")
            .resolve("species")
            .absolute()

        val target = root.parent
            .resolve("rewrites")

        val gson = GsonBuilder().setPrettyPrinting().create()

        val species = target.resolve("species")
        val forms = target.resolve("forms")

        for (directory in root.toFile().list { dir, name -> Files.isDirectory(root.resolve(name))}) {
            val d = root.resolve(directory)
            Files.walk(d).use { stream ->
                stream.filter{ !it.equals(d) }.forEach {
                    val name = it.fileName.nameWithoutExtension
                    val json = gson.fromJson(FileReader(it.toFile()), JsonObject::class.java)

                    val rewrite = JsonObject()
                    json.keySet().stream()
                        .filter { key -> !key.equals("forms") && !key.equals("evolutions")}
                        .forEach { key ->
                            rewrite.add(key, json.get(key))
                        }

                    var writer = FileWriter(this.createFile(species.resolve(directory).resolve("$name.json")))
                    writer.use {
                        writer.write(gson.toJson(rewrite))
                    }

                    val formList = json.getAsJsonArray("forms")
                    for (child in formList) {
                        val obj = child as JsonObject
                        val formName = obj.get("name").asString

                        writer = FileWriter(this.createFile(forms.resolve(directory).resolve(name).resolve("$formName.json")))
                        writer.use {
                            writer.write(gson.toJson(child))
                        }
                    }
                }
            }
        }
    }

    private fun createFile(target: Path): File {
        if(!Files.exists(target)) {
            Files.createDirectories(target.parent)
            Files.createFile(target)
        }

        return target.toFile()
    }

}