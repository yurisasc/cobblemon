package com.cablemc.pokemoncobbled.common.util

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps

fun ItemStack.saveToJson(): JsonElement = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.writeNbt(NbtCompound()))