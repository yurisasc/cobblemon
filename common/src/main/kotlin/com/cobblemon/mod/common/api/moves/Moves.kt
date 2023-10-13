/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.battles.MoveTarget
import com.cobblemon.mod.common.battles.runner.ShowdownService
import com.cobblemon.mod.common.net.messages.client.data.MovesRegistrySyncPacket
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Registry for all known Moves
 */
object Moves : DataRegistry {

    override val id = cobblemonResource("moves")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<Moves>()

    private val allMoves = mutableMapOf<String, MoveTemplate>()
    private val idMapping = mutableMapOf<Int, MoveTemplate>()

    override fun reload(manager: ResourceManager) {
        this.allMoves.clear()
        this.idMapping.clear()
        val movesJson = ShowdownService.service.getMoves()
        for (i in 0 until movesJson.size()) {
            val jsMove = movesJson[i].asJsonObject
            val id = jsMove.get("id").asString
            try {
                val num = jsMove.get("num").asInt
                val elementalType = CobblemonRegistries.ELEMENTAL_TYPE.first { it.showdownId() == jsMove.get("type").asString }
                val damageCategory = DamageCategory.valueOf(jsMove.get("category").asString.uppercase())
                val power = jsMove.get("basePower").asDouble
                val target = MoveTarget.fromShowdownId(jsMove.get("target").asString)
                // If not a double it's always true
                val accuracyJson = jsMove.get("accuracy").asJsonPrimitive
                val accuracy = if (accuracyJson.isNumber) accuracyJson.asDouble else -1.0
                val pp = jsMove.get("pp").asInt
                val priority = jsMove.get("priority").asInt
                val critRatio = jsMove.get("critRatio")?.asDouble ?: 1.0
                val effectChances = arrayListOf<Double>()
                val secondariesMember = jsMove.get("secondaries")
                val secondaryMember = jsMove.get("secondary")
                if (secondariesMember != null && secondariesMember is JsonArray) {
                    for (j in 0 until secondariesMember.size()) {
                        val element = secondariesMember[j].asJsonObject
                        // They declare moves without data on secondary effects for sheer force compatibility
                        if (element.has("chance")) {
                            effectChances += element.get("chance").asDouble
                        }
                    }
                }
                else if (secondaryMember != null && secondaryMember is JsonObject) {
                    // They declare moves without data on secondary effects for sheer force compatibility
                    if (secondaryMember.has("chance")) {
                        effectChances += secondaryMember.get("chance").asDouble
                    }
                }
                val move = MoveTemplate(id, num, elementalType, damageCategory, power, target, accuracy, pp, priority, critRatio, effectChances.toTypedArray())
                this.register(move)
            } catch (e: Exception) {
                Cobblemon.LOGGER.error("Caught exception trying to resolve the move '{}'", id, e)
            }
        }
        Cobblemon.LOGGER.info("Loaded {} moves", this.allMoves.size)
        this.observable.emit(this)
    }

    override fun sync(player: ServerPlayerEntity) {
        MovesRegistrySyncPacket(all()).sendToPlayer(player)
    }

    fun getByName(name: String) = allMoves[name.lowercase()]
    fun getByNumericalId(id: Int) = idMapping[id]
    fun getByNameOrDummy(name: String) = allMoves[name.lowercase()] ?: MoveTemplate.dummy(name.lowercase())
    fun getExceptional() = getByName("tackle") ?: allMoves.values.random()
    fun count() = allMoves.size
    fun names(): Collection<String> = this.allMoves.keys.toSet()
    fun all() = this.allMoves.values.toList()

    internal fun receiveSyncPacket(moves: Collection<MoveTemplate>) {
        moves.forEach(this::register)
    }

    private fun register(move: MoveTemplate) {
        this.allMoves[move.name] = move
        this.idMapping[move.num] = move
    }

}