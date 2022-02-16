package com.cablemc.pokemoncobbled.forge.common.util

import com.cablemc.pokemoncobbled.forge.common.entity.EntityRegistry
import net.minecraft.nbt.CompoundTag

fun CompoundTag.isPokemonEntity() : Boolean {
    return this.getString("id").equals(EntityRegistry.POKEMON.id.toString())
}