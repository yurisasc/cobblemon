package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.pokemon.status.Statuses
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import net.minecraft.resources.ResourceLocation

class StatusUpdatePacket() : StringUpdatePacket() {
    constructor(pokemon: Pokemon, value: String): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun set(pokemon: Pokemon, value: String) {
        if (value.isEmpty()) {
            pokemon.status = null
        } else {
            val status = Statuses.getStatus(ResourceLocation(value))
            if (status != null && status is PersistentStatus) {
                pokemon.applyStatus(status)
            }
        }
    }
}