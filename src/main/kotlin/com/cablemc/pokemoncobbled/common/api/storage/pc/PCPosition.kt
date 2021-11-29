package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag

class PCPosition(val box: Int, val slot: Int) : StorePosition {
    override fun toString() = "{box=$box,slot=$slot}"
}