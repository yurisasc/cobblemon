package com.cobblemon.mod.common.util.codec

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec

val EXPRESSION_CODEC = object : PrimitiveCodec<Expression> {
    override fun <T> read(ops: DynamicOps<T>, input: T): DataResult<Expression> {
        return ops.getStringValue(input).map { MoLang.createParser(it).parseExpression() }
    }

    override fun <T> write(ops: DynamicOps<T>, value: Expression): T {
        return ops.createString(value.attributes?.get("string") as String)
    }
}