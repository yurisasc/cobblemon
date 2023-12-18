/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.ai

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.ai.BattleAI
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.moves.Move
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.*
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.battles.*
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon

/**
 * AI that tries to choose the best move for the given situations. Based off of the Pokemon Trainer Tournament Simulator Github
 * https://github.com/cRz-Shadows/Pokemon_Trainer_Tournament_Simulator/blob/main/pokemon-showdown/sim/examples/Simulation-test-1.ts#L330
 *
 * @since December 15th 2023
 */

class ActiveTracker {
    var p1Active: TrackerPokemon = TrackerPokemon()
    var p2Active: TrackerPokemon = TrackerPokemon()

    data class TrackerPokemon(
            var pokemon: Pokemon? = null,
            var species: String? = null,
            var currentHp: Int = 0,
            var currentHpPercent: Double = 0.0,
            var boosts: Map<Stat, Int> = mapOf(),
            var stats: Map<String, Int> = mapOf(),
            var moves: List<Move> = listOf(),
            var nRemainingMons: Int = 0,
            var sideConditions: Map<String, Any> = mapOf(),
            var firstTurn: Int = 0,
            var protectCount: Int = 0
    )
}

class StrongBattleAI(): BattleAI {

    private val entryHazards = listOf("spikes", "stealthrock", "stickyweb", "toxicspikes")
    private val antiHazardsMoves = listOf("rapidspin", "defog", "tidyup")
    private val setupMoves = setOf("tailwind", "trickroom", "auroraveil", "lightscreen", "reflect")
    private val selfRecoveryMoves = listOf("healorder", "milkdrink", "recover", "rest", "roost", "slackoff", "softboiled")
    private val weatherSetupMoves = mapOf(
            "chillyreception" to "Snow",
            "hail" to "Hail",
            "raindance" to "RainDance",
            "sandstorm" to "Sandstorm",
            "snowscape" to "Snow",
            "sunnyday" to "SunnyDay"
    )
    private val speedTierCoefficient = 0.1
    private val hpFractionCoefficient = 0.4
    private val switchOutMatchupThreshold = -2
    private val selfKoMoveMatchupThreshold = 0.3
    private val trickRoomThreshold = 85
    private val recoveryMoveThreshold = 0.4
    private val accuracySwitchThreshold = -3

    // create the active pokemon tracker here
    private val activeTracker = ActiveTracker()

    /*override fun choose(
            activeBattlePokemon: ActiveBattlePokemon,
            moveset: ShowdownMoveset?,
            forceSwitch: Boolean
    ): ShowdownActionResponse {
        if (forceSwitch || activeBattlePokemon.isGone()) {
            val switchTo = activeBattlePokemon.actor.pokemonList.filter { it.canBeSentOut() }.randomOrNull()
                    ?: return DefaultActionResponse() //throw IllegalStateException("Need to switch but no Pokémon to switch to")
            switchTo.willBeSwitchedIn = true
            return SwitchActionResponse(switchTo.uuid)
        }

        if (moveset == null) {
            return PassActionResponse
        }


    }    */

    // get base stats of the pokemon sent in
    fun getBaseStats(pokemon: Pokemon, stat: String): Int {
        return when (stat) {
            "hp" -> pokemon.species.baseStats.getOrDefault(Stats.HP, 0)
            "atk" -> pokemon.species.baseStats.getOrDefault(Stats.ATTACK, 0)
            "spa" -> pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_ATTACK, 0)
            "def" -> pokemon.species.baseStats.getOrDefault(Stats.DEFENCE, 0)
            "spd" -> pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_DEFENCE, 0)
            "spe" -> pokemon.species.baseStats.getOrDefault(Stats.SPEED, 0)
            "total" ->  (pokemon.species.baseStats.getOrDefault(Stats.HP, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.ATTACK, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.DEFENCE, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_DEFENCE, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.SPEED, 0) )
            else -> 0
        }
    }

    fun getIVs(pokemon: Pokemon, stat: String): Int {
        return when (stat) {
            "hp" -> pokemon.ivs.getOrDefault(Stats.HP)
            "atk" -> pokemon.ivs.getOrDefault(Stats.ATTACK)
            "spa" -> pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK)
            "def" -> pokemon.ivs.getOrDefault(Stats.DEFENCE)
            "spd" -> pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE)
            "spe" -> pokemon.ivs.getOrDefault(Stats.SPEED)
            else -> 0
        }
    }

    fun getEVs(pokemon: Pokemon, stat: String): Int {
        return when (stat) {
            "hp" -> pokemon.evs.getOrDefault(Stats.HP)
            "atk" -> pokemon.evs.getOrDefault(Stats.ATTACK)
            "spa" -> pokemon.evs.getOrDefault(Stats.SPECIAL_ATTACK)
            "def" -> pokemon.evs.getOrDefault(Stats.DEFENCE)
            "spd" -> pokemon.evs.getOrDefault(Stats.SPECIAL_DEFENCE)
            "spe" -> pokemon.evs.getOrDefault(Stats.SPEED)
            else -> 0
        }
    }

    // old function definition
    //override fun choose(request: ShowdownActionRequest, active: ActivePokemon, moves: List<MoveChoice>, canDynamax: Boolean, possibleMoves: List<Move>): ShowdownActionResponse {
    override fun choose(activeBattlePokemon: ActiveBattlePokemon, moveset: ShowdownMoveset?, forceSwitch: Boolean): ShowdownActionResponse {

        // get the current battle and set it as a variable
        val battle = activeBattlePokemon.battle
        val request = activeBattlePokemon.actor.request!! // todo idk if this is the right way to do it


        updateActiveTracker(battle)

        //val (mon, opponent) = getCurrentPlayer(battle)

        val mon = activeTracker.p1Active
        val opponent = activeTracker.p2Active

        // Update protect count if it's on cooldown
        if (mon.protectCount > 0) {
            mon.protectCount -= 1
        }

        val currentWeather = battle.contextManager.get(BattleContext.Type.WEATHER)?.iterator()?.next()?.id
        val allMoves = moveset?.moves?.filterNot { it.pp == 0 || it.disabled }

        // Rough estimation of damage ratio
        val physicalRatio = statEstimation(mon, Stats.ATTACK) / statEstimation(opponent, Stats.DEFENCE)
        val specialRatio = statEstimation(mon, Stats.SPECIAL_ATTACK) / statEstimation(opponent, Stats.SPECIAL_DEFENCE)

        // List of all side conditions on each player's side
        val monSideConditionList = mon.sideConditions.keys
        val oppSideConditionList = opponent.sideConditions.keys

        // Decision-making based on move availability and switch-out condition
        if (!moveset?.moves?.isNullOrEmpty()!! && !shouldSwitchOut(request, battle)
                || (request.side?.pokemon?.count { getHpFraction(it.condition) != 0.0 } == 1 && mon.currentHpPercent == 1.0)) {
            val nRemainingMons = mon.nRemainingMons
            val nOppRemainingMons = opponent.nRemainingMons

            // Fake Out
            allMoves?.firstOrNull { it.id == "fakeout" && mon.firstTurn && !opponent.species.types.contains("Ghost") }?.let {
                mon.firstTurn = false
                return Pair(getMoveSlot(it.id), false)
            }

            mon.firstTurn = false

            // Explosion/Self destruct
            allMoves.firstOrNull {
                (it.id == "explosion" || it.id == "selfdestruct")
                        && mon.currentHpPercent < selfKoMoveMatchupThreshold
                        && opponent.currentHpPercent > 0.5
                        && !"Ghost" in opponent.species.types
            }?.let {
                return Pair(getMoveSlot(it.id), false)
            }

            // Deal with non-weather related field changing effects
            for (move in moveset.moves) {
                // Tailwind
                if (move.id == "tailwind" && move.id !in monSideConditionList) {
                    return Pair(getMoveSlot(move.id), false)
                }

                // Trick room
                if (move.id == "trickroom" && move.id !in monSideConditionList
                        && request.side?.pokemon?.count { it.stats.spd <= trickRoomThreshold } >= 3) {
                    return Pair(getMoveSlot(move.id), false)
                }

                // Aurora veil
                if (move.id == "auroraveil" && move.id !in monSideConditionList
                        && currentWeather in listOf("Hail", "Snow")) {
                    return Pair(getMoveSlot(move.id), false)
                }

                // Light Screen
                if (move.id == "lightscreen" && move.id !in monSideConditionList
                        && getBaseStats(opponent,"spa") > getBaseStats(opponent,"atk")) {
                    return Pair(getMoveSlot(move.id), false)
                }

                // Reflect
                if (move.id == "reflect" && move.id !in monSideConditionList
                        && getBaseStats(opponent,"atk") > getBaseStats(opponent,"spa")) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // Entry hazard setup and removal
            for (move in moveset.moves) {
                // Setup
                if (nOppRemainingMons >= 3 && move.id in entryHazards
                        && entryHazards.none { it in oppSideConditionList }) {
                    return Pair(getMoveSlot(move.id), false)
                }

                // Removal
                if (nRemainingMons >= 2 && move.id in antiHazardsMoves
                        && entryHazards.any { it in monSideConditionList }) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // Court Change
            for (move in moveset.moves) {
                if (move.id == "courtchange"
                        && (!entryHazards.none { it in monSideConditionList }
                                || setOf("tailwind", "lightscreen", "reflect").any { it in oppSideConditionList })
                        && setOf("tailwind", "lightscreen", "reflect").none { it in monSideConditionList }
                        && entryHazards.none { it in oppSideConditionList }) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // Self recovery moves
            for (move in moveset.moves) {
                if (move.id in selfRecoveryMoves && mon.currentHpPercent < recoveryMoveThreshold) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // Strength Sap
            for (move in moveset.moves) {
                if (move.id == "strengthsap" && mon.currentHpPercent < 0.5
                        && getBaseStats(opponent,"atk") > 80) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // Weather setup moves
            for (move in moveset.moves) {
                weatherSetupMoves[move.id]?.let { requiredWeather ->
                    if (currentWeather != requiredWeather.toLowerCase() &&
                            !(currentWeather == "PrimordialSea" && requiredWeather == "RainDance") &&
                            !(currentWeather == "DesolateLand" && requiredWeather == "SunnyDay")) {
                        return Pair(getMoveSlot(move.id), false)
                    }
                }
            }

            // Setup moves
            if (mon.currentHpPercent == 1.0 && estimateMatchup(request, battle) > 0) {
                for (move in moveset.moves) {
                    setupMoves[move.id]?.let { statBoosts ->
                        if (statBoosts.any { (stat, boost) -> boost != 0 && (mon.boosts[stat] ?: 0) < 6 }) {
                            if (move.id != "curse" || !"Ghost" in mon.species.types) {
                                return Pair(getMoveSlot(move.id), false)
                            }
                        }
                    }
                }
            }

            // Status Inflicting Moves
            val statusInflictingMoves = loadStatusInflictingMoves() // Load this data as required
            for (move in moveset.moves) {
                val activeOpponent = request.side.foe.pokemon.firstOrNull { it.isActive }
                activeOpponent?.let {
                    // Make sure the opponent doesn't already have a status condition
                    if ((it.volatiles.containsKey("curse") || it.status.isNotEmpty()) &&
                            opponent.currentHpPercent > 0.6 && mon.currentHpPercent > 0.5) {

                        when (statusInflictingMoves[move.id]) {
                            "burn" -> if (!opponent.species.types.contains("Fire") && getBaseStats(opponent,"atk") > 80) {
                                return Pair(getMoveSlot(move.id), false)
                            }

                            "paralysis" -> if (!opponent.species.types.contains("Electric") && getBaseStats(opponent,"spe") > getBaseStats(mon,"spe")) {
                                return Pair(getMoveSlot(move.id), false)
                            }

                            "sleep" -> if (!opponent.species.types.contains("Grass") && (move.id == "spore" || move.id == "sleeppowder") &&
                                    !listOf("insomnia", "sweetveil").contains(request.side.foe.pokemon.ability)) {
                                return Pair(getMoveSlot(move.id), false)
                            }

                            "confusion" -> if (!listOf("Poison", "Steel").any { it in opponent.species.types } &&
                                    !listOf("magicguard", "owntempo", "oblivious").contains(request.side.foe.pokemon.ability)) {
                                return Pair(getMoveSlot(move.id), false)
                            }

                            "poison" -> if (!listOf("Poison", "Steel").any { it in opponent.species.types } &&
                                    !listOf("immunity", "magicguard").contains(request.side.foe.pokemon.ability)) {
                                return Pair(getMoveSlot(move.id), false)
                            }

                            "cursed" -> if ("Ghost" in mon.species.types &&
                                    request.side.foe.pokemon.ability != "magicguard") {
                                return Pair(getMoveSlot(move.id), false)
                            }

                            "leech" -> if (!opponent.species.types.contains("Grass") &&
                                    request.side.foe.pokemon.ability != "magicguard") {
                                return Pair(getMoveSlot(move.id), false)
                            }
                        }
                    }
                }
            }

            // Accuracy lowering moves
            for (move in moveset.moves) {
                if (mon.currentHpPercent == 1.0 && estimateMatchup(request) > 0 &&
                        opponent.boosts["accuracy"] ?: 0 > accuracySwitchThreshold) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // Protect style moves
            for (move in moveset.moves) {
                val activeOpponent = request.side.foe.pokemon.firstOrNull { it.isActive }
                if (move.id in listOf("protect", "banefulbunker", "obstruct", "craftyshield", "detect", "quickguard", "spikyshield", "silktrap")) {
                    // Stall out side conditions
                    if ((oppSideConditionList.intersect(setOf("tailwind", "lightscreen", "reflect", "trickroom")).isNotEmpty() &&
                                    monSideConditionList.intersect(setOf("tailwind", "lightscreen", "reflect")).isEmpty()) ||
                            (activeOpponent?.volatiles?.containsKey("curse") == true || activeOpponent?.status?.isNotEmpty() == true) &&
                            mon.protectCount == 0 && request.side.foe.pokemon.ability != "unseenfist") {
                        mon.protectCount = 2
                        return Pair(getMoveSlot(move.id), false)
                    }
                }
            }

            // Damage dealing moves
            val moveValues = mutableMapOf<String, Double>()
            for (move in moveset.moves) {
                val moveData = dex.getMove(move.id)  // todo find equivalent to whatever the hell this is talking about
                var value = moveData.basePower.toDouble()
                value *= if (moveData.type in mon.species.types) 1.5 else 1.0
                value *= if (moveData.category == "Physical") physicalRatio else specialRatio
                value *= moveData.accuracy
                value *= expectedHits(move.id)
                value *= bestDamageMultiplier(move.id, opponent.species, true)

                // Handle special cases
                if (move.id == "fakeout") {
                    value = 0.0
                }

                val opponentAbility = request.side.foe.pokemon.ability
                if ((opponentAbility == "lightningrod" && moveData.type == "Electric") ||
                        (opponentAbility == "flashfire" && moveData.type == "Fire") ||
                // ... additional ability checks ...
                ) {
                    value = 0.0
                }

                moveValues[move.id] = value
            }

            // uncommment this and try to get it to behave itself. Wants to return no matter what so deal with it later
            val bestMoveValue = moveValues.maxByOrNull { it.value }?.value ?: 0.0
            val bestMove = moveValues.entries.firstOrNull { it.value == bestMoveValue }?.key
            if (bestMove != null && !"recharge" in moveValues) {
                return Pair(getMoveSlot(bestMove), shouldDynamax(request, canDynamax))
            } else {
                return Pair("move 1", shouldDynamax(request, canDynamax))
            }

        }

            // healing wish (dealing with it here because you'd only use it if you should switch out anyway)
            for (move in moveset.moves) {
                if (move.id == "healingwish" && mon.currentHpPercent < selfKoMoveMatchupThreshold) {
                    return Pair(getMoveSlot(move.id), false)
                }
            }

            // switch out
            if (shouldSwitchOut(request, battle)) {
                val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) > 0 }
                val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(request, it) }
                val bestMatchup = availableSwitches?.find { estimateMatchup(request, it) == bestEstimation }
                bestMatchup?.let {
                    return Pair("switch ${getPokemonPos(request, it)}", canDynamax)
                }
            }
            mon.firstTurn = 0

            // otherwise can't find a good option so use a random move
            //return Pair(prng.sample(moves.map { it.choice }), false)
            if (moveset == null) {
                return PassActionResponse
            }
            val move = moveset.moves
                    .filter { it.canBeUsed() }
                    .filter { it.mustBeUsed() || it.target.targetList(activeBattlePokemon)?.isEmpty() != true }
                    .randomOrNull()
                    ?: return MoveActionResponse("struggle")

            val target = if (move.mustBeUsed()) null else move.target.targetList(activeBattlePokemon)
            return if (target == null) {
                MoveActionResponse(move.id)
            } else {
                // prioritize opponents rather than allies
                val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull() ?: target.random()
                MoveActionResponse(move.id, (chosenTarget as ActiveBattlePokemon).getPNX())
            }
        }






    protected fun estimateMatchup(request: ShowdownActionRequest, battle: PokemonBattle, nonActiveMon: Pokemon? = null): Double {
        updateActiveTracker(battle)
        val oppTrainer = getCurrentPlayer(battle)
        var mon = oppTrainer.first.species
        var opponent = oppTrainer.second.species
        nonActiveMon?.let { mon = it }


        var score = 1.0
        score += bestDamageMultiplier(mon, opponent)
        score -= bestDamageMultiplier(opponent, mon)

        if (getBaseStats(mon, "spe") > getBaseStats(opponent, "spe")) {
            score += speedTierCoefficient
        } else if (getBaseStats(opponent, "spe") > getBaseStats(mon, "spe")) {
            score -= speedTierCoefficient
        }

        if (request.side?.id == "p1") {
            score += if (nonActiveMon != null) getCurrentHp(nonActiveMon.condition) * hpFractionCoefficient
            else activeTracker.p1Active.currentHp * hpFractionCoefficient
            score -= activeTracker.p2Active.currentHp * hpFractionCoefficient
        } else {
            score += if (nonActiveMon != null) getCurrentHp(nonActiveMon.condition) * hpFractionCoefficient
            else activeTracker.p2Active.currentHp * hpFractionCoefficient
            score -= activeTracker.p1Active.currentHp * hpFractionCoefficient
        }

        return score
    }

    protected fun estimateMatchupTeamPreview(nonActiveMon: Pokemon, nonActiveOpp: Pokemon): Double {
        val monName = nonActiveMon.species.name
        val oppName = nonActiveOpp.species.name

        var score = 1.0
        score += bestDamageMultiplier(monName, oppName) //todo check what the hell this does
        score -= bestDamageMultiplier(oppName, monName)

        if (getBaseStats(nonActiveMon, "spe") > getBaseStats(nonActiveOpp, "spe")) {
            score += speedTierCoefficient
        } else if (getBaseStats(nonActiveOpp, "spe") > getBaseStats(nonActiveMon, "spe")) {
            score -= speedTierCoefficient
        }

        // Calculate max HP for opponent
        val oppHp = (((2 * getBaseStats(nonActiveOpp, "hp") + getIVs(nonActiveOpp, "hp") + getEVs(nonActiveOpp, "hp") / 4) * nonActiveOpp.level) / 100 + nonActiveOpp.level + 10).toInt()
        score += getCurrentHp(nonActiveMon.condition) * hpFractionCoefficient
        score -= oppHp * hpFractionCoefficient

        return score
    }

    protected fun shouldDynamax(request: ShowdownActionRequest, battle: PokemonBattle, canDynamax: Boolean): Boolean {
        updateActiveTracker(battle)
        if (canDynamax) {
            val (mon, opponent) = getCurrentPlayer(battle)

            // if active mon is the last full HP mon
            if (request.side?.pokemon?.count { getHpFraction(it.condition) == 1.0 } == 1 && mon.currentHp == 1) {
                return true
            }

            // Matchup advantage and full hp on full hp
            if (estimateMatchup(request, battle) > 0 && mon.currentHpPercent == 1.0 && opponent.currentHpPercent == 1.0) {
                return true
            }

            // last pokemon
            if (request.side?.pokemon?.count { getHpFraction(it.condition) != 0.0 } == 1 && mon.currentHpPercent == 1.0) {
                return true
            }
        }
        return false
    }

    protected fun shouldSwitchOut(request: ShowdownActionRequest, battle: PokemonBattle): Boolean {
        updateActiveTracker(battle)

        val (mon, opponent) = getCurrentPlayer(battle)
        val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) != 0.0 }

        val isTrapped = request.active?.any { it ->
            it.trapped ?: false // todo is this the right trapped? Probably not
        }

        // If there is a decent switch in and not trapped...
        if (availableSwitches != null) {
            //if (availableSwitches.any { estimateMatchup(request) > 0 } && !request.side?.pokemon.trapped) {
            if (availableSwitches.any { estimateMatchup(request) > 0 } && !isTrapped!!) {
                // ...and a 'good' reason to switch out
                if ((mon.boosts["accuracy"] ?: 0) <= accuracySwitchThreshold) {
                    return true
                }
                if ((mon.boosts["def"] ?: 0) <= -3 || (mon.boosts["spd"] ?: 0) <= -3) {
                    return true
                }
                if ((mon.boosts["atk"] ?: 0) <= -3 && (mon.stats["atk"] ?: 0) >= (mon.stats["spa"] ?: 0)) {
                    return true
                }
                if ((mon.boosts["spa"] ?: 0) <= -3 && (mon.stats["atk"] ?: 0) <= (mon.stats["spa"] ?: 0)) {
                    return true
                }
                if (estimateMatchup(request) < switchOutMatchupThreshold) {
                    return true
                }
            }
        }
        return false
    }

    protected fun statEstimation(mon: ActiveTracker.TrackerPokemon, stat: Stat): Double {
        val boost = mon.boosts[stat] ?: 0

        val actualBoost = if (boost > 1) {
            (2 + boost) / 2.0
        } else {
            2 / (2.0 - boost)
        }

        val baseStat = getBaseStats(mon, stat) ?: 0
        return ((2 * baseStat + 31) + 5) * actualBoost
    }

        // gets the slot number of the passed-in move
        fun getMoveSlot(move: String, possibleMoves: List<Move>): String {
            val bestMoveSlotIndex = possibleMoves.indexOfFirst { it.id == move } + 1
            return "move $bestMoveSlotIndex"
        }

        // gets the slot number of the bestMatchup pokemon in the team
        fun getPokemonPos(request: ShowdownActionRequest, bestMatchup: Pokemon): Int {
            return request.side?.pokemon?.indexOfFirst {
                it.details == bestMatchup.details && getHpFraction(it.condition) > 0 && !it.active
            } + 1
        }

        // returns an approximate number of hits for a given move for estimation purposes
        fun expectedHits(move: String): Double {
            val moveData = dex.getMove(move) // todo find equivalent of what this wants
            return when (move) {
                "triplekick", "tripleaxel" -> 1 + 2 * 0.9 + 3 * 0.81
                "populationbomb" -> 7.0
                else -> moveData.multihit?.let { (2 + 3) / 3.0 + (4 + 5) / 6.0 } ?: 1.0
            }
        }

        fun chooseSwitch(request: ShowdownActionRequest, battle: PokemonBattle, active: Active?, switches: List<SwitchOption>): Int {
            updateActiveTracker(battle)
            val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) > 0 }
            if (availableSwitches!!.isEmpty()) return 1

            val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(request, it) }
            val bestMatchup = availableSwitches.find { estimateMatchup(request, it) == bestEstimation }
            getCurrentPlayer(battle)[0].firstTurn = 1

            return bestMatchup?.let { getPokemonPos(request, it) } ?: 1
        }

        fun chooseTeamPreview(request: ShowdownActionRequest, battle: PokemonBattle, team: List<AnyObject>): String {
            updateActiveTracker(battle)

            // Uncomment the following line to enable the bot to choose the best mon based on the opponent's team
            // return "team 1"

            val mons = request.side?.pokemon
            val opponentPokemon = request.side.foe.pokemon.map { it.set }
            var bestMon: Pokemon? = null
            var bestAverage: Double? = null

            for (mon in mons) {
                val matchups = opponentPokemon.map { opp -> estimateMatchupTeamPreview(mon, opp) }
                val average = matchups.sum() / matchups.size
                if (bestAverage == null || average > bestAverage) {
                    bestMon = mon
                    bestAverage = average
                }
            }

            // If you have a pokemon with some setup move that will benefit other pokemon on the team, use that first
            for (mon in mons) {
                for (move in mon.moves) {
                    if (weatherSetupMoves.containsKey(move.id) || entryHazards.contains(move.id) ||
                            setupMoves.contains(move.id)) {
                        bestMon = mon
                        break
                    }
                }
            }

            getCurrentPlayer(battle)[0].firstTurn = 1
            return "team ${bestMon?.position?.plus(1)}"
        }

        fun bestDamageMultiplier(attacker: String, defender: String, isMove: Boolean = false): Double {
            val typeMatchups = JSON.parse(File("../Data/UsefulDatasets/type-chart.json").readText())
            val attackerTypes = if (isMove) {
                listOf(Dex.moves.get(attacker).type, "???") // todo find out what it wants for moves
            } else {
                Dex.species.get(attacker).types
            }
            val defenderTypes = Dex.species.get(defender).types

            var multiplier = 1.0
            var bestMultiplier = 1.0

            for (attackerType in attackerTypes) {
                for (defenderType in defenderTypes) {
                    if (attackerType != "???" && defenderType != "???" &&
                            attackerType.isNotEmpty() && defenderType.isNotEmpty()) {
                        multiplier *= (typeMatchups[attackerType]?.get(defenderType) ?: 1.0)
                    }
                }

                if (multiplier > bestMultiplier) {
                    bestMultiplier = multiplier
                }

                multiplier = 1.0
            }

            return bestMultiplier
        }

        private fun fixMove(m: Move): String {
            val id = toID(m.move)
            return when {
                id.startsWith("return") -> "return"
                id.startsWith("frustration") -> "frustration"
                id.startsWith("hiddenpower") -> "hiddenpower"
                else -> id
            }
        }

        private fun getHpFraction(condition: String): Double {
            if (condition == "0 fnt") return 0.0
            val (numerator, denominator) = condition.split('/').map { it.toInt() }
            return numerator.toDouble() / denominator
        }

        private fun getCurrentHp(condition: String): Int {
            if (condition == "0 fnt") return 0
            return condition.split('/')[0].toInt()
        }

        private fun getNonZeroStats(name: String): Map<String, Int> {
            val setupMoves = JSON.parse<Map<String, Map<String, Int>>>(File("../Data/UsefulDatasets/setup_moves.json").readText())
            return setupMoves[name]?.filter { it.value != 0 } ?: emptyMap()
        }

        private fun updateActiveTracker(battle: PokemonBattle) {
            // I think is the first side pokemon
            val p1 = activeTracker.p1Active
            val pokemon1 = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.entity!!.pokemon
            val p1Boosts = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.statChanges
            // todo find out how to get stats
            //val p1Stats = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.

            // convert p1Boosts to a regular Map rather than a MutableMap
            //val p1BoostsMap = p1Boosts?.mapKeys { it.key.toString() } ?: mapOf()
            val p1BoostsMap = p1Boosts?.mapKeys { it.key } ?: mapOf()

            // opposing pokemon to the first side pokemon
            val p2 = activeTracker.p2Active
            val pokemon2 = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.entity!!.pokemon
            val p2Boosts = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.statChanges

            // convert p2Boosts to a regular Map rather than a MutableMap
            //val p2BoostsMap = p2Boosts?.mapKeys { it.key.toString() } ?: mapOf()
            val p2BoostsMap = p2Boosts?.mapKeys { it.key } ?: mapOf()

            p1.pokemon = pokemon1
            p1.species = pokemon1.species.name
            p1.currentHp = pokemon1.currentHealth
            p1.currentHpPercent = (pokemon1.currentHealth / pokemon1.hp).toDouble()
            p1.boosts = p1BoostsMap
            //mon.stats = pokemon.stats
            p1.moves = pokemon1.moveSet.getMoves()
            p1.nRemainingMons = battle.side1.actors.sumOf { actor ->
                actor.pokemonList.count { pokemon ->
                    pokemon.health != 0
                }
            }
            //p1.sideConditions = pokemon.sideConditions   //todo what the hell does this mean

            p2.pokemon = pokemon2
            p2.species = pokemon2.species.name
            p2.currentHp = pokemon2.currentHealth
            p2.currentHpPercent = (pokemon2.currentHealth / pokemon2.hp).toDouble()
            p2.boosts = p2BoostsMap
            //mon.stats = pokemon.stats
            p2.moves = pokemon2.moveSet.getMoves()
            p2.nRemainingMons = battle.side2.actors.sumOf { actor ->
                actor.pokemonList.count { pokemon ->
                    pokemon.health != 0
                }
            }
            //p2.sideConditions = pokemon.sideConditions   //todo what the hell does this mean

        }

        private fun getCurrentPlayer(battle: PokemonBattle): Pair<Pokemon, Pokemon> {
            val mon = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.entity!!.pokemon
            val opponent = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.entity!!.pokemon

            //val mon = if (request.side?.id == "p1") activeTracker.p1Active else activeTracker.p2Active
            //val opponent = if (request.side?.id == "p1") activeTracker.p2Active else activeTracker.p1Active



            return Pair(mon, opponent)
        }


}


