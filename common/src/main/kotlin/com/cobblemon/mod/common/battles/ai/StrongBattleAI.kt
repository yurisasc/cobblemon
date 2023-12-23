/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.ai

import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.abilities.AbilityPool
import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.ai.BattleAI
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.moves.Move
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.moves.categories.DamageCategories
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.*
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.status.Status
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.battles.*
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer

/**
 * AI that tries to choose the best move for the given situations. Based off of the Pokemon Trainer Tournament Simulator Github
 * https://github.com/cRz-Shadows/Pokemon_Trainer_Tournament_Simulator/blob/main/pokemon-showdown/sim/examples/Simulation-test-1.ts#L330
 *
 * @since December 15th 2023
 */
// Define the type for the damage multipliers
typealias TypeEffectivenessMap = Map<String, Map<String, Double>>

fun getDamageMultiplier(attackerType: ElementalType, defenderType: ElementalType): Double {
    return typeEffectiveness[attackerType]?.get(defenderType) ?: 1.0
}

val typeEffectiveness: Map<ElementalType, Map<ElementalType, Double>> = mapOf(
        ElementalTypes.NORMAL to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 0.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.FIRE to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
                ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 2.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 2.0, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.WATER to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 2.0, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 0.5,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 2.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.ELECTRIC to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 2.0, ElementalTypes.ELECTRIC to 0.5, ElementalTypes.GRASS to 0.5,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 0.0, ElementalTypes.FLYING to 2.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.GRASS to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 2.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 0.5,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 2.0, ElementalTypes.FLYING to 0.5,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 0.5, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 0.5,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.ICE to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
                ElementalTypes.ICE to 0.5, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 2.0, ElementalTypes.FLYING to 2.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 2.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.FIGHTING to mapOf(
                ElementalTypes.NORMAL to 2.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 0.5,
                ElementalTypes.PSYCHIC to 0.5, ElementalTypes.BUG to 0.5, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 0.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 2.0, ElementalTypes.STEEL to 2.0, ElementalTypes.FAIRY to 0.5
        ),
        ElementalTypes.POISON to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 0.5, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 0.5, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.0, ElementalTypes.FAIRY to 2.0
        ),
        ElementalTypes.GROUND to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 2.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 2.0, ElementalTypes.GRASS to 0.5,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 2.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 0.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 0.5, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 2.0, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.FLYING to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 0.5, ElementalTypes.GRASS to 2.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 2.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 2.0, ElementalTypes.ROCK to 0.5, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.PSYCHIC to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 2.0, ElementalTypes.POISON to 2.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 0.5, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 0.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.BUG to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 2.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 0.5, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 0.5,
                ElementalTypes.PSYCHIC to 2.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 0.5, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 2.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 0.5
        ),
        ElementalTypes.ROCK to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 2.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 0.5, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 0.5, ElementalTypes.FLYING to 2.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 2.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.GHOST to mapOf(
                ElementalTypes.NORMAL to 0.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 2.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 2.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 0.5, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 1.0
        ),
        ElementalTypes.DRAGON to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 2.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 0.0
        ),
        ElementalTypes.DARK to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 1.0, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 0.5, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 2.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 2.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 0.5, ElementalTypes.STEEL to 1.0, ElementalTypes.FAIRY to 0.5
        ),
        ElementalTypes.STEEL to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 0.5, ElementalTypes.ELECTRIC to 0.5, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 2.0, ElementalTypes.FIGHTING to 1.0, ElementalTypes.POISON to 1.0, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 2.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 1.0,
                ElementalTypes.DARK to 1.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 2.0
        ),
        ElementalTypes.FAIRY to mapOf(
                ElementalTypes.NORMAL to 1.0, ElementalTypes.FIRE to 0.5, ElementalTypes.WATER to 1.0, ElementalTypes.ELECTRIC to 1.0, ElementalTypes.GRASS to 1.0,
                ElementalTypes.ICE to 1.0, ElementalTypes.FIGHTING to 2.0, ElementalTypes.POISON to 0.5, ElementalTypes.GROUND to 1.0, ElementalTypes.FLYING to 1.0,
                ElementalTypes.PSYCHIC to 1.0, ElementalTypes.BUG to 1.0, ElementalTypes.ROCK to 1.0, ElementalTypes.GHOST to 1.0, ElementalTypes.DRAGON to 2.0,
                ElementalTypes.DARK to 2.0, ElementalTypes.STEEL to 0.5, ElementalTypes.FAIRY to 1.0
        )
)

val multiHitMoves: Map<String, Map<Int, Int>> = mapOf(
        // 2 - 5 hit moves
        "armthrust" to mapOf(2 to 5),
        "barrage" to mapOf(2 to 5),
        "bonerush" to mapOf(2 to 5),
        "bulletseed" to mapOf(2 to 5),
        "cometpunch" to mapOf(2 to 5),
        "doubleslap" to mapOf(2 to 5),
        "furyattack" to mapOf(2 to 5),
        "furyswipes" to mapOf(2 to 5),
        "iciclespear" to mapOf(2 to 5),
        "pinmissile" to mapOf(2 to 5),
        "rockblast" to mapOf(2 to 5),
        "scaleshot" to mapOf(2 to 5),
        "spikecannon" to mapOf(2 to 5),
        "tailslap" to mapOf(2 to 5),
        "watershuriken" to mapOf(2 to 5),

        // fixed hit count
        "bonemerang" to mapOf(2 to 2),
        "doublehit" to mapOf(2 to 2),
        "doubleironbash" to mapOf(2 to 2),
        "doublekick" to mapOf(2 to 2),
        "dragondarts" to mapOf(2 to 2),
        "dualchop" to mapOf(2 to 2),
        "dualwingbeat" to mapOf(2 to 2),
        "geargrind" to mapOf(2 to 2),
        "twinbeam" to mapOf(2 to 2),
        "twineedle" to mapOf(2 to 2),
        "suringstrikes" to mapOf(3 to 3),
        "tripledive" to mapOf(3 to 3),
        "watershuriken" to mapOf(3 to 3),

        // accuracy based multi-hit moves
        "tripleaxel" to mapOf(1 to 3),
        "triplekick" to mapOf(1 to 3),
        "populationbomb" to mapOf(1 to 10)
)

val statusMoves: Map<MoveTemplate?, String> = mapOf(
        Moves.getByName("willowisp") to Statuses.BURN.showdownName,
        Moves.getByName("scald") to Statuses.BURN.showdownName,
        Moves.getByName("scorchingsands") to Statuses.BURN.showdownName,
        Moves.getByName("glare") to Statuses.PARALYSIS.showdownName,
        Moves.getByName("nuzzle") to Statuses.PARALYSIS.showdownName,
        Moves.getByName("stunspore") to Statuses.PARALYSIS.showdownName,
        Moves.getByName("thunderwave") to Statuses.PARALYSIS.showdownName,
        Moves.getByName("Nuzzle") to Statuses.PARALYSIS.showdownName,
        Moves.getByName("darkvoid") to Statuses.SLEEP.showdownName,
        Moves.getByName("hypnosis") to Statuses.SLEEP.showdownName,
        Moves.getByName("lovelykiss") to Statuses.SLEEP.showdownName,
        Moves.getByName("relicsong") to Statuses.SLEEP.showdownName,
        Moves.getByName("sing") to Statuses.SLEEP.showdownName,
        Moves.getByName("sleeppower") to Statuses.SLEEP.showdownName,
        Moves.getByName("spore") to Statuses.SLEEP.showdownName,
        Moves.getByName("yawn") to Statuses.SLEEP.showdownName,
        Moves.getByName("chatter") to "confusion",
        Moves.getByName("confuseray") to "confusion",
        Moves.getByName("dynamicpunch") to "confusion",
        Moves.getByName("flatter") to "confusion",
        Moves.getByName("supersonic") to "confusion",
        Moves.getByName("swagger") to "confusion",
        Moves.getByName("sweetkiss") to "confusion",
        Moves.getByName("teeterdance") to "confusion",
        Moves.getByName("poisongas") to Statuses.POISON.showdownName,
        Moves.getByName("poisonpowder") to Statuses.POISON.showdownName,
        Moves.getByName("toxic") to Statuses.POISON_BADLY.showdownName,
        Moves.getByName("toxicthread") to Statuses.POISON.showdownName,
        Moves.getByName("curse") to "cursed",
        Moves.getByName("leechseed") to "leech"
)

val boostFromMoves: Map<String, Map<Stat, Int>> = mapOf(
        "bellydrum" to mapOf(Stats.ATTACK to 6),
        "bulkup" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1),
        "clangoroussoul" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
        "coil" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.ACCURACY to 1),
        "dragondance" to mapOf(Stats.ATTACK to 1, Stats.SPEED to 1),
        "extremeevoboost" to mapOf(Stats.ATTACK to 2, Stats.DEFENCE to 2, Stats.SPECIAL_ATTACK to 2, Stats.SPECIAL_DEFENCE to 2, Stats.SPEED to 2),
        "clangoroussoulblaze" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
        "filletaway" to mapOf(Stats.ATTACK to 2, Stats.SPECIAL_ATTACK to 2, Stats.SPEED to 2),
        "honeclaws" to mapOf(Stats.ATTACK to 1, Stats.ACCURACY to 1),
        "noretreat" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
        "shellsmash" to mapOf(Stats.ATTACK to 2, Stats.DEFENCE to -1, Stats.SPECIAL_ATTACK to 2, Stats.SPECIAL_DEFENCE to -1, Stats.SPEED to 2),
        "shiftgear" to mapOf(Stats.ATTACK to 1, Stats.SPEED to 2),
        "swordsdance" to mapOf(Stats.ATTACK to 2),
        "tidyup" to mapOf(Stats.ATTACK to 1, Stats.SPEED to 1),
        "victorydance" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPEED to 1),
        "acidarmor" to mapOf(Stats.DEFENCE to 2),
        "barrier" to mapOf(Stats.DEFENCE to 2),
        "cottonguard" to mapOf(Stats.DEFENCE to 3),
        "defensecurl" to mapOf(Stats.DEFENCE to 1),
        "irondefense" to mapOf(Stats.DEFENCE to 2),
        "shelter" to mapOf(Stats.DEFENCE to 2, Stats.EVASION to 1),
        "stockpile" to mapOf(Stats.DEFENCE to 1, Stats.SPECIAL_DEFENCE to 1),
        "stuffcheeks" to mapOf(Stats.DEFENCE to 2),
        "amnesia" to mapOf(Stats.SPECIAL_DEFENCE to 2),
        "calmmind" to mapOf(Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1),
        "geomancy" to mapOf(Stats.SPECIAL_ATTACK to 2, Stats.SPECIAL_DEFENCE to 2, Stats.SPEED to 2),
        "nastyplot" to mapOf(Stats.SPECIAL_ATTACK to 2),
        "quiverdance" to mapOf(Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1, Stats.SPEED to 1),
        "tailglow" to mapOf(Stats.SPECIAL_ATTACK to 3),
        "takeheart" to mapOf(Stats.SPECIAL_ATTACK to 1, Stats.SPECIAL_DEFENCE to 1),
        "agility" to mapOf(Stats.SPEED to 2),
        "autotomize" to mapOf(Stats.SPEED to 2),
        "rockpolish" to mapOf(Stats.SPEED to 2),
        "curse" to mapOf(Stats.ATTACK to 1, Stats.DEFENCE to 1, Stats.SPEED to -1),
        "minimize" to mapOf(Stats.EVASION to 2)
)


class ActiveTracker {
    var p1Active: TrackerPokemon = TrackerPokemon()
    var p2Active: TrackerPokemon = TrackerPokemon()

    data class TrackerPokemon(
            var pokemon: Pokemon? = null,
            var species: String? = null,
            var currentHp: Int = 0,
            var currentHpPercent: Double = 0.0,
            var boosts: Map<Stat, Int> = mapOf(),
            var stats: Map<Stat, Int> = mapOf(),
            var moves: List<Move> = listOf(),
            var nRemainingMons: Int = 0,
            var sideConditions: Map<String, Any> = mapOf(),
            var firstTurn: Int = 0,
            var protectCount: Int = 0
    )
}

class StrongBattleAI() : BattleAI {

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
            "total" -> (pokemon.species.baseStats.getOrDefault(Stats.HP, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.ATTACK, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.DEFENCE, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.SPECIAL_DEFENCE, 0) +
                    pokemon.species.baseStats.getOrDefault(Stats.SPEED, 0))

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
        val p1Actor = battle.side1.actors.first()
        val p2Actor = battle.side2.actors.first()

        val playerSide2 = battle.side2.actors.first()
        // todo make nice function for knowing what is the best switchout

        val canDynamax = false

        if (forceSwitch || activeBattlePokemon.isGone()) {
            val switchTo = activeBattlePokemon.actor.pokemonList.filter { it.canBeSentOut() }.randomOrNull()
                    ?: return DefaultActionResponse() //throw IllegalStateException("Need to switch but no Pokémon to switch to")
            switchTo.willBeSwitchedIn = true
            return SwitchActionResponse(switchTo.uuid)
        }

        updateActiveTracker(battle)

        //val (mon, opponent) = getCurrentPlayer(battle)

        // sync up the current pokemon that is choosing the moves
        val (mon, opponent) = if (activeBattlePokemon.battlePokemon!!.effectedPokemon.uuid == activeTracker.p1Active.pokemon!!.uuid) {
            Pair(activeTracker.p1Active, activeTracker.p2Active)
        } else {
            Pair(activeTracker.p2Active, activeTracker.p1Active)
        }

        //val mon = activeTracker.p1Active
        //val opponent = activeTracker.p2Active

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

        // todo Assess Damger level of current pokemon based on current HP and matchup pokemon
        // todo Try to find a way to store a list of moves each pokemon in the battle has used so that the AI can learn and decide differently over time
        // todo try to caclulate if it is worth it to use status moves somehow

        // Decision-making based on move availability and switch-out condition
        if (!moveset?.moves?.isNullOrEmpty()!! && !shouldSwitchOut(request, battle) ||
                (request.side?.pokemon?.count { getHpFraction(it.condition) != 0.0 } == 1 && mon.currentHpPercent == 1.0)) {
            val nRemainingMons = mon.nRemainingMons
            val nOppRemainingMons = opponent.nRemainingMons

            // Fake Out
            allMoves?.firstOrNull { it.id == "fakeout" && mon.firstTurn == 1 && !opponent.pokemon?.types?.contains(ElementalTypes.GHOST)!! }?.let {
                mon.firstTurn = 0
                return MoveActionResponse(it.id)
            }

            mon.firstTurn = 0

            // Explosion/Self destruct
            allMoves?.firstOrNull {
                (it.id.equals("explosion") || it.id.equals("selfdestruct"))
                        && mon.currentHpPercent < selfKoMoveMatchupThreshold
                        && opponent.currentHpPercent > 0.5
                        && ElementalTypes.GHOST !in opponent.pokemon!!.types
            }?.let {
                MoveActionResponse(it.id)
            }

            // Deal with non-weather related field changing effects
            for (move in moveset.moves) {
                // Tailwind
                if (move.id == "tailwind" && move.id !in monSideConditionList) {
                    return MoveActionResponse(move.id)
                }

                // Trick room
                if (move.id == "trickroom" && move.id !in monSideConditionList
                        && request.side?.pokemon?.count { statEstimation(mon, Stats.SPEED) <= trickRoomThreshold }!! >= 3) {
                    return MoveActionResponse(move.id)
                }

                // Aurora veil
                if (move.id == "auroraveil" && move.id !in monSideConditionList
                        && currentWeather in listOf("Hail", "Snow")) {
                    return MoveActionResponse(move.id)
                }

                // Light Screen
                if (move.id == "lightscreen" && move.id !in monSideConditionList
                        && getBaseStats(opponent.pokemon!!, "spa") > getBaseStats(opponent.pokemon!!, "atk")) {
                    return MoveActionResponse(move.id)
                }

                // Reflect
                if (move.id == "reflect" && move.id !in monSideConditionList
                        && getBaseStats(opponent.pokemon!!, "atk") > getBaseStats(opponent.pokemon!!, "spa")) {
                    return MoveActionResponse(move.id)
                }
            }

            // Entry hazard setup and removal
            for (move in moveset.moves) {
                // Setup
                if (nOppRemainingMons >= 3 && move.id in entryHazards
                        && entryHazards.none { it in oppSideConditionList }) {
                    return MoveActionResponse(move.id)
                }

                // Removal
                if (nRemainingMons >= 2 && move.id in antiHazardsMoves
                        && entryHazards.any { it in monSideConditionList }) {
                    return MoveActionResponse(move.id)
                }
            }

            // Court Change
            for (move in moveset.moves) {
                if (move.id == "courtchange"
                        && (!entryHazards.none { it in monSideConditionList }
                                || setOf("tailwind", "lightscreen", "reflect").any { it in oppSideConditionList })
                        && setOf("tailwind", "lightscreen", "reflect").none { it in monSideConditionList }
                        && entryHazards.none { it in oppSideConditionList }) {
                    return MoveActionResponse(move.id)
                }
            }

            // Self recovery moves
            for (move in moveset.moves) {
                if (move.id in selfRecoveryMoves && mon.currentHpPercent < recoveryMoveThreshold) {
                    return MoveActionResponse(move.id)
                }
            }

            // Strength Sap
            for (move in moveset.moves) {
                if (move.id == "strengthsap" && mon.currentHpPercent < 0.5
                        && getBaseStats(opponent.pokemon!!, "atk") > 80) {
                    return MoveActionResponse(move.id)
                }
            }

            // Weather setup moves
            for (move in moveset.moves) {
                weatherSetupMoves[move.id]?.let { requiredWeather ->
                    if (currentWeather != requiredWeather.toLowerCase() &&
                            !(currentWeather == "PrimordialSea" && requiredWeather == "RainDance") &&
                            !(currentWeather == "DesolateLand" && requiredWeather == "SunnyDay")) {
                        return MoveActionResponse(move.id)
                    }
                }
            }

            // Setup moves
            if (mon.currentHpPercent == 1.0 && estimateMatchup(request, battle) > 0) {
                for (move in moveset.moves) {
                    if (setupMoves.contains(move.id) && (getNonZeroStats(move.id).keys.minOfOrNull {
                                mon.boosts[it] ?: 0
                            }!! < 6)) {
                        if (!move.id.equals("curse") || ElementalTypes.GHOST !in mon.pokemon!!.types) {
                            return MoveActionResponse(move.id)
                        }
                    }
                }
            }
            fun hasMajorStatusImmunity(target: ActiveTracker.TrackerPokemon) : Boolean {
                // TODO: Need to check for Safeguard and Misty Terrain
                return listOf("comatose", "purifyingsalt").contains(opponent.pokemon!!.ability.name) &&
                        (currentWeather == "sunny" && opponent.pokemon!!.ability.name == "leafguard");
            }
            // Status Inflicting Moves
            for (move in moveset.moves) {
                val activeOpponent = opponent.pokemon
                activeOpponent?.let {
                    // Make sure the opponent doesn't already have a status condition
                    //if ((it.volatiles.containsKey("curse") || it.status != null) && // todo I removed this because idk why you would need to know if it had curse
                    if (it.status != null && opponent.currentHpPercent > 0.6 && mon.currentHpPercent > 0.5) { // todo make sure this is the right status to use. It might not be

                        when (statusMoves.get(Moves.getByName(move.id))) {
                            "burn" -> if (!opponent.pokemon!!.types.contains(ElementalTypes.FIRE) && getBaseStats(opponent.pokemon!!, "atk") > 80 &&
                                    !hasMajorStatusImmunity(opponent) &&
                                    !listOf("waterbubble", "waterveil", "flareboost", "guts", "magicguard").contains(opponent.pokemon!!.ability.name)) {
                                MoveActionResponse(move.id)
                            }

                            "paralysis" -> if (!opponent.pokemon!!.types.contains(ElementalTypes.ELECTRIC) && getBaseStats(opponent.pokemon!!, "spe") > getBaseStats(mon.pokemon!!, "spe") &&
                                    !hasMajorStatusImmunity(opponent) &&
                                    !listOf("limber", "guts").contains(opponent.pokemon!!.ability.name)) {
                                MoveActionResponse(move.id)
                            }

                            "sleep" -> if (!opponent.pokemon!!.types.contains(ElementalTypes.GRASS) && (move.id.equals("spore") || move.id.equals("sleeppowder")) &&
                                    !hasMajorStatusImmunity(opponent) &&
                                    !listOf("insomnia", "sweetveil").contains(opponent.pokemon!!.ability.name)) {
                                MoveActionResponse(move.id)
                            }

                            "confusion" -> if (!listOf("owntempo", "oblivious").contains(opponent.pokemon!!.ability.name)) {
                                MoveActionResponse(move.id)
                            }

                            "poison" -> if (!listOf(ElementalTypes.POISON, ElementalTypes.STEEL).any { it in opponent.pokemon!!.types } &&
                                    !hasMajorStatusImmunity(opponent) &&
                                    !listOf("immunity", "poisonheal", "guts", "magicguard").contains(opponent.pokemon!!.ability.name)) {
                                MoveActionResponse(move.id)
                            }

                            "cursed" -> if (ElementalTypes.GHOST in mon.pokemon!!.types &&
                                    !opponent.pokemon!!.ability.name.equals("magicguard")) {
                                MoveActionResponse(move.id)
                            }

                            "leech" -> if (!opponent.pokemon!!.types.contains(ElementalTypes.GRASS) &&
                                    !listOf("liquidooze", "magicguard").contains(opponent.pokemon!!.ability.name)) {
                                MoveActionResponse(move.id)
                            }
                        }
                    }
                }
            }

            // Accuracy lowering moves // todo seems to get stuck here. Try to check if it is an accuracy lowering move first before entering
            for (move in moveset.moves) {
                if (1 == 2 && mon.currentHpPercent == 1.0 && estimateMatchup(request, battle) > 0 &&
                        (opponent.boosts[Stats.ACCURACY] ?: 0) > accuracySwitchThreshold) {
                    return MoveActionResponse(move.id)
                }
            }
            //PersistentStatusContainer
            // Protect style moves
            for (move in moveset.moves) {
                val activeOpponent = opponent.pokemon
                if (move.id in listOf("protect", "banefulbunker", "obstruct", "craftyshield", "detect", "quickguard", "spikyshield", "silktrap")) {
                    // Stall out side conditions
                    if ((oppSideConditionList.intersect(setOf("tailwind", "lightscreen", "reflect", "trickroom")).isNotEmpty() &&
                                    monSideConditionList.intersect(setOf("tailwind", "lightscreen", "reflect")).isEmpty()) ||
                            //(activeOpponent?.volatiles?.containsKey("curse") == true || (activeOpponent?.status == null)) && // todo I think this is the wrong status
                            (activeOpponent?.status == null) && // todo I think this is the wrong status
                            mon.protectCount == 0 && opponent.pokemon!!.ability.name != "unseenfist") {
                        mon.protectCount = 2
                        return MoveActionResponse(move.id)
                    }
                }
            }

            // Damage dealing moves
            val moveValues = mutableMapOf<InBattleMove, Double>()
            for (move in moveset.moves) {
                val moveData = Moves.getByName(move.id)
                /*var value = moveData!!.power
                value *= if (moveData.elementalType in mon.pokemon!!.types) 1.5 else 1.0 // STAB
                value *= if (moveData.damageCategory == DamageCategories.PHYSICAL) physicalRatio else specialRatio
                //value *= moveData.accuracy // todo look into better way to take accuracy into account
                value *= expectedHits(Moves.getByName(move.id)!!)
                value *= moveDamageMultiplier(move.id, opponent.pokemon!!)*/

                // Attempt at better estimation
                val movePower = moveData!!.power
                val pokemonLevel = mon.pokemon!!.level
                val statRatio = if (moveData.damageCategory == DamageCategories.PHYSICAL) physicalRatio else specialRatio

                val STAB = when {
                    moveData.elementalType in mon.pokemon!!.types && mon.pokemon!!.ability.name == "adaptability" -> 2.0
                    moveData.elementalType in mon.pokemon!!.types -> 1.5
                    else -> 1.0
                }
                val weather = when {
                    // Sunny Weather
                    currentWeather == "sunny" && (moveData.elementalType == ElementalTypes.FIRE || moveData.name == "hydrosteam") -> 1.5
                    currentWeather == "sunny" && moveData.elementalType == ElementalTypes.WATER && moveData.name != "hydrosteam" -> 0.5

                    // Rainy Weather
                    currentWeather == "raining" && moveData.elementalType == ElementalTypes.WATER-> 1.5
                    currentWeather == "raining" && moveData.elementalType == ElementalTypes.FIRE-> 0.5

                    // Add other cases below for weather

                    else -> 1.0
                }
                val damageTypeMultiplier = moveDamageMultiplier(move.id, opponent.pokemon!!)
                val burn = when {
                    opponent.pokemon!!.status?.status?.showdownName == "burn" && moveData.damageCategory == DamageCategories.PHYSICAL -> 0.5
                    else -> 1.0
                }

                var damage = (((((2 * pokemonLevel) / 5 ) + 2) * movePower * statRatio) / 50 + 2)
                damage *= weather
                damage *= STAB
                damage *= damageTypeMultiplier
                damage *= burn

                var value = damage // set value to be the output of damage


                // HOW DAMAGE IS ACTUALLY CALCULATED
                // REFERENCES: https://bulbapedia.bulbagarden.net/wiki/Damage
                // Damage = (((((2 * pokemon.level) / 5 ) + 2) * move.power * (mon.attackStat / opponent.defenseStat)) / 50 + 2)
                // Damage *= Targets // 0.75 (0.5 in Battle Royals) if the move has more than one target when the move is executed, and 1 otherwise.
                // Damage *= PB // 0.25 (0.5 in Generation VI) if the move is the second strike of Parental Bond, and 1 otherwise
                // Damage *= Weather // 1.5 if a Water-type move is being used during rain or a Fire-type move or Hydro Steam during harsh sunlight, and 0.5 if a Water-type move (besides Hydro Steam) is used during harsh sunlight or a Fire-type move during rain, and 1 otherwise or if any Pokémon on the field have the Ability Cloud Nine or Air Lock.
                // Damage *= GlaiveRush // 2 if the target used the move Glaive Rush in the previous turn, or 1 otherwise.
                // Damage *= Critical // 1.5 (2 in Generation V) for a critical hit, and 1 otherwise. Decimals are rounded down to the nearest integer. It is always 1 if the target's Ability is Battle Armor or Shell Armor or if the target is under the effect of Lucky Chant.
                // Damage *= randomNumber // random number between .85 and 1.00
                // Damage *= STAB // 1.5 if mon.types is equal to move.type or if it is a combined Pledge move || 2.0 if it has adaptability || Terra gimmick has other rules
                // Damage *= Type // type damage multipliers || CHeck website for additional rules for some moves
                // Damage *= Burn // 0.5 if the pokemon is burned, its Ability is not Guts, and the used move is a physical move (other than Facade from Generation VI onward), and 1
                // Damage *= Other // 1 in most cases, and a different multiplier when specific interactions of moves, Abilities, or items take effect, in this order
                // Damage *= ZMove // 1 usually OR 0.25 if the move is a Z-Move, Max Move, or G-Max Move being used into a protection move
                // Damage *= TeraShield // ONLY for Terra raid battles


                // Handle special cases
                if (move.id.equals("fakeout")) {
                    value = 0.0
                }

                val opponentAbility = opponent.pokemon!!.ability
                if ((opponentAbility.template.name.equals("lightningrod") && moveData.elementalType == ElementalTypes.ELECTRIC) ||
                        (opponentAbility.template.name.equals("flashfire") && moveData.elementalType == ElementalTypes.FIRE) ||
                        (opponentAbility.template.name.equals("levitate") && moveData.elementalType == ElementalTypes.GROUND) ||
                        (opponentAbility.template.name.equals("sapsipper") && moveData.elementalType == ElementalTypes.GRASS) ||
                        (opponentAbility.template.name.equals("motordrive") && moveData.elementalType == ElementalTypes.ELECTRIC) ||
                        (opponentAbility.template.name.equals("stormdrain") && moveData.elementalType == ElementalTypes.WATER) ||
                        (opponentAbility.template.name.equals("voltabsorb") && moveData.elementalType == ElementalTypes.ELECTRIC) ||
                        (opponentAbility.template.name.equals("waterabsorb") && moveData.elementalType == ElementalTypes.WATER) ||
                        (opponentAbility.template.name.equals("immunity") && moveData.elementalType == ElementalTypes.POISON) ||
                        (opponentAbility.template.name.equals("eartheater") && moveData.elementalType == ElementalTypes.GROUND) ||
                        (opponentAbility.template.name.equals("suctioncup") && moveData.name == "roar" || moveData.name == "whirlwind")
                ) {
                    value = 0.0
                }

                moveValues[move] = value
            }

            // uncommment this and try to get it to behave itself. Wants to return no matter what so deal with it later
            val bestMoveValue = moveValues.maxByOrNull { it.value }?.value ?: 0.0
            val bestMove = moveValues.entries.firstOrNull { it.value == bestMoveValue }?.key
            val target = if (bestMove!!.mustBeUsed()) null else bestMove.target.targetList(activeBattlePokemon)
            if (allMoves != null) {
                if (allMoves.none { it.id == "recharge" || it.id == "struggle" }) {  //"recharge" !in moveValues) {
                    if (target == null) {
                        return MoveActionResponse(bestMove.id)
                    }
                    else {
                        //return MoveActionResponse(getMoveSlot(bestMove, allMoves))//, false) //shouldDynamax(request, canDynamax))
                        val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull()
                                ?: target.random()

                        return MoveActionResponse(bestMove.id, (chosenTarget as ActiveBattlePokemon).getPNX())
                    }
                } else {
                    if (target == null) {
                        return MoveActionResponse(allMoves.first().id)
                    }
                    else{
                        val chosenTarget = target.filter { !it.isAllied(activeBattlePokemon) }.randomOrNull()
                                ?: target.random()

                        return MoveActionResponse(allMoves.first().id, (chosenTarget as ActiveBattlePokemon).getPNX())
                    }

                            //?: Moves.getByName("struggle")!!.name) //, false) //shouldDynamax(request, canDynamax))
                }
            }

        }

        // healing wish (dealing with it here because you'd only use it if you should switch out anyway)
        for (move in moveset.moves) {
            if (move.id.equals("healingwish") && mon.currentHpPercent < selfKoMoveMatchupThreshold) {
                return MoveActionResponse(move.id)
            }
        }


        // switch out
        if (shouldSwitchOut(request, battle)) {
            val availableSwitches = p1Actor.pokemonList.filter { it.uuid != mon.pokemon!!.uuid && it.health > 0 }
            val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(request, battle, it.effectedPokemon) }
            availableSwitches.forEach {
                estimateMatchup(request, battle, it.effectedPokemon)
            }
            val bestMatchup = availableSwitches.find { estimateMatchup(request, battle, it.effectedPokemon) == bestEstimation }
            bestMatchup?.let {
                return SwitchActionResponse(it.uuid)
                //Pair("switch ${getPokemonPos(request, it)}", canDynamax)
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

    fun estimateMatchup(request: ShowdownActionRequest, battle: PokemonBattle, nonActiveMon: Pokemon? = null): Double {
        updateActiveTracker(battle)
        val battlePokemon = getCurrentPlayer(battle)
        var mon = battlePokemon.first
        var opponent = battlePokemon.second
        nonActiveMon?.let { mon = it }

        //var p1NonActiveMon

        var score = 1.0
        score += bestDamageMultiplier(mon, opponent)
        score -= bestDamageMultiplier(opponent, mon)

        if (getBaseStats(mon, "spe") > getBaseStats(opponent, "spe")) {
            score += speedTierCoefficient
        } else if (getBaseStats(opponent, "spe") > getBaseStats(mon, "spe")) {
            score -= speedTierCoefficient
        }

        if (request.side?.id == "p1") {
            score += if (nonActiveMon != null) nonActiveMon.currentHealth * hpFractionCoefficient
            else activeTracker.p1Active.currentHp * hpFractionCoefficient
            score -= activeTracker.p2Active.currentHp * hpFractionCoefficient
        } else {
            score += if (nonActiveMon != null) nonActiveMon.currentHealth * hpFractionCoefficient
            else activeTracker.p2Active.currentHp * hpFractionCoefficient
            score -= activeTracker.p1Active.currentHp * hpFractionCoefficient
        }

        return score
    }

    /*fun estimateMatchupTeamPreview(nonActiveMon: Pokemon, nonActiveOpp: Pokemon): Double {
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
    }*/

    fun shouldDynamax(request: ShowdownActionRequest, battle: PokemonBattle, canDynamax: Boolean): Boolean {
        updateActiveTracker(battle)
        if (canDynamax) {
            //val (mon, opponent) = getCurrentPlayer(battle)

            val mon = activeTracker.p1Active
            val opponent = activeTracker.p2Active

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

    fun shouldSwitchOut(request: ShowdownActionRequest, battle: PokemonBattle): Boolean {
        updateActiveTracker(battle)

        //val (mon, opponent) = getCurrentPlayer(battle)
        val mon = activeTracker.p1Active
        val opponent = activeTracker.p2Active

        val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) != 0.0 }

        val isTrapped = request.active?.any { it ->
            it.trapped ?: false // todo is this the right trapped? Probably not
        }

        // If there is a decent switch in and not trapped...
        if (availableSwitches != null) {
            //if (availableSwitches.any { estimateMatchup(request) > 0 } && !request.side?.pokemon.trapped) {
            if (availableSwitches.any { estimateMatchup(request, battle) > 0 } && !isTrapped!!) {
                // ...and a 'good' reason to switch out
                if ((mon.boosts[Stats.ACCURACY] ?: 0) <= accuracySwitchThreshold) {
                    return true
                }
                if ((mon.boosts[Stats.DEFENCE] ?: 0) <= -3 || (mon.boosts[Stats.SPECIAL_DEFENCE] ?: 0) <= -3) {
                    return true
                }
                if ((mon.boosts[Stats.ATTACK] ?: 0) <= -3 && (mon.stats[Stats.ATTACK]
                                ?: 0) >= (mon.stats[Stats.SPECIAL_ATTACK] ?: 0)) {
                    return true
                }
                if ((mon.boosts[Stats.SPECIAL_ATTACK] ?: 0) <= -3 && (mon.stats[Stats.ATTACK]
                                ?: 0) <= (mon.stats[Stats.SPECIAL_ATTACK] ?: 0)) {
                    return true
                }
                if (estimateMatchup(request, battle) < switchOutMatchupThreshold) {
                    return true
                }
            }
        }
        return false
    }

    fun statEstimation(mon: ActiveTracker.TrackerPokemon, stat: Stat): Double {
        val boost = mon.boosts[stat] ?: 0

        val actualBoost = if (boost > 1) {
            (2 + boost) / 2.0
        } else {
            2 / (2.0 - boost)
        }

        val baseStat = getBaseStats(mon.pokemon!!, stat.showdownId) ?: 0
        return ((2 * baseStat + 31) + 5) * actualBoost
    }

    // gets the slot number of the passed-in move
    fun getMoveSlot(move: String, possibleMoves: List<InBattleMove>?): String {
        val bestMoveSlotIndex = possibleMoves?.indexOfFirst { it.id == move }?.plus(1)
        return "move $bestMoveSlotIndex"
    }

    // gets the slot number of the bestMatchup pokemon in the team
    /*fun getPokemonPos(request: ShowdownActionRequest, bestMatchup: Pokemon): Int {
        return request.side?.pokemon?.indexOfFirst {
            it.details == bestMatchup.details && getHpFraction(it.condition) > 0 && !it.active
        } + 1
    }*/

    // returns an approximate number of hits for a given move for estimation purposes
    /*fun expectedHits(move: String): Double {
        val moveData = dex.getMove(move) // todo find equivalent of what this wants
        return when (move) {
            "triplekick", "tripleaxel" -> 1 + 2 * 0.9 + 3 * 0.81
            "populationbomb" -> 7.0
            else -> moveData.multihit?.let { (2 + 3) / 3.0 + (4 + 5) / 6.0 } ?: 1.0
        }
    }*/

    /*fun chooseSwitch(request: ShowdownActionRequest, battle: PokemonBattle, switches: List<SwitchOption>): Int {
        updateActiveTracker(battle)
        val availableSwitches = request.side?.pokemon?.filter { !it.active && getHpFraction(it.condition) > 0 }
        if (availableSwitches!!.isEmpty()) return 1

        val bestEstimation = availableSwitches.maxOfOrNull { estimateMatchup(request, it) }
        val bestMatchup = availableSwitches.find { estimateMatchup(request, it) == bestEstimation }
        getCurrentPlayer(battle)[0].firstTurn = 1

        return bestMatchup?.let { getPokemonPos(request, it) } ?: 1
    }*/

    /*fun chooseTeamPreview(request: ShowdownActionRequest, battle: PokemonBattle, team: List<AnyObject>): String {
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
    }*/

    fun moveDamageMultiplier(moveID: String, defender: Pokemon): Double {
        val move = Moves.getByName(moveID)
        val defenderTypes = defender.types
        var multiplier = 1.0

        for (defenderType in defenderTypes)
            multiplier *= (getDamageMultiplier(move!!.elementalType, defenderType) ?: 1.0)

        return multiplier
    }

    fun bestDamageMultiplier(attacker: Pokemon, defender: Pokemon): Double { // todo copy all to make overload
        //val typeMatchups = JSON.parse(File("../Data/UsefulDatasets/type-chart.json").readText())
        //val atkMoveType = attackMove.type
        val attackerMoves = attacker.moveSet.getMoves()

        val defenderTypes = defender.types

        var multiplier = 1.0
        var bestMultiplier = 1.0

        for (attackerMove in attackerMoves) {
            for (defenderType in defenderTypes) {
                multiplier *= (getDamageMultiplier(attackerMove.type, defenderType) ?: 1.0)
            }

            if (multiplier > bestMultiplier) {
                bestMultiplier = multiplier
            }

            multiplier = 1.0
        }

        return bestMultiplier
    }

    // The move options provided by the simulator have been converted from the name
    // which we're tracking, so we need to convert them back.
    /*private fun fixMove(m: Move): String {
        val id = toID(m.move)
        return when {
            id.startsWith("return") -> "return"
            id.startsWith("frustration") -> "frustration"
            id.startsWith("hiddenpower") -> "hiddenpower"
            else -> id
        }
    }*/

    // returns an approximate number of hits for a given move for estimation purposes
    fun expectedHits(move: MoveTemplate): Int {
        val minMaxHits = multiHitMoves[move.name]
        if (move.name == "triplekick" || move.name == "tripleaxel") {
            //Triple Kick and Triple Axel have an accuracy check for each hit, and also
            //rise in BP for each hit
            return (1 + 2 * 0.9 + 3 * 0.81).toInt()
        }
        if (move.name == "populationbomb") {
            // population bomb hits until it misses, 90% accuracy
            return 7
        }
        if (minMaxHits == null)
            // non multihit move
            return 1
        else if (minMaxHits[0] == minMaxHits[1])
            return minMaxHits[0]!!
        else {
            // It hits 2-5 times
            return (2 + 3) / 3 + (4 + 5) / 6
        }
    }

    private fun getHpFraction(condition: String): Double {
        if (condition == "0 fnt") return 0.0
        val (numerator, denominator) = condition.split('/').map { it.toInt() }
        return numerator.toDouble() / denominator
    }

    /*private fun getCurrentHp(condition: String): Int {
        if (condition == "0 fnt") return 0
        return condition.split('/')[0].toInt()
    }*/

    private fun getNonZeroStats(name: String): Map<Stat, Int> {
        return boostFromMoves[name] ?: emptyMap()
        //boostFromMoves.filterKeys { it == name }.filter { return it.value ?: emptyMap() }
    }

    private fun updateActiveTracker(battle: PokemonBattle) {
        // I think is the first side pokemon
        val p1 = activeTracker.p1Active
        val pokemon1 = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon
        val p1Boosts = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.statChanges
        val playerSide1 = battle.side1.actors.first()
        // todo find out how to get stats
        //val p1Stats = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.

        // convert p1Boosts to a regular Map rather than a MutableMap
        //val p1BoostsMap = p1Boosts?.mapKeys { it.key.toString() } ?: mapOf()
        val p1BoostsMap = p1Boosts?.mapKeys { it.key } ?: mapOf()

        // opposing pokemon to the first side pokemon
        val p2 = activeTracker.p2Active


        val pokemon2 = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon
        val p2Boosts = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.statChanges
        val playerSide2 = battle.side2.actors.first()
        //val nextAvailablePokemon = playerSide2.pokemonList.filter { it.health != 0 }.first().effectedPokemon.uuid
        // todo make nice function for knowing what is the best switchout

        // convert p2Boosts to a regular Map rather than a MutableMap
        //val p2BoostsMap = p2Boosts?.mapKeys { it.key.toString() } ?: mapOf()
        val p2BoostsMap = p2Boosts?.mapKeys { it.key } ?: mapOf()

        p1.pokemon = pokemon1
        p1.species = pokemon1!!.species.name
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
        p2.species = pokemon2!!.species.name
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
        val mon = battle.side1.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon
        val opponent = battle.side2.activePokemon.firstOrNull()?.battlePokemon?.effectedPokemon

        //val mon = if (request.side?.id == "p1") activeTracker.p1Active else activeTracker.p2Active
        //val opponent = if (request.side?.id == "p1") activeTracker.p2Active else activeTracker.p1Active

        return Pair(mon!!, opponent!!)
    }


}


