/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.animation

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import kotlin.io.path.Path
import kotlin.io.path.name

internal class ValidateAnimations {

    @Test
    fun `validate animation configurations`() {
        val sourceName = Pattern.compile("(?<name>[A-Za-z-0-9]+)Model[.]kt")
        val inSource = Pattern.compile("(// *.*)?bedrock[A-Za-z(\",]+ \"(?<name>[a-z_-]+)\"[)]")
        val assetName = Pattern.compile("(?<name>[a-z-]+)[.]animation[.]json")
        val inAsset = Pattern.compile("animation[.][a-z]+[.](?<name>[a-z0-9_-]+)")

        val root = Path("src").resolve("main")

        val assets = root.resolve("resources")
            .resolve("assets")
            .resolve("cobblemon")
            .resolve("bedrock")
            .resolve("pokemon")
            .resolve("animations")

        val source = root.resolve("kotlin")
            .resolve("com")
            .resolve("cobblemon")
            .resolve("mod")
            .resolve("common")
            .resolve("client")
            .resolve("render")
            .resolve("models")
            .resolve("blockbench")
            .resolve("pokemon")

        val cache = mutableMapOf<String, PathResolver>()

        Files.walk(source).use {
            it.filter { it.parent.fileName.toString().contains("gen") }
                .forEach {
                    val matcher = sourceName.matcher(it.name)
                    matcher.matches()

                    val resolver = PathResolver()
                    resolver.source = it
                    cache[matcher.group("name").lowercase()] = resolver
                }
        }

        Files.walk(assets).use {
            it.forEach {
                val matcher = assetName.matcher(it.name)
                if(matcher.matches()) {
                    val resolver = cache[matcher.group("name")]!!
                    resolver.animation = it
                }
            }
        }

        cache.entries.forEach { entry ->
            var reader = BufferedReader(FileReader(entry.value.source.toFile()))
            val source: String
            reader.use {
                val ignore = AtomicBoolean(false)
                source = reader.lines()
                    .filter {
                        var result: Boolean = ignore.get()
                        if(it.trim().startsWith("/*")) {
                            ignore.set(true)
                            result = true
                        } else if(it.trim().contains("*/")) {
                            result = ignore.getAndSet(false)
                        }

                        !result
                    }
                    .reduce { a, b -> a.plus('\n').plus(b) }
                    .get()
            }

            val options = mutableSetOf<String>()
            val sourceMatcher = inSource.matcher(source)
            while(sourceMatcher.find()) {
                if(sourceMatcher.group(1) == null) {
                    options.add(sourceMatcher.group("name"))
                }
            }

            if(entry.value.animation != null) {
                reader = BufferedReader(FileReader(entry.value.animation!!.toFile()))
                reader.use {
                    var animations = GsonBuilder().create().fromJson(reader, JsonObject::class.java)
                    animations = animations.get("animations").asJsonObject
                    animations.keySet().forEach {
                        val matcher = inAsset.matcher(it)
                        if(matcher.matches()) {
                            matcher.matches()

                            val name = matcher.group("name")
                            if (options.contains(name)) {
                                options.remove(name)
                            }
                        } else {
                            println("Ambiguous pose for ${entry.key}: $it, ignoring...")
                        }
                    }

                    assertEquals(0, options.size, "Invalid animations found for pokemon: ${entry.key} $options")
                }
            }
        }
    }

    private class PathResolver {
        lateinit var source: Path
        var animation: Path? = null
    }

}