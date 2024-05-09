/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class TradePokemonContext(val traded: Pokemon, val received: Pokemon)

class TradePokemonCriterion(
    playerCtx: Optional<LootContextPredicate>,
    val traded: String,
    val received: String,
    val tradedHeldItem: String,
    val receivedHeldItem: String
): SimpleCriterionCondition<TradePokemonContext>(playerCtx) {

    companion object {
        val CODEC: Codec<TradePokemonCriterion> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(TradePokemonCriterion::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "traded", "any").forGetter(TradePokemonCriterion::traded),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "received", "any").forGetter(TradePokemonCriterion::received),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "traded_held_item", "minecraft:air").forGetter(TradePokemonCriterion::tradedHeldItem),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "received_held_item", "minecraft:air").forGetter(TradePokemonCriterion::receivedHeldItem)
        ).apply(it, ::TradePokemonCriterion) }
    }

    override fun matches(player: ServerPlayerEntity, context: TradePokemonContext): Boolean {
        val heldItem1 = context.traded.heldItem().item.registryEntry.registryKey().value
        val heldItem2 = context.received.heldItem().item.registryEntry.registryKey().value

        if (traded != "any" && context.traded.species.resourceIdentifier != traded.asIdentifierDefaultingNamespace()) {
            return false
        }

        if (received != "any" && context.received.species.resourceIdentifier != received.asIdentifierDefaultingNamespace()) {
            return false
        }

        if (heldItem1 != tradedHeldItem.asIdentifierDefaultingNamespace() && heldItem1 != "minecraft:air".asIdentifierDefaultingNamespace()) {
            return false
        }

        if (heldItem2 != receivedHeldItem.asIdentifierDefaultingNamespace() && heldItem2 != "minecraft:air".asIdentifierDefaultingNamespace()) {
            return false
        }

        return true
    }
}

/*class TradePokemonCriterionCondition(id: Identifier, entity: LootContextPredicate) : SimpleCriterionCondition<TradePokemonContext>(id, entity) {
    var traded = "any"
    var received = "any"
    var tradedHeldItem = "any"
    var receivedHeldItem = "any"
    override fun toJson(json: JsonObject) {
        json.addProperty("traded", traded)
        json.addProperty("received", received)
        json.addProperty("traded_held_item", tradedHeldItem)
        json.addProperty("received_held_item", receivedHeldItem)
    }

    override fun fromJson(json: JsonObject) {
        traded = json.get("traded")?.asString ?: "any"
        received = json.get("received")?.asString ?: "any"
        tradedHeldItem = json.get("traded_held_item")?.asString ?: "minecraft:air"
        receivedHeldItem = json.get("received_held_item")?.asString ?: "minecraft:air"
    }

    override fun matches(player: ServerPlayerEntity, context: TradePokemonContext): Boolean {
        val heldItem1 = context.traded.heldItem().item.registryEntry.registryKey().value
        val heldItem2 = context.received.heldItem().item.registryEntry.registryKey().value
        return (context.traded.species.resourceIdentifier == traded.asIdentifierDefaultingNamespace() || traded == "any") &&
                (context.received.species.resourceIdentifier == received.asIdentifierDefaultingNamespace() || received == "any") &&
                (heldItem1 == tradedHeldItem.asIdentifierDefaultingNamespace() || heldItem1 == "minecraft:air".asIdentifierDefaultingNamespace()) &&
                (heldItem2 == receivedHeldItem.asIdentifierDefaultingNamespace() || heldItem2 == "minecraft:air".asIdentifierDefaultingNamespace())
    }
}

open class TradePokemonContext(val traded: Pokemon, val received: Pokemon)*/