package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import net.minecraft.nbt.CompoundTag

fun CompoundTag.isPokemonEntity() : Boolean {
    return this.getString("id").equals(EntityRegistry.POKEMON.id.toString())
}