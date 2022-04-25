package com.cablemc.pokemoncobbled.common.api.asset

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter

/**
 * A utility object for managing the "manifest" based assets. A JSON in a directory named _MANIFEST.json
 * is a list of all of the files in the folder that should be loaded.
 *
 * Rationale: On Forge this is completely unneeded, but the Fabric class loader is either bugged or
 * designed in such a way that targeting a folder for an asset load returns a URI that cannot be converted
 * into a path (which would then be used for file walking). As a result, arbitrarily loading all files in
 * a folder is either not possible in Fabric or I don't know what trickery is required. A manifest means there
 * will not be a blind searching for JSONs and in future enables us to make the manifest potentially compress
 * all the JSONs into itself to speed up load times internally.
 *
 * @author Hiroku
 * @since April 1st, 2022.
 */
object JsonManifestWalker {
    /**
     * Builds a manifest at [manifestPath] using the files inside the same folder.
     *
     * This is for internal use though we could make this more normalized later.
     */
    internal fun build(manifestPath: String) {
        val file = File(manifestPath)
        file.createNewFile()
        val folder = file.parentFile
        folder.mkdir()
        val members = folder.listFiles { f: File -> f.extension == "json" && f.nameWithoutExtension != "_MANIFEST" }
        val jsonArray = JsonArray()
        members.forEach {
            jsonArray.add(it.relativeTo(folder).toString())
        }
        val pw = PrintWriter(file)
        GsonBuilder().setPrettyPrinting().create().toJson(jsonArray, pw)
        pw.flush()
        pw.close()
    }

    /**
     * Loads the given manifest and all the files it references.
     */
    fun <T> load(clazz: Class<T>, folder: String, gson: Gson): List<T> {
        val manifestPath = "/assets/${PokemonCobbled.MODID}/$folder/_MANIFEST.json"
        val manifest = PokemonCobbled::class.java.getResourceAsStream(manifestPath)!!
        val folderPath = manifestPath.substringBeforeLast("/")
        val list = mutableListOf<T>()
        val array = gson.fromJson<JsonArray>(InputStreamReader(manifest))
        array.forEach {
            val path = it.asString
            val stream = PokemonCobbled.javaClass.getResourceAsStream("$folderPath/$path") ?: run {
                LOGGER.error("manifest contains element $path which was not found.")
                return@forEach
            }
            try {
                list.add(gson.fromJson(InputStreamReader(stream), clazz))
            } catch (exception: Exception) {
                LOGGER.error("Issue loading manifest component: $path")
                exception.printStackTrace()
            }
        }

        return list
    }
}