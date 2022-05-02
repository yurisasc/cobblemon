package com.cablemc.pokemoncobbled.common.api.abilities.extensions

import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.Ability
import com.cablemc.pokemoncobbled.common.util.DataKeys
import net.minecraft.nbt.NbtCompound

internal class Test: Ability(template = Abilities.getOrException("flash_fire")) {

    var testValue = "TestValue"

    override fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putString(DataKeys.POKEMON_ABILITY_NAME, name)
        nbt.putString("Test", "TestValue")
        return nbt
    }

    override fun loadFromNBT(nbt: NbtCompound): Ability {
        testValue = nbt.getString("Test")
        return this
    }
}