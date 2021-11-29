package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag

class PartyPosition(val slot: Int) : StorePosition {
    override fun toString() = "{slot=$slot}"
}