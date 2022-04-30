package com.cablemc.pokemoncobbled.common.entity.player

import net.minecraft.nbt.NbtCompound

interface IShoulderable {

    fun changeShoulderEntityLeft(compoundTag: NbtCompound)

    fun changeShoulderEntityRight(compoundTag: NbtCompound)

}