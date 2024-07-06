/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.controller

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.EvolutionController
import com.cobblemon.mod.common.api.pokemon.evolution.progress.EvolutionProgress
import com.cobblemon.mod.common.api.pokemon.evolution.progress.EvolutionProgressTypes
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.AddEvolutionPacket
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.ClearEvolutionsPacket
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.RemoveEvolutionPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asTranslated
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

class ServerEvolutionController : EvolutionController<Evolution> {

    private val evolutions = hashSetOf<Evolution>()
    private val progress = arrayListOf<EvolutionProgress<*>>()
    private lateinit var pokemon: Pokemon
    private var evolutionIds = hashSetOf<String>()

    override val size: Int
        get() = this.evolutions.size

    override fun pokemon(): Pokemon = this.pokemon

    override fun attachPokemon(pokemon: Pokemon) {
        this.pokemon = pokemon
        this.evolutions.clear()
        this.progress.removeIf { !it.shouldKeep(pokemon) }
        this.evolutionIds.forEach { this.findEvolutionFromId(it)?.let(this::add) }
    }

    override fun start(evolution: Evolution) {
        CobblemonEvents.EVOLUTION_ACCEPTED.postThen(
            event = EvolutionAcceptedEvent(this.pokemon, evolution),
            ifSucceeded = {
                // Evolution will clear the pending stuff after if successful
                evolution.forceEvolve(this.pokemon)
            }
        )
    }

    override fun progress(): Collection<EvolutionProgress<*>> = this.progress.toList()

    override fun <P : EvolutionProgress<*>> trackProgress(progress: P): P {
        this.progress.add(progress)
        return progress
    }

    override fun <P : EvolutionProgress<*>> progressFirstOrCreate(predicate: (progress: EvolutionProgress<*>) -> Boolean, progressFactory: () -> P): P {
        val existing = this.progress.firstOrNull(predicate)
        if (existing == null) {
            val created = progressFactory()
            this.progress.add(created)
            return created
        }
        return existing as P
    }

    override fun add(element: Evolution): Boolean {
        if (this.evolutions.add(element)) {
            this.pokemon.getOwnerPlayer()?.sendMessage("cobblemon.ui.evolve.hint".asTranslated(pokemon.getDisplayName()).green())
            this.pokemon.notify(AddEvolutionPacket(this.pokemon, element))
            this.pokemon.getOwnerPlayer()?.playSound(CobblemonSounds.CAN_EVOLVE, 1F, 1F)
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<Evolution>): Boolean {
        var result = false
        elements.forEach { element ->
            if (this.add(element)) {
                result = true
            }
        }
        return result
    }

    override fun clear() {
        // We don't want to send unnecessary packets
        val pokemon = this.pokemon
        if (this.evolutions.isNotEmpty()) {
            this.evolutions.clear()
            this.pokemon.notify(ClearEvolutionsPacket { pokemon })
        }
        this.progress.clear()
    }

    override fun contains(element: Evolution) = this.evolutions.contains(element)

    override fun containsAll(elements: Collection<Evolution>) = this.evolutions.containsAll(elements)

    override fun isEmpty() = this.evolutions.isEmpty()

    override fun iterator() = this.evolutions.iterator()

    override fun remove(element: Evolution): Boolean {
        if (this.evolutions.remove(element)) {
            this.pokemon.notify(RemoveEvolutionPacket(this.pokemon, element))
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<Evolution>): Boolean {
        var result = false
        elements.forEach { element ->
            if (this.remove(element)) {
                result = true
            }
        }
        return result
    }

    override fun retainAll(elements: Collection<Evolution>): Boolean  {
        var result = false
        val comparedSet = elements.toSet()
        val iterator = this.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!comparedSet.contains(element)) {
                iterator.remove()
                result = true
            }
        }
        return result
    }

    private fun findEvolutionFromId(id: String) = this.pokemon.evolutions.firstOrNull { evolution -> evolution.id.equals(id, true) }

    companion object {
        private const val PENDING = "pending"
        private const val PROGRESS = "progress"
        internal const val ID_KEY = "id"

        @JvmStatic
        val CODEC: Codec<ServerEvolutionController> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.list(Codec.STRING).fieldOf(PENDING).forGetter { controller -> controller.evolutions.map { it.id } },
                Codec.list(EvolutionProgressTypes.codec()).fieldOf(PROGRESS).forGetter { controller -> controller.progress.filter { it.shouldKeep(controller.pokemon) } }
            ).apply(instance) { evolutions, progress -> ServerEvolutionController().apply {
                this.evolutionIds += evolutions
                this.progress += progress
            } }
        }
    }
}