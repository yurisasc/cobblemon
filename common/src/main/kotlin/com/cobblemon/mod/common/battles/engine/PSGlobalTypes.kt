package com.cobblemon.mod.common.battles.engine

interface PSEffect
interface PSBattle
interface EventInfo

// A lot of these are classes probably
interface PSPokemon
interface PSSide
interface PSField

enum class EffectType {
    Condition, Pokemon, Move, Item, Ability, Format,
    Nature, Ruleset, Weather, Status, Rule, ValidatorRule
}

enum class Nonstandard {
    Past, Future, Unobtainable, CAP,
    LGPE, Custom, Gigantamax
}

interface EffectData {
    val name: String?
    val desc: String?
    val duration: Int?
    val durationCallback: (PSBattle.(target: PSPokemon, source: PSPokemon, effect: PSEffect?) -> Int)? get() = null
    val effectType: EffectType?
    val infiltrates: Boolean?
    val isNonstandard: Nonstandard?
    val shortDesc: String?
}

typealias ModifierEffect = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ModifierMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ResultMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ExtResultMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias VoidEffect = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias VoidMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ModifierSourceEffect = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ModifierSourceMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ResultSourceMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias ExtResultSourceMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias VoidSourceEffect = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?
typealias VoidSourceMove = PSBattle.(relayVar: Number, target: PSPokemon, effect: PSEffect) -> Int?


interface BasicEffect : EffectData {
    val id: String
    override val effectType: EffectType
    val exists: Boolean
    val fullname: String
    val gen: Int
    val sourceEffect: String
    override fun toString(): String
}