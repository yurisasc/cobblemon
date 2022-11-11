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

interface BasicEffect : EffectData {
    val id: String
    override val effectType: EffectType
    val exists: Boolean
    val fullname: String
    val gen: Int
    val sourceEffect: String
    override fun toString(): String
}

interface AbilityEventMethods {
    val onCheckShow: (PSBattle.(pokemon: PSPokemon) -> Unit)? get() = {}
    val onEnd: (PSBattle.(target: Triple<PSPokemon, PSSide, PSField>) -> Unit)? get() = {} // target was actually meant to be a union type of those 3
    val onPreStart: (PSBattle.(pokemon: PSPokemon) -> Unit)? get() = {}
    val onStart: (PSBattle.(target: PSPokemon) -> Unit)? get() = {}
}