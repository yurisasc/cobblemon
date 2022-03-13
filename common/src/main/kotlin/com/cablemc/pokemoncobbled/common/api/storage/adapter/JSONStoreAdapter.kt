package com.cablemc.pokemoncobbled.common.api.storage.adapter

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.UUID

/**
 * A [FileStoreAdapter] for JSON files. This allows a [PokemonStore] to be serialized to a .json file. This is usually
 * slower and makes for a larger file per storage by several times compared to a [NBTStoreAdapter].
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
open class JSONStoreAdapter(
    override val rootFolder: String,
    override val useNestedFolders: Boolean,
    override val folderPerClass: Boolean,
    private val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
) : OneToOneFileStoreAdapter<JsonObject> {
    override val fileExtension: String = "json"
    override fun <E : StorePosition, T : PokemonStore<E>> serialize(store: T) = store.saveToJSON(JsonObject())

    override fun save(file: File, serialized: JsonObject) {
        val pw = PrintWriter(file)
        val json = gson.toJson(serialized)
        pw.write(json)
        pw.flush()
        pw.close()
    }

    override fun <E, T : PokemonStore<E>> load(file: File, storeClass: Class<out T>, uuid: UUID): T? {
        val br = BufferedReader(FileReader(file))
        val json = gson.fromJson<JsonObject>(br)
        val store = storeClass.getConstructor(UUID::class.java).newInstance(uuid)
        store.loadFromJSON(json)
        return store
    }
}