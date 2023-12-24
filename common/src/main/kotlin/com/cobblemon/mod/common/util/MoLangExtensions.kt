/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangEnvironment
import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.MoScope
import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.MoValue
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.util.math.Vec3d

val genericRuntime = MoLangRuntime().setup()

fun MoLangRuntime.resolve(expression: Expression): MoValue = expression.evaluate(MoScope(), environment)
fun MoLangRuntime.resolveDouble(expression: Expression): Double = resolve(expression).asDouble()
fun MoLangRuntime.resolveFloat(expression: Expression): Float = resolve(expression).asDouble().toFloat()
fun MoLangRuntime.resolveInt(expression: Expression): Int = resolveDouble(expression).toInt()

fun MoLangRuntime.resolveBoolean(expression: Expression): Boolean = resolve(expression).asDouble() != 0.0
fun MoLangRuntime.resolveVec3d(triple: Triple<Expression, Expression, Expression>) = Vec3d(
    resolveDouble(triple.first),
    resolveDouble(triple.second),
    resolveDouble(triple.third)
)

fun MoLangRuntime.resolveBoolean(expression: Expression, pokemon: Pokemon): Boolean {
    environment.writePokemon(pokemon)
    return resolveBoolean(expression)
}

fun MoLangRuntime.resolveDouble(expression: Expression, pokemon: Pokemon): Double {
    environment.writePokemon(pokemon)
    return resolveDouble(expression)
}

fun MoLangRuntime.resolveInt(expression: Expression, pokemon: Pokemon): Int {
    environment.writePokemon(pokemon)
    return resolveInt(expression)
}

fun MoLangRuntime.resolveFloat(expression: Expression, pokemon: Pokemon): Float {
    environment.writePokemon(pokemon)
    return resolveFloat(expression)
}


fun MoLangRuntime.resolveBoolean(expression: Expression, pokemon: BattlePokemon): Boolean {
    environment.writePokemon(pokemon)
    return resolveBoolean(expression)
}

fun MoLangRuntime.resolveDouble(expression: Expression, pokemon: BattlePokemon): Double {
    environment.writePokemon(pokemon)
    return resolveDouble(expression)
}

fun MoLangRuntime.resolveInt(expression: Expression, pokemon: BattlePokemon): Int {
    environment.writePokemon(pokemon)
    return resolveInt(expression)
}

fun MoLangRuntime.resolveFloat(expression: Expression, pokemon: BattlePokemon): Float {
    environment.writePokemon(pokemon)
    return resolveFloat(expression)
}


fun Expression.getString() = originalString ?: "0"
fun Double.asExpression() = NumberExpression(this)
fun String.asExpression() = try {
    MoLang.createParser(if (this == "") "0.0" else this).parseExpression()
} catch (exc: Exception) {
    Cobblemon.LOGGER.error("Failed to parse MoLang expression: $this")
    throw exc
}
fun MoLangEnvironment.writePokemon(pokemon: Pokemon) {
    val pokemonStruct = VariableStruct()
    pokemon.writeVariables(pokemonStruct)
    setSimpleVariable("pokemon", pokemonStruct)
}

fun MoLangEnvironment.writePokemon(pokemon: BattlePokemon) {
    val pokemonStruct = VariableStruct()
    pokemon.writeVariables(pokemonStruct)
    setSimpleVariable("pokemon", pokemonStruct)
}