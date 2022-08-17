package com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import java.io.File
import java.util.UUID

/**
 * A [OneToOneFileStoreAdapter] that can arbitrarily save a single [PokemonStore] into an [NbtCompound] with the
 * help of Minecraft's [NbtIo]. This is arguably the best persistence method for [PokemonStore]s and is absolutely
 * the most efficient [FileStoreAdapter].
 *
 * @author Hiroku
 * @since November 30th, 2021
 */
open class NBTStoreAdapter(
    override val rootFolder: String,
    override val useNestedFolders: Boolean,
    override val folderPerClass: Boolean,
) : AbstractOneToOneFileStoreAdapter<NbtCompound>(rootFolder, useNestedFolders, folderPerClass, "dat") {
    override fun <E : StorePosition, T : PokemonStore<E>> serialize(store: T) = store.saveToNBT(NbtCompound())
    override fun save(file: File, serialized: NbtCompound) = NbtIo.writeCompressed(serialized, file)
    override fun <E, T : PokemonStore<E>> load(file: File, storeClass: Class<out T>, uuid: UUID): T? {
        val store = storeClass.getConstructor(UUID::class.java).newInstance(uuid)
        store.loadFromNBT(NbtIo.readCompressed(file))
        return store
    }
}