/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.item.ability.AbilityChanger
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.scheduling.ClientTaskTracker.after
import com.cobblemon.mod.common.api.scheduling.ServerTaskTracker
import com.cobblemon.mod.common.api.scheduling.taskBuilder
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.BattleFormat
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.battles.BattleSide
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.net.messages.client.animation.PlayPoseableAnimationPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeStartedPacket
import com.cobblemon.mod.common.trade.ActiveTrade
import com.cobblemon.mod.common.trade.DummyTradeParticipant
import com.cobblemon.mod.common.trade.PlayerTradeParticipant
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.toPokemon
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import java.io.File
import java.io.PrintWriter
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Box

@Suppress("unused")
object TestCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("testcommand")
            .requires { it.hasPermissionLevel(4) }
            .executes(::execute)
        dispatcher.register(command)
    }

    @Suppress("SameReturnValue")
    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        if (context.source.entity !is ServerPlayerEntity) {
            return Command.SINGLE_SUCCESS
        }

        try {
            val player = context.source.entity as ServerPlayerEntity
            val evolutionEntity = GenericBedrockEntity(world = player.world)
            evolutionEntity?.apply {
                category = cobblemonResource("evolution")
                colliderHeight = 1.5F
                colliderWidth = 1.5F
                scale = 1F
                syncAge = true // Otherwise particle animation will be starting from zero even if you come along partway through
                setPosition(player.x, player.y, player.z + 4)
            }
            player.world.spawnEntity(evolutionEntity)
            after(seconds = 0.5F) {
                player.sendPacket(PlayPoseableAnimationPacket(evolutionEntity.id, setOf("evolution:animation.evolution.evolution"), emptySet()))
            }


//            readBerryDataFromCSV()

//            this.testClosestBattle(context)
            //testTrade(context.source.player!!)
//            testParticles(context)
//            extractMovesData()
//            // Player variables
//            val player = context.source.entity as ServerPlayerEntity
//            val party = player.party()
//            party.heal()
//
//            val playerActor = PlayerBattleActor(
//                player.uuid,
//                party.toBattleTeam()
//            )
//
//            // Enemy variables
//            val pokemon = Pokemon().apply { species = PokemonSpecies.random() }.also { it.initialize() }
//            val enemyPokemon = BattlePokemon(pokemon)
//
//            val enemyPokemon2 = BattlePokemon(PokemonSpecies.random().create())
//            val enemyPokemon3 = BattlePokemon(PokemonSpecies.random().create())
//            val enemyPokemon4 = BattlePokemon(PokemonSpecies.random().create())
//
//            enemyPokemon.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(2.0, 0.0, 0.0))
//            enemyPokemon2.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(-2.0, 0.0, 0.0))
//            enemyPokemon3.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(0.0, 0.0, 2.0))
//            enemyPokemon4.effectedPokemon.sendOut(player.world as ServerWorld, player.pos.add(0.0, 0.0, -2.0))
//
//            // Start the battle
//            BattleRegistry.startBattle(
//                battleFormat = BattleFormat.GEN_8_DOUBLES,
//                side1 = BattleSide(playerActor),
//                side2 = BattleSide(MultiPokemonBattleActor(listOf(enemyPokemon, enemyPokemon2, enemyPokemon3, enemyPokemon4)))
//            )

//            val player = context.source.entity as ServerPlayerEntity
//            player.giveItemStack(PokemonItem.from(PokemonSpecies.random(), "alolan"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Command.SINGLE_SUCCESS
    }

    var trade: ActiveTrade? = null
    var lastDebugId = 0

    private fun testClosestBattle(context: CommandContext<ServerCommandSource>) {
        val player = context.source.playerOrThrow
        val cloneTeam = player.party().toBattleTeam(true)
        cloneTeam.forEach { it.effectedPokemon.level = 100 }
        val scanBox = Box.of(player.pos, 9.0, 9.0, 9.0)
        val results = player.world.getEntitiesByType(CobblemonEntities.POKEMON, scanBox) { entityPokemon -> entityPokemon.pokemon.isWild() }
        val pokemonEntity = results.firstOrNull()
        if (pokemonEntity == null) {
            context.source.sendError(Text.literal("Cannot find any wild Pokémon in a 9x9x9 area"))
            return
        }
        BattleRegistry.startBattle(
            BattleFormat.GEN_9_SINGLES,
            BattleSide(PlayerBattleActor(player.uuid, cloneTeam)),
            BattleSide(PokemonBattleActor(pokemonEntity.pokemon.uuid, BattlePokemon(pokemonEntity.pokemon), Cobblemon.config.defaultFleeDistance))
        )
    }

    private fun testTrade(playerEntity: ServerPlayerEntity) {
        val trade = ActiveTrade(
            player1 = PlayerTradeParticipant(playerEntity),
            player2 = DummyTradeParticipant(
                pokemonList = mutableListOf(
                    "pikachu level=30 shiny".toPokemon(),
                    "machop level=15".toPokemon()
                )
            )
        )
        this.trade = trade
        playerEntity.sendPacket(TradeStartedPacket(trade.player2.uuid, trade.player2.name.copy(), trade.player2.party.mapNullPreserving(TradeStartedPacket::TradeablePokemon)))

        taskBuilder()
            .interval(0.5F) // Run every half second
            .execute { task ->
                if (this.trade != trade) {
                    task.expire()
                    return@execute
                }

                testUpdate()
            }
            .tracker(ServerTaskTracker)
            .iterations(Int.MAX_VALUE)
            .build()
    }

    @Suppress("UNUSED_VARIABLE")
    private fun testUpdate() {
        val trade = this.trade ?: return
        val dummy = trade.player2 as DummyTradeParticipant

        val currentDebugId = 0 // Change this number to some other number and hot reload when you want the later code block to run once.

        if (lastDebugId != currentDebugId) {
            // Some code here, when hotswapped, will immediately run.
            // This is a trick so that if you want to fiddle with the GUI, then you want the dummy participant to do something,
            // you can update the code here and the 'currentDebugId' value and this will run once.

            // Something

            this.lastDebugId = currentDebugId
        }
    }

    fun readBerryDataFromCSV() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val csv = File("scripty/berries.csv").readLines()
        val iterator = csv.iterator()
        iterator.next() // Skip heading
        iterator.next() // Skip sub-heading thing
        for (line in iterator) {
            val cols = line.split(",")
            val berryName = cols[1].lowercase() + "_berry"
            val json = gson.fromJson(File("scripty/old/$berryName.json").reader(), JsonObject::class.java)
            val growthPoints = mutableListOf<JsonObject>()
            var index = 7
            while (true) {
                if (cols.size <= index || cols[index].isBlank()) {
                    break
                }

                val posX = cols[index].toFloat()
                val posY = cols[index+1].toFloat()
                val posZ = cols[index+2].toFloat()
                val rotX = cols[index+3].toFloat()
                val rotY = cols[index+4].toFloat()
                val rotZ = cols[index+5].toFloat()

                val position = JsonObject()
                position.addProperty("x", posX)
                position.addProperty("y", posY)
                position.addProperty("z", posZ)
                val rotation = JsonObject()
                rotation.addProperty("x", rotX)
                rotation.addProperty("y", rotY)
                rotation.addProperty("z", rotZ)

                val obj = JsonObject()
                obj.add("position", position)
                obj.add("rotation", rotation)
                growthPoints.add(obj)
                index += 6
            }

            val arr = json.getAsJsonArray("growthPoints")
            arr.removeAll { true }
            for (point in growthPoints) {
                arr.add(point)
            }

            val new = File("scripty/new/$berryName.json")
            val pw = PrintWriter(new)
            gson.toJson(json, pw)
            pw.flush()
            pw.close()
        }
    }

//    private fun testParticles(context: CommandContext<ServerCommandSource>) {
//        val file = File("particle.particle.json")
//        val effect = SnowstormParticleReader.loadEffect(GsonBuilder().create().fromJson<JsonObject>(file.readText()))
//
//        val player = context.source.entity as ServerPlayerEntity
//        val position = player.pos
//        val pkt = SpawnSnowstormParticlePacket(effect, position, 0F, 0F)
//        player.sendPacket(pkt)
//    }

//    private fun extractMovesData() {
//        val ctx = GraalShowdown.context
//        ctx.eval("js", """
//                const ShowdownMoves = require('pokemon-showdown/data/moves');
//            """.trimIndent())
//        val moves = ctx.getBindings("js").getMember("ShowdownMoves").getMember("Moves")
//        val gson = GsonBuilder().setPrettyPrinting().create()
//        for (moveName in moves.memberKeys) {
//            try {
//                val value = moves.getMember(moveName)
//                val obj = JsonObject()
//                obj.addProperty("name", moveName)
//                obj.addProperty("type", value.getMember("type").asString())
//                obj.addProperty("damageCategory", value.getMember("category").asString())
//                obj.addProperty("target", value.getMember("target").asString())
//                obj.addProperty("power", value.getMember("basePower").asInt())
//                obj.addProperty("accuracy", value.getMember("accuracy").let { if (it.isBoolean) -1F else it.asFloat() })
//                obj.addProperty("pp", value.getMember("pp").asInt())
//                obj.addProperty("priority", value.getMember("priority").asInt())
//                if (value.hasMember("secondary")) {
//                    val secondary = value.getMember("secondary")
//                    if (secondary.hasMember("chance")) {
//                        obj.addProperty("effectChance", secondary.getMember("chance").asInt())
//                    }
//                }
//                val file = File("outputmoves").also { it.mkdir() }
//                val pw = PrintWriter(File(file, "$moveName.json"))
//                pw.write(gson.toJson(obj))
//                pw.close()
//            } catch (e: Exception) {
//                println("Issue when converting $moveName")
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun extractAbilitiesData() {
//        val ctx = GraalShowdown.context
//        ctx.eval("js", """
//            const ShowdownAbilities = require('pokemon-showdown/data/abilities');
//        """.trimIndent())
//        val abilities = ctx.getBindings("js").getMember("ShowdownAbilities").getMember("Abilities")
//        val gson = GsonBuilder().setPrettyPrinting().create()
//        for (abilityName in abilities.memberKeys) {
//            try {
//                val obj = JsonObject()
//                obj.addProperty("name", abilityName)
//                obj.addProperty("displayName", "cobblemon.ability.$abilityName")
//                obj.addProperty("description", "cobblemon.ability.$abilityName.desc")
//                val file = File("outputabilities").also { it.mkdir() }
//                val pw = PrintWriter(File(file, "$abilityName.json"))
//                pw.write(gson.toJson(obj))
//                pw.close()
//            } catch (e: Exception) {
//                println("Issue when converting $abilityName")
//                e.printStackTrace()
//            }
//        }
//    }

    private fun testAbilitiesBetweenEvolution(context: CommandContext<ServerCommandSource>) {
        val results = Text.literal("Ability test results (Assumed default assets)")
            .append(Text.literal("\n"))
            .append(this.testHiddenAbilityThroughoutEvolutions())
            .append(Text.literal("\n"))
            .append(this.testMiddleStageSingleAbility())
            .append(Text.literal("\n"))
            .append(this.testForcedAbility())
            .append(Text.literal("\n"))
            .append(this.testIllegalAbilityNonForced())
            .append(Text.literal("\n"))
            .append(this.testAbilityCapsule())
            .append(Text.literal("\n"))
            .append(this.testAbilityPatch())
        context.source.sendMessage(results)
    }

    private fun testHiddenAbilityThroughoutEvolutions(): Text {
        // Hidden ability test, Dragonite HA differs from Dratini/Dragonair we need to ensure he keeps that ability until the end
        // Skip Dratini cause same HA irrelevant for this test
        val pokemon = PokemonProperties.parse("dragonair level=${Cobblemon.config.maxPokemonLevel} hiddenability=true").create()
        val dragonite = pokemon.evolutions.firstOrNull() ?: return Text.literal("✖ Failed to find Dragonair » Dragonite evolution").red()
        dragonite.evolutionMethod(pokemon)
        val failed = pokemon.ability.index != 0 || pokemon.ability.priority != Priority.LOW || pokemon.ability.forced
        val symbol = if (failed) "✖" else "✔"
        val result = Text.literal(" $symbol Dratini line final Ability(name=${pokemon.ability.name}, priority=${pokemon.ability.priority}, index=${pokemon.ability.index}, forced=${pokemon.ability.forced})")
        return if (failed) result.red() else result.green()
    }

    private fun testMiddleStageSingleAbility(): Text {
        val pokemon = PokemonProperties.parse("scatterbug level=${Cobblemon.config.maxPokemonLevel} ability=compoundeyes").create()
        val spewpa = pokemon.evolutions.firstOrNull() ?: return Text.literal("✖ Failed to find Scatterbug » Spewpa evolution").red()
        spewpa.evolutionMethod(pokemon)
        val vivillon = pokemon.evolutions.firstOrNull() ?: return Text.literal("✖ Failed to find Spewpa » Vivillon evolution").red()
        vivillon.evolutionMethod(pokemon)
        val failed = pokemon.ability.index != 1 || pokemon.ability.priority != Priority.LOWEST || pokemon.ability.forced
        val symbol = if (failed) "✖" else "✔"
        val result = Text.literal(" $symbol Scatterbug line final Ability(name=${pokemon.ability.name}, priority=${pokemon.ability.priority}, index=${pokemon.ability.index}, forced=${pokemon.ability.forced})")
        return if (failed) result.red() else result.green()
    }

    private fun testForcedAbility(): Text {
        val pokemon = PokemonProperties.parse("magikarp level=${Cobblemon.config.maxPokemonLevel} ability=adaptability").create()
        val gyarados = pokemon.evolutions.firstOrNull() ?: return Text.literal("✖ Failed to find Magikarp » Gyarados evolution").red()
        gyarados.evolutionMethod(pokemon)
        val failed = !pokemon.ability.forced || pokemon.ability.template.name != "adaptability"
        val symbol = if (failed) "✖" else "✔"
        val result = Text.literal(" $symbol Magikarp line forced Ability(name=${pokemon.ability.name}, priority=${pokemon.ability.priority}, index=${pokemon.ability.index}, forced=${pokemon.ability.forced})")
        return if (failed) result.red() else result.green()
    }

    private fun testIllegalAbilityNonForced(): Text {
        val pokemon = PokemonProperties.parse("rattata").create()
        pokemon.updateAbility(Abilities.getOrException("adaptability").create(false))
        val failed = !pokemon.ability.forced
        val symbol = if (failed) "✖" else "✔"
        val result = Text.literal(" $symbol Rattata illegal non-forced (name=${pokemon.ability.name}, priority=${pokemon.ability.priority}, index=${pokemon.ability.index}, forced=${pokemon.ability.forced})")
        return if (failed) result.red() else result.green()
    }

    private fun testAbilityCapsule(): Text {
        val pokemon = PokemonProperties.parse("rattata").create()
        val failed = !AbilityChanger.COMMON_ABILITY.performChange(pokemon)
        val symbol = if (failed) "✖" else "✔"
        val result = Text.literal(" $symbol Rattata capsule Ability(name=${pokemon.ability.name}, priority=${pokemon.ability.priority}, index=${pokemon.ability.index}, forced=${pokemon.ability.forced})")
        return if (failed) result.red() else result.green()
    }

    private fun testAbilityPatch(): Text {
        val pokemon = PokemonProperties.parse("magikarp ha=true").create()
        // It shouldn't change
        val failed = AbilityChanger.HIDDEN_ABILITY.performChange(pokemon)
        val symbol = if (failed) "✖" else "✔"
        val result = Text.literal(" $symbol Magikarp patch Ability(name=${pokemon.ability.name}, priority=${pokemon.ability.priority}, index=${pokemon.ability.index}, forced=${pokemon.ability.forced})")
        return if (failed) result.red() else result.green()
    }

}