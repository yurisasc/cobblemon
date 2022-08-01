package com.cablemc.pokemoncobbled.common.api.data

import com.cablemc.pokemoncobbled.common.data.CobbledSynchronousJsonResourceReloader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * A [SynchronousResourceReloader] backed by [Gson].
 * Every deserialized item is attached to an [Identifier].
 * A for example a file under data/mymod/[resourcePath]/entry.json would be backed by the identifier modid:entry.
 *
 * @param T The type being deserialized.
 */
interface SynchronousJsonResourceReloader<T> : SynchronousResourceReloader {

    /**
     * The [Gson] used to deserialize the raw data.
     */
    val gson: Gson

    /**
     * The [Path] for the resource.
     */
    val resourcePath: Path

    /**
     * The [TypeToken] of type [T].
     */
    val typeToken: TypeToken<T>

    override fun reload(manager: ResourceManager) {
        val data = hashMapOf<Identifier, T>()
        manager.findResources(this.resourcePath.pathString) { path -> path.endsWith(JSON_EXTENSION) }.forEach { identifier ->
            manager.getResource(identifier).use { resource ->
                resource.inputStream.use { stream ->
                    stream.bufferedReader().use { reader ->
                        val resolvedIdentifier = Identifier(identifier.namespace, identifier.path.substringBefore(JSON_EXTENSION))
                        data[resolvedIdentifier] = this.gson.fromJson(reader, this.typeToken.type)
                    }
                }
            }
        }
        this.processData(data)
    }

    fun processData(data: Map<Identifier, T>)

    companion object {

        private const val JSON_EXTENSION = ".json"

        fun <T> create(gson: Gson, resourcePath: Path, typeToken: TypeToken<T>, dataConsumer: (data: Map<Identifier, T>) -> Unit): SynchronousJsonResourceReloader<T> = CobbledSynchronousJsonResourceReloader(gson, resourcePath, typeToken, dataConsumer)

    }

}