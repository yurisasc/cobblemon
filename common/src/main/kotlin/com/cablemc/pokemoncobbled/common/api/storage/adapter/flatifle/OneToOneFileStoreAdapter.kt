package com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import java.io.File
import java.util.UUID

/**
 * A subset of [FileStoreAdapter] that make predictable use of files based on the implementation of [rootFolder],
 * [useNestedFolders], [folderPerClass], and [fileExtension].
 *
 * @param S The serialized form of a storage. This is what will be constructed synchronously, while the saving
 *          may occur asynchronously.
 *
 * @author Hiroku
 * @since November 30th, 2021
 */
interface OneToOneFileStoreAdapter<S> : FileStoreAdapter<S> {
    /** The root folder for these storages, such as "data/mystores/" */
    val rootFolder: String
    /**
     * Whether the stores in a folder should be nested within folders named after the first two characters of the UUID.
     * This makes it easier to access files on an FTP connection by drastically reducing the number of stores in a single
     * folder. For example, a store with UUID 380df991-f603-344c-a090-369bad2a924a would be located under the root folder
     * in {rootFolder}/38/{fileName}.
     */
    val useNestedFolders: Boolean
    /**
     * Whether different types of store will be saved in different folders beneath the root. If this is false, stores
     * will save with a suffix denoting what class of store they are so that they can be differentiated.
     */
    val folderPerClass: Boolean
    /** The file extension, such as json or dat, that will be appended to the file. */
    val fileExtension: String

    fun save(file: File, serialized: S)
    fun <E, T : PokemonStore<E>> load(file: File, storeClass: Class<out T>, uuid: UUID): T?
    fun getFile(storeClass: Class<out PokemonStore<*>>, uuid: UUID): File {
        val className = storeClass.simpleName.lowercase()
        val subfolder1 = if (folderPerClass) "$className/" else ""
        val subfolder2 = if (useNestedFolders) "${uuid.toString().substring(0, 2)}/" else ""
        val folder = if (!rootFolder.endsWith("/")) "$rootFolder/" else rootFolder
        val fileName = if (folderPerClass) "$uuid.$fileExtension" else "$uuid-$className.$fileExtension"
        val file = File(folder + subfolder1 + subfolder2, fileName)
        file.parentFile.mkdirs()
        return file
    }

    override fun save(storeClass: Class<out PokemonStore<*>>, uuid: UUID, serialized: S) {
        val file = getFile(storeClass, uuid)
        val tempFile = File(file.absolutePath + ".temp")
        tempFile.createNewFile()
        save(tempFile, serialized)
        tempFile.copyTo(file, overwrite = true)
        tempFile.delete()
    }

    override fun <E : StorePosition, T : PokemonStore<E>> load(storeClass: Class<T>, uuid: UUID): T? {
        val file = getFile(storeClass, uuid)
        val tempFile = File(file.absolutePath + ".temp")
        if (tempFile.exists()) {
            try {
                val tempLoaded = load(tempFile, storeClass, uuid)
                if (tempLoaded != null) {
                    return tempLoaded
                }
            } finally {
                tempFile.delete()
            }
        }

        return if (file.exists()) {
            load(file, storeClass, uuid)
        } else {
            null
        }
    }
}