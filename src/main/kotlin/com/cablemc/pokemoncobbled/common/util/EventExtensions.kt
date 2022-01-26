package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraftforge.eventbus.api.Event

fun <T : Event> T.postAndThen(run: (T) -> Unit) {
    if (PokemonCobbledMod.EVENT_BUS.post(this)) return
    run.invoke(this)
}