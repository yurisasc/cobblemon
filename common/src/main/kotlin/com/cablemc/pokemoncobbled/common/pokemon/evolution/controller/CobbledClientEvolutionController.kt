package com.cablemc.pokemoncobbled.common.pokemon.evolution.controller

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution.AcceptEvolutionPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.JsonElement
import net.minecraft.nbt.NbtElement

internal class CobbledClientEvolutionController(override val pokemon: Pokemon) : EvolutionController<EvolutionDisplay> {

    private val evolutions = hashSetOf<EvolutionDisplay>()

    override val size: Int
        get() = this.evolutions.size

    override fun start(evolution: EvolutionDisplay) {
        sendToServer(AcceptEvolutionPacket(this.pokemon, evolution))
    }

    override fun saveToNBT(): NbtElement {
        throw UnsupportedOperationException(UOE_MESSAGE)
    }

    override fun loadFromNBT(nbt: NbtElement) {
        throw UnsupportedOperationException(UOE_MESSAGE)
    }

    override fun saveToJson(): JsonElement {
        throw UnsupportedOperationException(UOE_MESSAGE)
    }

    override fun loadFromJson(json: JsonElement) {
        throw UnsupportedOperationException(UOE_MESSAGE)
    }

    override fun add(element: EvolutionDisplay) = this.evolutions.add(element)

    override fun addAll(elements: Collection<EvolutionDisplay>) = this.evolutions.addAll(elements)

    override fun clear() {
        this.evolutions.clear()
    }

    override fun iterator() = this.evolutions.iterator()

    override fun remove(element: EvolutionDisplay) = this.evolutions.remove(element)

    override fun removeAll(elements: Collection<EvolutionDisplay>) = this.evolutions.removeAll(elements.toSet())

    override fun retainAll(elements: Collection<EvolutionDisplay>) = this.evolutions.retainAll(elements.toSet())

    override fun contains(element: EvolutionDisplay) = this.evolutions.contains(element)

    override fun containsAll(elements: Collection<EvolutionDisplay>) = this.evolutions.containsAll(elements)

    override fun isEmpty() = this.evolutions.isEmpty()

    companion object {

        private const val UOE_MESSAGE = "The client side has no need to save/load their EvolutionController this is purely for networking and display purposes"

    }

}