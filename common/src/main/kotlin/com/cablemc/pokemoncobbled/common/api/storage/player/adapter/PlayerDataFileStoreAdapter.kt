package com.cablemc.pokemoncobbled.common.api.storage.player.adapter

import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerData
import java.util.UUID

interface PlayerDataFileStoreAdapter {

    /** The root folder for these storages, such as "data/mystores/" */
    val rootFolder: String

    /**
     * Whether the stores in a folder should be nested within folders named after the first two characters of the UUID.
     * This makes it easier to access files on an FTP connection by drastically reducing the number of stores in a single
     * folder. For example, a store with UUID 380df991-f603-344c-a090-369bad2a924a would be located under the root folder
     * in {rootFolder}/38/{fileName}.
     */
    val useNestedFolders: Boolean

    /** The file extension, such as json or dat, that will be appended to the file. */
    val fileExtension: String

    fun load(uuid: UUID): PlayerData

    fun save(playerData: PlayerData)

}