package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys
import java.util.UUID
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos

class PokemonTetherBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.POKEMON_TETHER, pos, state) {
    class Tethering(val tetheringId: UUID, val pokemonId: UUID, val pcId: UUID) {
        fun getPokemon() = Cobblemon.storage.getPC(pcId)[pokemonId]
    }

    companion object {
        internal val TICKER = BlockEntityTicker<PokemonTetherBlockEntity> { world, _, _, blockEntity ->
            blockEntity.ticksUntilCheck--
            if (blockEntity.ticksUntilCheck <= 0) {
                blockEntity.checkPokemon()
            }
        }
    }

    var ticksUntilCheck = Cobblemon.config.pastureBlockUpdateTicks
    private val tetheredPokemon = mutableListOf<Tethering>()
    private var maxTetheredPokemon: Short? = null

    fun getMaxTethered() = maxTetheredPokemon ?: Cobblemon.config.defaultPasturedPokemonLimit

    fun tether(pokemon: Pokemon) {

    }

    fun checkPokemon() {
        val deadLinks = mutableListOf<Tethering>()
        tetheredPokemon.forEach {
            val pokemon = it.getPokemon()
            if (pokemon == null) {
                deadLinks.add(it)
            } else if (pokemon.tetheringId == null || pokemon.tetheringId != it.tetheringId) {
                deadLinks.add(it)
            }
        }
        tetheredPokemon.removeAll(deadLinks)
        markDirty()
        ticksUntilCheck = Cobblemon.config.pastureBlockUpdateTicks
    }

    fun releasePokemon(pokemonId: UUID) {
        val tethering = tetheredPokemon.find { it.pokemonId == pokemonId } ?: return
        tethering.getPokemon()?.tetheringId = null
        markDirty()
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains(DataKeys.TETHER_MAX)) {
            maxTetheredPokemon = nbt.getShort(DataKeys.TETHER_MAX)
        }
        val list = nbt.getList(DataKeys.TETHER_POKEMON, NbtCompound.COMPOUND_TYPE.toInt())
        for (tetheringNBT in list) {
            tetheringNBT as NbtCompound
            val tetheringId = tetheringNBT.getUuid(DataKeys.TETHERING_ID)
            val pokemonId = tetheringNBT.getUuid(DataKeys.POKEMON_UUID)
            val pcId = tetheringNBT.getUuid(DataKeys.PC_ID)
            tetheredPokemon.add(Tethering(tetheringId = tetheringId, pokemonId = pokemonId, pcId = pcId))
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        maxTetheredPokemon?.let { nbt.putShort(DataKeys.TETHER_MAX, it) }
        val list = NbtList()
        for (tethering in tetheredPokemon) {
            val tetheringNBT = NbtCompound()
            tetheringNBT.putUuid(DataKeys.TETHERING_ID, tethering.tetheringId)
            tetheringNBT.putUuid(DataKeys.POKEMON_UUID, tethering.pokemonId)
            tetheringNBT.putUuid(DataKeys.PC_ID, tethering.pcId)
            list.add(tetheringNBT)
        }
        nbt.put(DataKeys.TETHER_POKEMON, list)
    }
}