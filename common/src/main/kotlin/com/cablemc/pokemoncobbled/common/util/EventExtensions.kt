package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.forge.mod.PokemonCobbledMod
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.IEventBus

fun <T : Event> T.postAndThen(eventBus: IEventBus = PokemonCobbledMod.EVENT_BUS, run: (T) -> Unit) {
    if (eventBus.post(this)) return
    run(this)
}