package com.cablemc.pokemoncobbled.common.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.POKEMON_PER_BOX
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import java.util.UUID

class ClientPC(uuid: UUID, boxCount: Int) : ClientStorage<PCPosition>(uuid) {
    val boxes = MutableList(boxCount) { ClientBox() }
    override fun findByUUID(uuid: UUID): Pokemon? {
        for (box in boxes) {
            for (pokemon in box) {
                if (pokemon?.uuid == uuid) {
                    return pokemon
                }
            }
        }

        return null
    }

    override fun set(position: PCPosition, pokemon: Pokemon?) {
        val box = if (boxes.size > position.box) boxes[position.box] else return
        if (position.slot >= POKEMON_PER_BOX) {
            return
        }
        box.slots[position.slot] = pokemon
    }

    override fun get(position: PCPosition): Pokemon? {
        if (position.slot >= POKEMON_PER_BOX || position.box >= boxes.size) {
            return null
        }
        return boxes[position.box].slots[position.slot]
    }

    override fun getPosition(pokemon: Pokemon): PCPosition? {
        for (boxNumber in boxes.indices) {
            val box = boxes[boxNumber]
            for (slotNumber in box.slots.indices) {
                if (box.slots[slotNumber] == pokemon) {
                    return PCPosition(boxNumber, slotNumber)
                }
            }
        }
        return null
    }
}