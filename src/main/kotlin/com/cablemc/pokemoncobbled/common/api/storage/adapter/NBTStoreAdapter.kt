package com.cablemc.pokemoncobbled.common.api.storage.adapter

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.UUID

class NBTStoreAdapter(
    override val rootFolder: String,
    override val useNestedFolders: Boolean,
    override val folderPerClass: Boolean,
    override val fileExtension: String
) : OneToOneFileStoreAdapter<CompoundTag> {
    override fun <E : StorePosition, T : PokemonStore<E>> serialize(store: T) = store.saveToNBT(CompoundTag())

    override fun <E, T : PokemonStore<E>> load(file: File, storeClass: Class<out T>, uuid: UUID): T? {
        val stream = InputStreamReader(FileInputStream(file))
        val str = stream.readText()
        stream.close()
        val nbt = TagParser.parseTag(str)
        val store = storeClass.getConstructor(UUID::class.java).newInstance(uuid)
        store.loadFromNBT(nbt)
        return store
    }

    override fun save(file: File, serialized: CompoundTag) {
        DataOutputStream(FileOutputStream(file)).use { dataStream -> serialized.write(dataStream) }
    }
}