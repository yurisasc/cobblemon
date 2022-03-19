package com.cablemc.pokemoncobbled.common.entity.player

import net.minecraft.nbt.CompoundTag

interface IShoulderable {

    fun changeShoulderEntityLeft(compoundTag: CompoundTag)

    fun changeShoulderEntityRight(compoundTag: CompoundTag)

}