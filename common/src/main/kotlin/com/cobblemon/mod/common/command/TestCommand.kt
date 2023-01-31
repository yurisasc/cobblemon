/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.snowstorm.AnimatedParticleUVMode
import com.cobblemon.mod.common.api.snowstorm.BedrockParticle
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEmitter
import com.cobblemon.mod.common.api.snowstorm.DynamicParticleMotion
import com.cobblemon.mod.common.api.snowstorm.DynamicParticleRotation
import com.cobblemon.mod.common.api.snowstorm.GradientParticleTinting
import com.cobblemon.mod.common.api.snowstorm.InstantParticleEmitterRate
import com.cobblemon.mod.common.api.snowstorm.LookAtXYZ
import com.cobblemon.mod.common.api.snowstorm.LoopingEmitterLifetime
import com.cobblemon.mod.common.api.snowstorm.OnceEmitterLifetime
import com.cobblemon.mod.common.api.snowstorm.OutwardsMotionDirection
import com.cobblemon.mod.common.api.snowstorm.ParticleMaterial
import com.cobblemon.mod.common.api.snowstorm.ParticleMotion
import com.cobblemon.mod.common.api.snowstorm.RotateXYZCameraMode
import com.cobblemon.mod.common.api.snowstorm.RotateYCameraMode
import com.cobblemon.mod.common.api.snowstorm.SphereParticleEmitterShape
import com.cobblemon.mod.common.api.snowstorm.StaticParticleUVMode
import com.cobblemon.mod.common.api.snowstorm.SteadyParticleEmitterRate
import com.cobblemon.mod.common.battles.runner.GraalShowdown
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket
import com.cobblemon.mod.common.particle.SnowstormParticleReader
import com.cobblemon.mod.common.util.asExpression
import com.cobblemon.mod.common.util.fromJson
import com.cobblemon.mod.common.util.getString
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.JsonOps
import java.io.File
import java.io.PrintWriter
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vector4f

object TestCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("testcommand")
            .requires { it.hasPermissionLevel(4) }
            .executes { execute(it) }
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        if (context.source.entity !is ServerPlayerEntity) {
            return Command.SINGLE_SUCCESS
        }

        try {
//            val jsonEffect = File("something.json")

//            val effect = BedrockParticleEffect(
//                emitter = BedrockParticleEmitter(
//                    rate = SteadyParticleEmitterRate(
//                        rate = 200.0.asExpression(),
//                        maximum = 200.0.asExpression()
//                    ),
//                    lifetime = OnceEmitterLifetime(
//                        activeTime = 4.0.asExpression()
//                    ),
//                    shape = SphereParticleEmitterShape(
//                        radius = 1.0.asExpression(),
//                        surfaceOnly = true
//                    )
//                ),
//                particle = BedrockParticle(
//                    texture = Identifier("minecraft:textures/block/fire_0.png"),
////                    texture = Identifier("minecraft:textures/particle/bubble.png"),
//                    material = ParticleMaterial.BLEND,
//                    sizeX = 0.12.asExpression(),
//                    sizeY = 0.12.asExpression(),
//                    maxAge = 3.5.asExpression(),
//                    motion = DynamicParticleMotion(
//                        direction = OutwardsMotionDirection(),
//                        speed = 2.0.asExpression(),
//                    ),
//                    rotation = DynamicParticleRotation(
//                        speed = 0.0.asExpression()
//                    ),
//                    uvMode = AnimatedParticleUVMode(
//                        textureSizeX = 16,
//                        textureSizeY = 512,
//                        startU = 0.0.asExpression(),
//                        startV = 0.0.asExpression(),
//                        uSize = 16.0.asExpression(),
//                        vSize = 16.0.asExpression(),
//                        stepU = 0.0.asExpression(),
//                        stepV = 16.0.asExpression(),
//                        maxFrame = 32.0.asExpression(),
//                        fps = 0.0.asExpression(),
//                        stretchToLifetime = true,
//                        loop = false
//                    ),
//                    cameraMode = RotateXYZCameraMode(),
//                    tinting = GradientParticleTinting(
//                        interpolant = "v.particle_age / v.particle_lifetime".asExpression(),
//                        gradient = mapOf(
//                            0.0 to Vector4f(1F, 1F, 1F, 1F),
//                            1.0 to Vector4f(0F, 0F, 0F, 1F)
//                        )
//                    )
//                )
//            )

            val file = File("particle.particle.json")
            val effect = SnowstormParticleReader.loadEffect(GsonBuilder().create().fromJson<JsonObject>(file.readText()))

            val player = context.source.entity as ServerPlayerEntity
            val position = player.pos.add(4.0, 1.0, 4.0)
            val codec = BedrockParticleEffect.CODEC
            codec.encodeStart(JsonOps.INSTANCE, effect).map { println(it.toString()) }
            val pkt = SpawnSnowstormParticlePacket(effect, position)
            player.sendPacket(pkt)
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

    private fun extractMovesData() {
        val ctx = GraalShowdown.context
        ctx.eval("js", """
                const ShowdownMoves = require('pokemon-showdown/data/moves');
            """.trimIndent())
        val moves = ctx.getBindings("js").getMember("ShowdownMoves").getMember("Moves")
        val gson = GsonBuilder().setPrettyPrinting().create()
        for (moveName in moves.memberKeys) {
            try {
                val value = moves.getMember(moveName)
                val obj = JsonObject()
                obj.addProperty("name", moveName)
                obj.addProperty("type", value.getMember("type").asString())
                obj.addProperty("damageCategory", value.getMember("category").asString())
                obj.addProperty("target", value.getMember("target").asString())
                obj.addProperty("power", value.getMember("basePower").asInt())
                obj.addProperty("accuracy", value.getMember("accuracy").let { if (it.isBoolean) -1F else it.asFloat() })
                obj.addProperty("pp", value.getMember("pp").asInt())
                obj.addProperty("priority", value.getMember("priority").asInt())
                if (value.hasMember("secondary")) {
                    val secondary = value.getMember("secondary")
                    if (secondary.hasMember("chance")) {
                        obj.addProperty("effectChance", secondary.getMember("chance").asInt())
                    }
                }
                val file = File("outputmoves").also { it.mkdir() }
                val pw = PrintWriter(File(file, "$moveName.json"))
                pw.write(gson.toJson(obj))
                pw.close()
            } catch (e: Exception) {
                println("Issue when converting $moveName")
                e.printStackTrace()
            }
        }
    }

    private fun extractAbilitiesData() {
        val ctx = GraalShowdown.context
        ctx.eval("js", """
            const ShowdownAbilities = require('pokemon-showdown/data/abilities');
        """.trimIndent())
        val abilities = ctx.getBindings("js").getMember("ShowdownAbilities").getMember("Abilities")
        val gson = GsonBuilder().setPrettyPrinting().create()
        for (abilityName in abilities.memberKeys) {
            try {
                val obj = JsonObject()
                obj.addProperty("name", abilityName)
                obj.addProperty("displayName", "cobblemon.ability.$abilityName")
                obj.addProperty("description", "cobblemon.ability.$abilityName.desc")
                val file = File("outputabilities").also { it.mkdir() }
                val pw = PrintWriter(File(file, "$abilityName.json"))
                pw.write(gson.toJson(obj))
                pw.close()
            } catch (e: Exception) {
                println("Issue when converting $abilityName")
                e.printStackTrace()
            }
        }
    }
}