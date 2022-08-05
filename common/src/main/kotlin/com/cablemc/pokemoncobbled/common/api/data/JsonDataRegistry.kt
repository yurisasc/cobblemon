package com.cablemc.pokemoncobbled.common.api.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * A [DataRegistry] that consumes JSON files.
 * Every deserialized instance is attached to an [Identifier].
 * For example a file under data/mymod/[resourcePath]/entry.json would be backed by the identifier modid:entry.
 *
 * @param T The type of the [DataRegistry].
 * @param D The type of the data consumed by this registry.
 *
 * @author Licious
 * @since August 5th, 2022
 */
interface JsonDataRegistry<T, D> : DataRegistry<T> {

    /**
     * The [Gson] used to deserialize the data this registry will consume.
     */
    val gson: Gson

    /**
     * The [TypeToken] of type [D].
     */
    val typeToken: TypeToken<D>

    override fun reload(manager: ResourceManager) {
        val data = hashMapOf<Identifier, D>()
        manager.findResources(this.resourcePath.pathString) { path -> path.endsWith(JSON_EXTENSION) }.forEach { identifier ->
            manager.getResource(identifier).use { resource ->
                resource.inputStream.use { stream ->
                    stream.bufferedReader().use { reader ->
                        val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                        data[resolvedIdentifier] = this.gson.fromJson(reader, this.typeToken.type)
                    }
                }
            }
        }
        this.reload(data)
    }

    /**
     * Reloads this registry from the deserialized data.
     *
     * @param data A map of the data associating an instance to the respective identifier from the [ResourceManager].
     */
    fun reload(data: Map<Identifier, D>)

    companion object {

        private const val JSON_EXTENSION = ".json"

    }

}