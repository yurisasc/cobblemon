package com.cablemc.pokemoncobbled.common.util

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.ItemStack

fun ItemStack.saveToJson(): JsonElement = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.save(CompoundTag()))