package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendToServer
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution.AcceptEvolutionPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.getOnSide
import com.cablemc.pokemoncobbled.common.util.ifClient
import com.google.gson.JsonElement
import net.minecraft.nbt.NbtElement

internal class CobbledClientEvolutionController(override val pokemon: Pokemon) : EvolutionController<EvolutionDisplay> {

    private val evolutions = hashSetOf<EvolutionDisplay>()

    override val size: Int
        get() = this.evolutions.size

    override fun start(evolution: EvolutionDisplay) {
        ifClient { sendToServer(AcceptEvolutionPacket(this.pokemon, evolution)) }
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

    override fun add(element: EvolutionDisplay) = getOnSide(client = { this.evolutions.add(element) }, server = { false })

    override fun addAll(elements: Collection<EvolutionDisplay>) = getOnSide(client = { this.evolutions.addAll(elements) }, server = { false })

    override fun clear() {
        ifClient { this.evolutions.clear() }
    }

    override fun iterator() = getOnSide(client = { this.evolutions.iterator() }, server = { mutableListOf<EvolutionDisplay>().iterator() })

    override fun remove(element: EvolutionDisplay) = getOnSide(client = { this.evolutions.remove(element) }, server = { false })

    override fun removeAll(elements: Collection<EvolutionDisplay>) = getOnSide(client = { this.evolutions.removeAll(elements.toSet()) }, server = { false })

    override fun retainAll(elements: Collection<EvolutionDisplay>) = getOnSide(client = { this.evolutions.retainAll(elements.toSet()) }, server = { false })

    override fun contains(element: EvolutionDisplay) = getOnSide(client = { this.evolutions.contains(element) }, server = { false })

    override fun containsAll(elements: Collection<EvolutionDisplay>) = getOnSide(client = { this.evolutions.containsAll(elements) }, server = { false })

    override fun isEmpty() = getOnSide(client = { this.evolutions.isEmpty() }, server = { false })

    companion object {

        private const val UOE_MESSAGE = "The client side has no need to save/load their EvolutionController this is purely for networking and display purposes"

    }

}