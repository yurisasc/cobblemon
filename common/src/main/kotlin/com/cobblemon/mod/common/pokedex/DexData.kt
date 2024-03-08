package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.ClientDataSynchronizer
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class DexData : ClientDataSynchronizer<DexData> {

    var identifier : Identifier = cobblemonResource("dex")
    var subdexes : MutableList<DexData> = mutableListOf()
    var orderedPokemonList : MutableList<DexPokemonData> = mutableListOf()

    override fun shouldSynchronize(other: DexData): Boolean {
        return other.identifier != identifier || other.subdexes != subdexes || other.orderedPokemonList != orderedPokemonList
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(identifier)
        buffer.writeInt(subdexes.size)
        subdexes.forEach {
            it.encode(buffer)
        }
        buffer.writeInt(orderedPokemonList.size)
        orderedPokemonList.forEach {
            it.encode(buffer)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        identifier = buffer.readIdentifier()
        val subdexesSize = buffer.readInt()
        for (i in 0 until subdexesSize){
            val decodedDex = DexData()
            decodedDex.decode(buffer)
            subdexes.add(decodedDex)
        }
        val orderedPokemonListSize = buffer.readInt()
        for (i in 0 until orderedPokemonListSize){
            val decodedOrderedPokemon = DexPokemonData()
            decodedOrderedPokemon.decode(buffer)
            orderedPokemonList.add(decodedOrderedPokemon)
        }
    }
}