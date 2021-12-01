package com.cablemc.pokemoncobbled.common.api.storage.adapter

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import java.io.File
import java.util.UUID

interface OneToOneFileStoreAdapter<S> : FileStoreAdapter<S> {
    val rootFolder: String
    val useNestedFolders: Boolean
    val folderPerClass: Boolean
    val fileExtension: String

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
        file.createNewFile()
        save(file, serialized)
    }

    override fun <E : StorePosition, T : PokemonStore<E>> load(storeClass: Class<T>, uuid: UUID): T? {
        val file = getFile(storeClass, uuid)
        return if (file.exists()) {
            load(file, storeClass, uuid)
        } else {
            null
        }
    }

    fun save(file: File, serialized: S)
    fun <E, T : PokemonStore<E>> load(file: File, storeClass: Class<out T>, uuid: UUID): T?
}