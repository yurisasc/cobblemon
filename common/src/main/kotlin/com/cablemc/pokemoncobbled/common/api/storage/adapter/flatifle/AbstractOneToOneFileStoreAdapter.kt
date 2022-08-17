package com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.adapter.CobbledAdapter
import java.util.UUID

abstract class AbstractOneToOneFileStoreAdapter<S>(
    override val rootFolder: String,
    override val useNestedFolders: Boolean,
    override val folderPerClass: Boolean,
    override val fileExtension: String
) : OneToOneFileStoreAdapter<S> {

    private val _children: MutableList<CobbledAdapter<S>> = mutableListOf()
    override fun children(): MutableList<CobbledAdapter<S>> = this._children
    override fun with(vararg children: CobbledAdapter<S>): AbstractOneToOneFileStoreAdapter<S> {
        this._children.addAll(children)
        return this
    }

    override fun <E : StorePosition, T : PokemonStore<E>> load(storeClass: Class<T>, uuid: UUID): T? {
        val result = super.load(storeClass, uuid)
        if (result == null) {
            for (child in this.children()) {
                child.load(storeClass, uuid).let { return@load it }
            }
        }

        return null
    }
}