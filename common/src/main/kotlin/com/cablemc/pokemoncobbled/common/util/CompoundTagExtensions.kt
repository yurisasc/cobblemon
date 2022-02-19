package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.CobbledEntities.POKEMON
import net.minecraft.nbt.CompoundTag

fun CompoundTag.isPokemonEntity() : Boolean {
    return this.getString("id").equals(POKEMON.id.toString())
}