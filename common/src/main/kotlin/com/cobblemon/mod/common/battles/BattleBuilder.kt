/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor
import com.cobblemon.mod.common.battles.ai.RandomBattleAI
import com.cobblemon.mod.common.battles.ai.StrongBattleAI
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.getBattleTheme
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.party
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.*
import kotlin.collections.HashMap
object BattleBuilder {

    @JvmOverloads
    fun pvp1v1(
        player1: ServerPlayerEntity,
        player2: ServerPlayerEntity,
        leadingPokemonPlayer1: UUID? = null,
        leadingPokemonPlayer2: UUID? = null,
        battleFormat: BattleFormat = BattleFormat.GEN_9_SINGLES,
        cloneParties: Boolean = false,
        healFirst: Boolean = false,
        partyAccessor: (ServerPlayerEntity) -> PartyStore = { it.party() }
    ): BattleStartResult {
        val team1 = partyAccessor(player1).toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemonPlayer1)
        val team2 = partyAccessor(player2).toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemonPlayer2)

        val player1Actor = PlayerBattleActor(player1.uuid, team1)
        val player2Actor = PlayerBattleActor(player2.uuid, team2)

        val errors = ErroredBattleStart()

        for ((player, actor) in arrayOf(player1 to player1Actor, player2 to player2Actor)) {
            if (actor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
                errors.participantErrors[actor] += BattleStartError.insufficientPokemon(
                    player = player,
                    requiredCount = battleFormat.battleType.slotsPerActor,
                    hadCount = actor.pokemonList.size
                )
            }

            if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
                errors.participantErrors[actor] += BattleStartError.alreadyInBattle(player)
            }
        }

        return if (errors.isEmpty) {
            BattleRegistry.startBattle(
                battleFormat = battleFormat,
                side1 = BattleSide(player1Actor),
                side2 = BattleSide(player2Actor)
            ).ifSuccessful {
                player1Actor.battleTheme = player2.getBattleTheme()
                player2Actor.battleTheme = player1.getBattleTheme()
            }
            errors
        } else {
            errors
        }
    }

    /**
     * Attempts to create a PvE battle against the given Pokémon.
     *
     * @param player The player battling the wild Pokémon.
     * @param pokemonEntity The Pokémon to battle.
     * @param leadingPokemon The Pokémon in the player's party to send out first. If null, it uses the first in the party.
     * @param battleFormat The format to use for the battle. By default it is [BattleFormat.GEN_9_SINGLES].
     * @param cloneParties Whether the player's party should be cloned so that damage will not affect their party afterwards. Defaults to false.
     * @param healFirst Whether the player's Pokémon should be healed before the battle starts. Defaults to false.
     * @param fleeDistance How far away the player must get to flee the Pokémon. If the value is -1, it cannot be fled.
     * @param party The party of the player to use for the battle. This does not need to be their actual party. Defaults to it though.
     */
    @JvmOverloads
    fun pve(
        player: ServerPlayerEntity,
        pokemonEntity: PokemonEntity,
        leadingPokemon: UUID? = null,
        battleFormat: BattleFormat = BattleFormat.GEN_9_SINGLES,
        cloneParties: Boolean = false,
        healFirst: Boolean = false,
        fleeDistance: Float = Cobblemon.config.defaultFleeDistance,
        party: PartyStore = player.party()
    ): BattleStartResult {
        val playerTeam = party.toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemon = leadingPokemon)
        val playerActor = PlayerBattleActor(player.uuid, playerTeam)
        val wildActor = PokemonBattleActor(pokemonEntity.pokemon.uuid, BattlePokemon(pokemonEntity.pokemon), fleeDistance)
        val errors = ErroredBattleStart()

        if (playerActor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
            errors.participantErrors[playerActor] += BattleStartError.insufficientPokemon(
                player = player,
                requiredCount = battleFormat.battleType.slotsPerActor,
                hadCount = playerActor.pokemonList.size
            )
        }

        if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
            errors.participantErrors[playerActor] += BattleStartError.alreadyInBattle(playerActor)
        }

        if (pokemonEntity.battleId != null) {
            errors.participantErrors[wildActor] += BattleStartError.alreadyInBattle(wildActor)
        }

        return if (errors.isEmpty) {
            BattleRegistry.startBattle(
                battleFormat = battleFormat,
                side1 = BattleSide(playerActor),
                side2 = BattleSide(wildActor)
            ).ifSuccessful {
                if (!cloneParties) {
                    pokemonEntity.battleId = it.battleId
                }
                playerActor.battleTheme = pokemonEntity.getBattleTheme()
            }
            errors
        } else {
            errors
        }
    }

    // player versus computer testing
    @JvmOverloads
    fun pvc(player: ServerPlayerEntity,
            battleAI: String,
            battleLevel: Int = 50,
            teamTyping: String = "random",
            cloneParties: Boolean = false,
            npcParty: MutableList<BattlePokemon> = mutableListOf(),
            battleFormat: BattleFormat = BattleFormat.GEN_9_SINGLES,
            healFirst: Boolean = false,
            playerParty: PartyStore = player.party()
    ): BattleStartResult {
        // choose battleAI AI type
        val battleAIType = when (battleAI) {
            "0" -> StrongBattleAI(0)
            "random" -> StrongBattleAI(0)
            "1" -> StrongBattleAI(1)
            "beginner" -> StrongBattleAI(1)
            "2" -> StrongBattleAI(2)
            "trainee" -> StrongBattleAI(2)
            "3" -> StrongBattleAI(3)
            "intermediate" -> StrongBattleAI(3)
            "4" -> StrongBattleAI(4)
            "advanced" -> StrongBattleAI(4)
            "5" -> StrongBattleAI(5)
            "master" -> StrongBattleAI(5)
            else -> RandomBattleAI()
        }

        val playerTeam = playerParty.toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemon = null)
        val playerClonedPartyStore: PlayerPartyStore = PlayerPartyStore(player.uuid)
        if(cloneParties) {
            playerTeam.forEachIndexed { index, it ->
                playerClonedPartyStore.set(index, it.effectedPokemon)
            }
        }
        val playerActor = PlayerBattleActor(player.uuid, playerTeam)

        if (npcParty.isEmpty()) {
            when (teamTyping.lowercase()) {
                "random" -> {
                    repeat(6) {
                        // todo generate random party of 6 BattlePokemon
                        val npcPokemon = Pokemon()

                        npcPokemon.uuid = UUID.randomUUID()
                        npcPokemon.level = battleLevel
                        npcPokemon.initialize() // This will generate everything else about the pokemon

                        npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                    }
                }
                "cynthia" -> {
                    // Spiritomb
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("spiritomb")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,6)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("pressure")!!.create()
                    npcPokemon.setMoveset(listOf("darkpulse", "shadowball", "willowisp", "suckerpunch"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Porygon-Z
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("porygonz")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)  //expert belt
                    npcPokemon.ability = Abilities.get("adaptability")!!.create()
                    npcPokemon.setMoveset(listOf("hyperbeam", "shadowball", "icebeam", "thunderbolt"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Togekiss
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("togekiss")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    //npcPokemon.item  leftovers
                    npcPokemon.ability = Abilities.get("serenegrace")!!.create()
                    npcPokemon.setMoveset(listOf("airslash", "dazzlinggleam", "aurasphere", "thunderwave"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Lucario
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("lucario")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FOCUS_SASH), false)  //focus sash
                    npcPokemon.ability = Abilities.get("innerfocus")!!.create()
                    npcPokemon.setMoveset(listOf("closecombat", "meteormash", "earthquake", "extremespeed"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Milotic
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("milotic")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,6)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FLAME_ORB), false) //  focus sash
                    npcPokemon.ability = Abilities.get("marvelscale")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "mirrorcoat", "icebeam", "scald"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Garchomp
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("garchomp")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.YACHE_BERRY), false)  //focus sash
                    npcPokemon.ability = Abilities.get("roughskin")!!.create()
                    npcPokemon.setMoveset(listOf("dragonclaw", "earthquake", "swordsdance", "poisonjab"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "volo" -> {
                    // Spiritomb
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("spiritomb")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,6)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("pressure")!!.create()
                    npcPokemon.setMoveset(listOf("hypnosis", "darkpulse", "extrasensory", "shadowball"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Giratina
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("giratina")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,248)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,12)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)  //expert belt
                    npcPokemon.ability = Abilities.get("pressure")!!.create()
                    npcPokemon.setMoveset(listOf("aurasphere", "dragonclaw", "earthpower", "shadowforce"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Togekiss
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("togekiss")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("serenegrace")!!.create()
                    npcPokemon.setMoveset(listOf("airslash", "calmmind", "extrasensory", "moonblast"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Lucario
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("lucario")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FOCUS_SASH), false)  //focus sash
                    npcPokemon.ability = Abilities.get("innerfocus")!!.create()
                    npcPokemon.setMoveset(listOf("closecombat", "crunch", "bulkup", "bulletpunch"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Milotic // todo replace with Hisuian Arcanine
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("milotic")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,6)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FLAME_ORB), false) //  focus sash
                    npcPokemon.ability = Abilities.get("marvelscale")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "mirrorcoat", "icebeam", "scald"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Garchomp
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("garchomp")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.YACHE_BERRY), false)  //focus sash
                    npcPokemon.ability = Abilities.get("roughskin")!!.create()
                    npcPokemon.setMoveset(listOf("dragonclaw", "earthpower", "earthpower", "slash"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "evice" -> {
                    // Slowking
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("slowking")!!
                    npcPokemon.nature = Natures.getNature("calm")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("owntempo")!!.create()
                    npcPokemon.setMoveset(listOf("shadowball", "psychic", "waterpulse", "skillswap"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Scizor
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("scizor")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.EXPERT_BELT), false)  //expert belt
                    npcPokemon.ability = Abilities.get("technician")!!.create()
                    npcPokemon.setMoveset(listOf("metalclaw", "xscissor", "batonpass", "swordsdance"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Machamp
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("machamp")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FLAME_ORB), false)
                    npcPokemon.ability = Abilities.get("guts")!!.create()
                    npcPokemon.setMoveset(listOf("crosschop", "earthquake", "bulletpunch", "rockslide"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Salamence
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("salamence")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,44)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,212)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)
                    npcPokemon.ability = Abilities.get("intimidate")!!.create()
                    npcPokemon.setMoveset(listOf("dragondance", "doubleedge", "aerialace", "dragonclaw"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Slaking // todo replace with Hisuian Arcanine
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("slaking")!!
                    npcPokemon.nature = Natures.getNature("naive")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)
                    npcPokemon.ability = Abilities.get("truant")!!.create()
                    npcPokemon.setMoveset(listOf("crushclaw", "earthquake", "aerialace", "bulkup"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Tyranitar
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("tyranitar")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)
                    npcPokemon.ability = Abilities.get("sandstream")!!.create()
                    npcPokemon.setMoveset(listOf("phantomforce", "thunder", "rockslide", "blizzard"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "red" -> {
                    // Pikachu
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("pikachu")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIGHT_BALL), false)
                    npcPokemon.ability = Abilities.get("static")!!.create()
                    npcPokemon.setMoveset(listOf("volttackle", "irontail", "quickattack", "brickbreak"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Lapras
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("lapras")!!
                    npcPokemon.nature = Natures.getNature("modest")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("waterabsorb")!!.create()
                    npcPokemon.setMoveset(listOf("rest", "sleeptalk", "surf", "icebeam")) // todo WHY WHYYYYY are the first two not working

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Snorlax
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("snorlax")!!
                    npcPokemon.nature = Natures.getNature("careful")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,188)
                    npcPokemon.setEV(Stats.DEFENCE,128)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,192)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false)  //expert belt
                    npcPokemon.ability = Abilities.get("thickfat")!!.create()
                    npcPokemon.setMoveset(listOf("crunch", "bellydrum", "highhorsepower", "heavyslam"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Charizard
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("charizard")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.CHARTI_BERRY), false)  //focus sash
                    npcPokemon.ability = Abilities.get("blaze")!!.create()
                    npcPokemon.setMoveset(listOf("dragonrush", "rockslide", "flareblitz", "dragondance"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Blastoise
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("blastoise")!!
                    npcPokemon.nature = Natures.getNature("modest")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,216)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,40)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.WACAN_BERRY), false) //  focus sash
                    npcPokemon.ability = Abilities.get("torrent")!!.create()
                    npcPokemon.setMoveset(listOf("hydrocannon", "flashcannon", "darkpulse", "rapidspin"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Venusaur
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("venusaur")!!
                    npcPokemon.nature = Natures.getNature("modest")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.WHITE_HERB), false)  //focus sash
                    npcPokemon.ability = Abilities.get("overgrow")!!.create()
                    npcPokemon.setMoveset(listOf("sludgebomb", "synthesis", "leafstorm", "earthquake"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "blue" -> {
                    // Exeggutor
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("exeggutor")!!
                    npcPokemon.nature = Natures.getNature("quiet")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,8)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.TERRAIN_EXTENDER), false)
                    npcPokemon.ability = Abilities.get("chlorophyll")!!.create()
                    npcPokemon.setMoveset(listOf("leafstorm", "psychic", "explosion", "trickroom"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Gyarados
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("gyarados")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,88)
                    npcPokemon.setEV(Stats.ATTACK,248)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,168)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.WACAN_BERRY), false)
                    npcPokemon.ability = Abilities.get("intimidate")!!.create()
                    npcPokemon.setMoveset(listOf("dragondance", "earthquake", "outrage", "waterfall"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Rhyperior
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("rhyperior")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.ATTACK,16)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,244)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false)  //expert belt
                    npcPokemon.ability = Abilities.get("solidrock")!!.create()
                    npcPokemon.setMoveset(listOf("megahorn", "stoneedge", "highhorsepower", "thunderfang"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Arcanine
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("arcanine")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)  //focus sash
                    npcPokemon.ability = Abilities.get("flashfire")!!.create()
                    npcPokemon.setMoveset(listOf("wildcharge", "flareblitz", "closecombat", "extremespeed"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Tyranitar
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("tyranitar")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false) //  focus sash
                    npcPokemon.ability = Abilities.get("unnerve")!!.create()
                    npcPokemon.setMoveset(listOf("earthquake", "crunch", "icefang", "rockslide"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Alakazam
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("alakazam")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FOCUS_SASH), false)
                    npcPokemon.ability = Abilities.get("magicguard")!!.create()
                    npcPokemon.setMoveset(listOf("dazzlinggleam", "psychic", "shadowball", "recover"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "trickster" -> {
                    // Bronzong
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("bronzong")!!
                    npcPokemon.nature = Natures.getNature("sassy")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.TERRAIN_EXTENDER), false)
                    npcPokemon.ability = Abilities.get("levitate")!!.create()
                    npcPokemon.setMoveset(listOf("trickroom", "selfdestruct", "psychic", "flashcannon"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    /*// Drifblim
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("drifblim")!!
                    npcPokemon.nature = Natures.getNature("relaxed")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,80)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,176)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.TERRAIN_EXTENDER), false)
                    npcPokemon.ability = Abilities.get("aftermath")!!.create()
                    npcPokemon.setMoveset(listOf("explosion", "trickroom", "clearsmog", "shadowball"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))*/

                    // Dusclops
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("dusclops")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.EVIOLITE), false)
                    npcPokemon.ability = Abilities.get("pressure")!!.create()
                    npcPokemon.setMoveset(listOf("willowisp", "haze", "trickroom", "painsplit"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Hatterene
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("hatterene")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.TERRAIN_EXTENDER), false)  //expert belt
                    npcPokemon.ability = Abilities.get("magicbounce")!!.create()
                    npcPokemon.setMoveset(listOf("trickroom", "dazzlinggleam", "psychic", "shadowball"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Rhperior
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("rhyperior")!!
                    npcPokemon.nature = Natures.getNature("relaxed")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ASSAULT_VEST), false)  //focus sash
                    npcPokemon.ability = Abilities.get("solidrock")!!.create()
                    npcPokemon.setMoveset(listOf("highhorsepower", "megahorn", "ironhead", "stoneedge"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Ursaluna
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("ursaluna")!!
                    npcPokemon.nature = Natures.getNature("brave")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FLAME_ORB), false) //  focus sash
                    npcPokemon.ability = Abilities.get("guts")!!.create()
                    npcPokemon.setMoveset(listOf("facade", "crunch", "seedbomb", "headlongrush"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Gholdengo
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("gholdengo")!!
                    npcPokemon.nature = Natures.getNature("quiet")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 0)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.WISE_GLASSES), false)
                    npcPokemon.ability = Abilities.get("goodasgold")!!.create()
                    npcPokemon.setMoveset(listOf("makeitrain", "focusblast", "thunderbolt", "shadowball"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "sandster" -> {
                    // Excadrill
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("excadrill")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("sandrush")!!.create()
                    npcPokemon.setMoveset(listOf("earthquake", "ironhead", "rapidspin", "stealthrock"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Hippowdon stall
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("hippowdon")!!
                    npcPokemon.nature = Natures.getNature("careful")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.ATTACK,8)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("sandstream")!!.create()
                    npcPokemon.setMoveset(listOf("toxic", "icefang", "earthquake", "stealthrock"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Tyranitar
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("tyranitar")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ASSAULT_VEST), false)  //expert belt
                    npcPokemon.ability = Abilities.get("sandstream")!!.create()
                    npcPokemon.setMoveset(listOf("stoneedge", "crunch", "firepunch", "earthquake"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    /*// Cradilly
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("cradily")!!
                    npcPokemon.nature = Natures.getNature("calm")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,4)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)  //focus sash
                    npcPokemon.ability = Abilities.get("stormdrain")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "toxic", "protect", "gigadrain"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))*/

                    // Pyukumuku
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("pyukumuku")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)  //Leftovers when we can track TypeChange easier
                    npcPokemon.ability = Abilities.get("unaware")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "toxic", "protect", "soak")) //replace rest with Soak when it works

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    /*// Toxapex
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("toxapex")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.BLACK_SLUDGE), false)  //Leftovers when we can track TypeChange easier
                    npcPokemon.ability = Abilities.get("Regenerator")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "toxic", "scald", "haze")) //replace rest with Soak when it works

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))*/

                    // Garchomp
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("garchomp")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false) //  focus sash
                    npcPokemon.ability = Abilities.get("sandforce")!!.create()
                    npcPokemon.setMoveset(listOf("stoneedge", "poisonjab", "earthquake", "outrage"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Reuiniculus
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("reuniclus")!!
                    npcPokemon.nature = Natures.getNature("calm")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 0)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,4)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("magicguard")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "thunderwave", "shadowball", "focusblast"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "pivoter" -> {
                    // Greninja
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("greninja")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)
                    npcPokemon.ability = Abilities.get("protean")!!.create()
                    npcPokemon.setMoveset(listOf("darkpulse", "surf", "uturn", "spikes"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Chi-Yu
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("chiyu")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    npcPokemon.teraType = ElementalTypes.GRASS
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.CHOICE_SPECS), false)
                    npcPokemon.ability = Abilities.get("beadsofruin")!!.create()
                    npcPokemon.setMoveset(listOf("overheat", "fireblast", "darkpulse", "terablast"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Magearna
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("magearna")!!
                    npcPokemon.nature = Natures.getNature("sassy")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,248)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,224)
                    npcPokemon.setEV(Stats.SPEED,36)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ASSAULT_VEST), false)  //expert belt
                    npcPokemon.ability = Abilities.get("soulheart")!!.create()
                    npcPokemon.setMoveset(listOf("voltswitch", "ironhead", "icebeam", "focusblast"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Cinderace
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("cinderace")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,224)
                    npcPokemon.setEV(Stats.ATTACK,32)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.HEAVY_DUTY_BOOTS), false)  //Leftovers when we can track TypeChange easier
                    npcPokemon.ability = Abilities.get("libero")!!.create()
                    npcPokemon.setMoveset(listOf("pyroball", "courtchange", "uturn", "willowisp")) //replace rest with Soak when it works

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Scizor
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("scizor")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)  //expert belt
                    npcPokemon.ability = Abilities.get("technician")!!.create()
                    npcPokemon.setMoveset(listOf("bulletpunch", "xscissor", "uturn", "swordsdance"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Corviknight
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("corviknight")!!
                    npcPokemon.nature = Natures.getNature("impish")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 0)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("pressure")!!.create()
                    npcPokemon.setMoveset(listOf("bodypress", "uturn", "defog", "roost"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "leon" -> {
                    // Dragapult
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("dragapult")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,4)
                    npcPokemon.setEV(Stats.ATTACK,248)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.POWER_HERB), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("clearbody")!!.create()
                    npcPokemon.setMoveset(listOf("dragondarts", "psychicfangs", "phantomforce", "dragondance"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Aegislash
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("aegislash")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.EXPERT_BELT), false)  //expert belt
                    npcPokemon.ability = Abilities.get("stancechange")!!.create()
                    npcPokemon.setMoveset(listOf("kingsshield", "shadowsneak", "shadowclaw", "ironhead"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Mr. Rime
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("mrrime")!!
                    npcPokemon.nature = Natures.getNature("modest")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.WISE_GLASSES), false)
                    npcPokemon.ability = Abilities.get("screencleaner")!!.create()
                    npcPokemon.setMoveset(listOf("icebeam", "psychic", "nastyplot", "dazzlinggleam"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Seismitoad
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("seismitoad")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.ATTACK,248)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.MUSCLE_BAND), false)
                    npcPokemon.ability = Abilities.get("poisontouch")!!.create()
                    npcPokemon.setMoveset(listOf("earthquake", "liquidation", "stealthrock", "knockoff"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Rillaboom
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("rillaboom")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.TERRAIN_EXTENDER), false) //  focus sash
                    npcPokemon.ability = Abilities.get("grassysurge")!!.create()
                    npcPokemon.setMoveset(listOf("grassyglide", "acrobatics", "earthquake", "drumbeating"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Charizard
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("charizard")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,4)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)  //focus sash
                    npcPokemon.ability = Abilities.get("solarpower")!!.create()
                    npcPokemon.setMoveset(listOf("ancientpower", "solarbeam", "fireblast", "airslash"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "iris" -> {
                    // Haxorus
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("haxorus")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("Rivalry")!!.create()
                    npcPokemon.setMoveset(listOf("dragondance", "scaleshot", "ironhead", "firstimpression"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Hydreigon
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("hydreigon")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.setEV(Stats.SPEED,248)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ASSAULT_VEST), false)  //expert belt
                    npcPokemon.ability = Abilities.get("levitate")!!.create()
                    npcPokemon.setMoveset(listOf("flashcannon", "darkpulse", "dragonpulse", "flamethrower"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Druddigon
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("druddigon")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ROCKY_HELMET), false)
                    npcPokemon.ability = Abilities.get("roughskin")!!.create()
                    npcPokemon.setMoveset(listOf("stealthrock", "taunt", "suckerpunch", "dragontail"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Lapras
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("lapras")!!
                    npcPokemon.nature = Natures.getNature("quiet")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.ATTACK,8)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,248)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.COVERT_CLOAK), false)
                    npcPokemon.ability = Abilities.get("waterabsorb")!!.create()
                    npcPokemon.setMoveset(listOf("thunderbolt", "freezedry", "surf", "iceshard"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Archeops
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("archeops")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,248)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SILK_SCARF), false) //  focus sash
                    npcPokemon.ability = Abilities.get("defeatist")!!.create()
                    npcPokemon.setMoveset(listOf("acrobatics", "tailwind", "return", "stoneedge"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Aggron
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("aggron")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("heavymetal")!!.create()
                    npcPokemon.setMoveset(listOf("stoneedge", "heavyslam", "thunderpunch", "firepunch"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "sillysun" -> {
                    // Koraidon
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("koraidon")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.CHOICE_SCARF), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("orichalcumpulse")!!.create()
                    npcPokemon.setMoveset(listOf("lowkick", "outrage", "uturn", "flareblitz"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Walking Wake
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("walkingwake")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,12)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,244)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.CHOICE_SPECS), false)  //expert belt
                    npcPokemon.ability = Abilities.get("protosynthesis")!!.create()
                    npcPokemon.setMoveset(listOf("hydrosteam", "dracometeor", "flamethrower", "dragonpulse"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Flutter Mane
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("fluttermane")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)
                    npcPokemon.ability = Abilities.get("protosynthesis")!!.create()
                    npcPokemon.setMoveset(listOf("shadowball", "moonblast", "powergem", "mysticalfire"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Iron Moth
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("ironmoth")!!
                    npcPokemon.nature = Natures.getNature("quiet")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ASSAULT_VEST), false)
                    npcPokemon.ability = Abilities.get("quarkdrive")!!.create()
                    npcPokemon.setMoveset(listOf("fierydance", "sludgewave", "uturn", "energyball"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Ho-Oh
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("hooh")!!
                    npcPokemon.nature = Natures.getNature("impish")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.HEAVY_DUTY_BOOTS), false) //  focus sash
                    npcPokemon.ability = Abilities.get("regenerator")!!.create()
                    npcPokemon.setMoveset(listOf("sacredfire", "bravebird", "toxic", "whirlwind"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Great Tusk
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("greattusk")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("protosynthesis")!!.create()
                    npcPokemon.setMoveset(listOf("stealthrock", "headlongrush", "knockoff", "rapidspin"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "futuremons" -> {
                    // Orthworm
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("orthworm")!!
                    npcPokemon.nature = Natures.getNature("impish")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("eartheater")!!.create()
                    npcPokemon.setMoveset(listOf("shedtail", "spikes", "bodypress", "ironhead"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Miriadon
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("miriadon")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.HEAVY_DUTY_BOOTS), false)  //expert belt
                    npcPokemon.ability = Abilities.get("hadronengine")!!.create()
                    npcPokemon.setMoveset(listOf("dracometeor", "electrodrift", "uturn", "taunt"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Iron Bundle
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("ironbundle")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.HEAVY_DUTY_BOOTS), false)
                    npcPokemon.ability = Abilities.get("quarkdrive")!!.create()
                    npcPokemon.setMoveset(listOf("hydropump", "freezedry", "flipturn", "taunt"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Iron Treads
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("irontreads")!!
                    npcPokemon.nature = Natures.getNature("careful")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.ATTACK,4)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LEFTOVERS), false)
                    npcPokemon.ability = Abilities.get("quarkdrive")!!.create()
                    npcPokemon.setMoveset(listOf("earthquake", "rapidspin", "stealthrock", "voltswitch"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Ho-Oh
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("hooh")!!
                    npcPokemon.nature = Natures.getNature("impish")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.HEAVY_DUTY_BOOTS), false) //  focus sash
                    npcPokemon.ability = Abilities.get("regenerator")!!.create()
                    npcPokemon.setMoveset(listOf("sacredfire", "bravebird", "recover", "whirlwind"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Iron Valiant
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("ironvaliant")!!
                    npcPokemon.nature = Natures.getNature("naive")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,4)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)
                    npcPokemon.ability = Abilities.get("quarkdrive")!!.create()
                    npcPokemon.setMoveset(listOf("moonblast", "closecombat", "knockoff", "thunderbolt"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                "rainster" -> {
                    // Basculegion
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("basculegion")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.CHOICE_BAND), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("adaptability")!!.create()
                    npcPokemon.setMoveset(listOf("wavecrash", "flipturn", "aquajet", "psychicfangs"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Bellibolt
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("bellibolt")!!
                    npcPokemon.nature = Natures.getNature("modest")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.ASSAULT_VEST), false)  //expert belt
                    npcPokemon.ability = Abilities.get("electromorphosis")!!.create()
                    npcPokemon.setMoveset(listOf("thunder", "weatherball", "muddywater", "voltswitch")) //swap muddy water for tera blast fairy

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Dragonite
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("dragonite")!!
                    npcPokemon.nature = Natures.getNature("rash")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,40)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,216)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.EJECT_PACK), false)
                    npcPokemon.ability = Abilities.get("multiscale")!!.create()
                    npcPokemon.setMoveset(listOf("dracometeor", "hurricane", "lowkick", "roost"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Iron Treads
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("irontreads")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,240)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,92)
                    npcPokemon.setEV(Stats.SPEED,176)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.EJECT_BUTTON), false)
                    npcPokemon.ability = Abilities.get("quarkdrive")!!.create()
                    npcPokemon.setMoveset(listOf("earthpower", "rapidspin", "stealthrock", "voltswitch"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Barraskewda
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("barraskewda")!!
                    npcPokemon.nature = Natures.getNature("adamant")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.DEFENCE,4)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.CHOICE_BAND), false) //  focus sash
                    npcPokemon.ability = Abilities.get("swiftswim")!!.create()
                    npcPokemon.setMoveset(listOf("liquidation", "flipturn", "aquajet", "closecombat"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Pelipper
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("pelipper")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,248)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_DEFENCE,8)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.DAMP_ROCK), false)
                    npcPokemon.ability = Abilities.get("drizzle")!!.create()
                    npcPokemon.setMoveset(listOf("surf", "hurricane", "uturn", "roost"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }

                /*"ghetsis" -> {

                }*/
                /*"vgc" -> {

                }*/
                /*"lance" -> {

                }*/
                else -> {
                    val typing = ElementalTypes.get(teamTyping.lowercase())

                    repeat(6) {
                        // todo generate random party of 6 BattlePokemon
                        val npcPokemon = Pokemon()

                        npcPokemon.uuid = UUID.randomUUID()
                        npcPokemon.level = battleLevel
                        npcPokemon.initialize(typing!!) // This will generate everything else about the pokemon

                        npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                    }
                }
            }
        }

        val npcUUID = UUID.randomUUID()

        // randomize team lineup (might be good to keep idk)
        npcParty.shuffle()

        // todo remove this as it is redundant and likely not how we want to do NPC skill levels
        val skill = when (battleAI) {
            "0" -> 0
            "random" -> 0
            "1" -> 1
            "beginner" -> 1
            "2" -> 2
            "trainee" -> 2
            "3" -> 3
            "intermediate" -> 3
            "4" -> 4
            "advanced" -> 4
            "5" -> 5
            "master" -> 5
            else -> 0
        }

        val npcActor = when (teamTyping.lowercase()) {
            "volo" -> TrainerBattleActor("Volo", npcUUID, npcParty.toList(), skill, battleAIType)
            "red" -> TrainerBattleActor("Red", npcUUID, npcParty.toList(), skill, battleAIType)
            "blue" -> TrainerBattleActor("Blue", npcUUID, npcParty.toList(), skill, battleAIType)
            "evice" -> TrainerBattleActor("Evice", npcUUID, npcParty.toList(), skill, battleAIType)
            "cynthia" -> TrainerBattleActor("Cynthia", npcUUID, npcParty.toList(), skill, battleAIType)
            "trickster" -> TrainerBattleActor("Trickster", npcUUID, npcParty.toList(), skill, battleAIType)
            "sandster" -> TrainerBattleActor("Sandster", npcUUID, npcParty.toList(), skill, battleAIType)
            "pivoter" -> TrainerBattleActor("Pivoter", npcUUID, npcParty.toList(), skill, battleAIType)
            "iris" -> TrainerBattleActor("Iris", npcUUID, npcParty.toList(), skill, battleAIType)
            "leon" -> TrainerBattleActor("Leon", npcUUID, npcParty.toList(), skill, battleAIType)
            "sillysun" -> TrainerBattleActor("Freshest Rice", npcUUID, npcParty.toList(), skill, battleAIType)
            "futuremons" -> TrainerBattleActor("Xcavalier", npcUUID, npcParty.toList(), skill, battleAIType)
             //"sunster" -> TrainerBattleActor("Sunster", npcUUID, npcParty.toList(), skill, battleAIType)
            "rainster" -> TrainerBattleActor("Rainster", npcUUID, npcParty.toList(), skill, battleAIType)

            else -> TrainerBattleActor("Master of Crabs", npcUUID, npcParty.toList(), skill, battleAIType)
        }

        val errors = ErroredBattleStart()

        // todo maybe use this error to check for no pokemon in NPC trainer's team
        if (playerActor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
            errors.participantErrors[playerActor] += BattleStartError.insufficientPokemon(
                    player = player,
                    requiredCount = battleFormat.battleType.slotsPerActor,
                    hadCount = playerActor.pokemonList.size
            )
        }

        if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
            errors.participantErrors[playerActor] += BattleStartError.alreadyInBattle(playerActor)
        }

        // todo check if it is okay to remove this later
        // make sure the pokemon on the AI team are all healed
        npcActor.pokemonList.forEach {
            it.effectedPokemon.heal()
        }

        return if (errors.isEmpty) {
            BattleRegistry.startBattle(
                    battleFormat = battleFormat,
                    side1 = BattleSide(playerActor),
                    side2 = BattleSide(npcActor),
                    clonePartyStores = if(cloneParties) listOf(playerClonedPartyStore) else null
            ).ifSuccessful {
                if (!cloneParties) {
                    // todo I don't think I need this
                    //pokemonEntity.battleId.set(Optional.of(it.battleId))
                } else {
                    it.simulation = true
                }
                // todo set the NPC trainer battle music
                //playerActor.battleTheme = pokemonEntity.getBattleTheme()
            }
        } else {
            errors
        }
    }
}

abstract class BattleStartResult {
    open fun ifSuccessful(action: (PokemonBattle) -> Unit): BattleStartResult {
        return this
    }

    open fun ifErrored(action: (ErroredBattleStart) -> Unit): BattleStartResult {
        return this
    }
}
class SuccessfulBattleStart(
    val battle: PokemonBattle
) : BattleStartResult() {
    override fun ifSuccessful(action: (PokemonBattle) -> Unit): BattleStartResult {
        action(battle)
        return this
    }
}

interface BattleStartError {

    fun getMessageFor(entity: Entity): MutableText

    companion object {
        fun alreadyInBattle(player: ServerPlayerEntity) = AlreadyInBattleError(player.uuid, player.displayName)
        fun alreadyInBattle(pokemonEntity: PokemonEntity) = AlreadyInBattleError(pokemonEntity.uuid, pokemonEntity.displayName)
        fun alreadyInBattle(actor: BattleActor) = AlreadyInBattleError(actor.uuid, actor.getName())

        fun targetIsBusy(targetName: MutableText) = BusyError(targetName)
        fun insufficientPokemon(
            player: ServerPlayerEntity,
            requiredCount: Int,
            hadCount: Int
        ) = InsufficientPokemonError(player, requiredCount, hadCount)

        fun canceledByEvent(reason: MutableText?) = CanceledError(reason)
    }
}

enum class CommonBattleStartError : BattleStartError {

}

class CanceledError(
    val reason: MutableText?
): BattleStartError {
    override fun getMessageFor(entity: Entity) = reason ?: battleLang("error.canceled")
}

class InsufficientPokemonError(
    val player: ServerPlayerEntity,
    val requiredCount: Int,
    val hadCount: Int
) : BattleStartError {
    override fun getMessageFor(entity: Entity): MutableText {
        return if (player == entity) {
            val key = if (hadCount == 0) "no_pokemon" else "insufficient_pokemon.personal"
            battleLang(
                "error.$key",
                requiredCount,
                hadCount
            )
        } else {
            battleLang(
                "error.insufficient_pokemon",
                player.displayName,
                requiredCount,
                hadCount
            )
        }
    }
}
class AlreadyInBattleError(
    val actorUUID: UUID,
    val name: Text
): BattleStartError {
    override fun getMessageFor(entity: Entity): MutableText {
        return if (actorUUID == entity.uuid) {
            battleLang("error.in_battle.personal")
        } else {
            battleLang("error.in_battle", name)
        }
    }
}
class BusyError(
    val targetName: MutableText
): BattleStartError {
    override fun getMessageFor(entity: Entity) = battleLang("errors.busy", targetName)
}

open class BattleActorErrors : HashMap<BattleActor, MutableSet<BattleStartError>>() {
    override operator fun get(key: BattleActor): MutableSet<BattleStartError> {
        return super.get(key) ?: mutableSetOf<BattleStartError>().also { this[key] = it }
    }
}

open class ErroredBattleStart(
    val generalErrors: MutableSet<BattleStartError> = mutableSetOf(),
    val participantErrors: BattleActorErrors = BattleActorErrors()
) : BattleStartResult() {
    override fun ifErrored(action: (ErroredBattleStart) -> Unit): BattleStartResult {
        action(this)
        return this
    }

    inline fun <reified T : BattleStartError> forError(action: (T) -> Unit): ErroredBattleStart {
        errors.filterIsInstance<T>().forEach { action(it) }
        return this
    }

    fun sendTo(entity: Entity, transformer: (MutableText) -> (MutableText) = { it }) {
        errors.forEach { entity.sendMessage(transformer(it.getMessageFor(entity))) }
    }

    inline fun <reified T : BattleStartError> ifHasError(action: () -> Unit): ErroredBattleStart {
        if (errors.filterIsInstance<T>().isNotEmpty()) {
            action()
        }
        return this
    }

    val isEmpty: Boolean
        get() = generalErrors.isEmpty() && participantErrors.values.all { it.isEmpty() }

    fun isPlayerToBlame(player: ServerPlayerEntity) = generalErrors.isEmpty()
        && participantErrors.size == 1
        && participantErrors.entries.first().let { it.key.uuid == player.uuid }

    fun isSomePlayerToBlame() = generalErrors.isEmpty() && participantErrors.isNotEmpty()

    val playersToBlame: Iterable<ServerPlayerEntity>
        get() = participantErrors.keys.mapNotNull { it.uuid.getPlayer() }

    val actorsToBlame: Iterable<BattleActor>
        get() = participantErrors.keys

    val errors: Iterable<BattleStartError>
        get() = generalErrors + participantErrors.flatMap { it.value }
}