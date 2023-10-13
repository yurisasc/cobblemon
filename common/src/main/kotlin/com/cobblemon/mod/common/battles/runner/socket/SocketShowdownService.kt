/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.runner.socket

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.hiddenpower.IvCondition
import com.cobblemon.mod.common.battles.BagItems
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.runner.ShowdownService
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

/**
 * Mediator service for communicating between the Cobblemon Minecraft mod and Cobblemon showdown service via
 * a socket client.
 *
 * This is primarily used for debugging purposes, but could be extended in the future to provide
 * a means of connecting to a remote Showdown server. This does not provide any fault handling in the
 * event that the server goes down.
 *
 * When messages are sent to showdown, this will await a response from showdown.
 * The protocol for messages sent from showdown is <length of characters in payload><payload>,
 * and a payload length of 0 indicates that there is no response.
 *
 * @see {@code sim/cobbled-debug-server.ts} within cobblemon-showdown repository
 * @since February 27, 2023
 * @author landonjw
 */
@Suppress("unused")
class SocketShowdownService(val host: String = "localhost", val port: Int = 18468, val localPort: Int = 0) : ShowdownService {

    private lateinit var socket: Socket
    private lateinit var writer: OutputStreamWriter
    private lateinit var reader: BufferedReader
    val gson = Gson()

    override fun openConnection() {
        socket = Socket(InetAddress.getLocalHost(), port, InetAddress.getLocalHost(), localPort)
        writer = socket.getOutputStream().writer(charset = Charset.forName("ascii"))
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    override fun closeConnection() {
        socket.close()
    }

    override fun startBattle(battle: PokemonBattle, messages: Array<String>) {
        writer.write(">startbattle ${battle.battleId}\n")
        acknowledge { Cobblemon.LOGGER.error("Failed to start battle!") }
        send(battle.battleId, messages)
    }

    override fun send(battleId: UUID, messages: Array<String>) {
        for (message in messages) {
            writer.write("$battleId~$message\n")
            writer.flush()
            readBattleInput().forEach { interpretMessage(battleId, it) }
        }
    }

    private fun read(reader: BufferedReader, size: Int): String {
        val buffer = CharArray(size)
        while (true) {
            if(reader.read(buffer) == 0) continue
            return String(buffer)
        }
    }

    private fun readMessage(): String {
        val payloadSize = read(reader, 8).toInt()
        val payload = read(reader, payloadSize)
        return payload
    }

    private fun readBattleInput(): List<String> {
        val lines = mutableListOf<String>()
        val numLines = read(reader, 8).toInt()
        if (numLines != 0) {
            for (i in 0 until numLines) {
                lines.add(readMessage())
            }
        }
        return lines
    }

    private fun interpretMessage(battleId: UUID, message: String) {
        ShowdownInterpreter.interpretMessage(battleId, message)
    }

    override fun getAbilityIds(): JsonArray {
        writer.write(">getCobbledAbilityIds")
        writer.flush()
        val response = readMessage()
        return gson.fromJson(response, JsonArray::class.java)
    }

    override fun getMoves(): JsonArray {
        writer.write(">getCobbledMoves\n")
        writer.flush()
        val response = readMessage()
        return gson.fromJson(response, JsonArray::class.java)
    }

    override fun getItemIds(): JsonArray {
        writer.write(">getCobbledItemIds")
        writer.flush()
        val response = readMessage()
        return gson.fromJson(response, JsonArray::class.java)
    }

    private fun sendSpeciesData(species: Species, form: FormData?) {
        writer.write(">receiveSpeciesData ${gson.toJson(PokemonSpecies.ShowdownSpecies(species, form))}\n")
        acknowledge()
    }

    private fun sendBagItem(itemId: String, js: String) {
        writer.write(">receiveBagItemData $itemId $js")
        acknowledge { Cobblemon.LOGGER.error("Failed to send bag item to Showdown: $itemId") }
    }

    override fun registerSpecies() {
        writer.write(">resetSpeciesData\n")
        acknowledge()
        PokemonSpecies.species.forEach { species ->
            sendSpeciesData(species, null)
            species.forms.forEach { form ->
                if (form != species.standardForm) {
                    sendSpeciesData(species, form)
                }
            }
        }
    }

    override fun registerTypes() {
        writer.write(">resetTypeData\n")
        acknowledge()
        CobblemonRegistries.ELEMENTAL_TYPE.forEach { type ->
            this.sendType(type)
        }
    }

    fun acknowledge(ifFails: () -> Unit = {}) {
        writer.flush()
        val ack = CharArray(3)
        reader.read(ack)
        if (String(ack) != "ACK") {
            ifFails()
        }
    }

    override fun registerBagItems() {
        for ((itemId, js) in BagItems.bagItemsScripts) {
            sendBagItem(itemId, js.replace("\n", " "))
        }
    }

    override fun indicateSpeciesInitialized() {
        writer.write(">afterCobbledSpeciesInit")
        acknowledge()
    }

    private fun sendType(type: ElementalType) {
        // See TypeInfo in Showdown
        val jObject = JsonObject().apply {
            val showdownId = type.showdownId()
            addProperty("name", showdownId.replaceFirstChar(Char::uppercase))
            addProperty("id", showdownId)
            val damageTaken = JsonObject()
            type.resistanceInformation().forEach { information ->
                damageTaken.addProperty(information.showdownId(), information.resistance.showdownValue)
            }
            add("damageTaken", damageTaken)
            type.hiddenPowerRequirement.ifPresent {
                val hpIvs = JsonObject()
                it.statMap.forEach { (stat, requirement) ->
                    if (requirement == IvCondition.EVEN) {
                        hpIvs.addProperty(stat.showdownId, 30)
                    }
                }
                add("HPivs", hpIvs)
            }
        }
        writer.write(">receiveTypeData ${this.gson.toJson(jObject)}\n")
        acknowledge()
    }

}