/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.bedrockk.molang.runtime.struct.MoStruct
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.moves.animations.ActionEffectTimeline
import com.cobblemon.mod.common.api.moves.categories.DamageCategory
import com.cobblemon.mod.common.api.registry.RegistryElement
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.battles.MoveTarget
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.simplify
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

/**
 * This class represents the base of a Move.
 * To build a Move you need to use its template
 *
 * @param elementalType The [ElementalType] of this move.
 * @param damageCategory The [DamageCategory] of this move.
 * @param power The base power of this move.
 * @param target The [MoveTarget] of this move.
 * @param accuracy The accuracy of this move.
 * @param pp The power points of this move, these mean how many times the move can be used before some manner of restoration is required.
 * @param priority The priority of this move.
 * @param critRatio The ratio at which this move will land a critical hit.
 * @param effectChances The effect chances if any ordered by effect.
 */
class MoveTemplate(
    val power: Int,
    val accuracy: Float,
    val pp: Int,
    val damageCategory: DamageCategory,
    val elementalType: ElementalType,
    val priority: Int,
    val target: MoveTarget,
    val flags: Set<MoveFlag>,
    // TODO: val damage: Optional<FixedDamage> type: number | 'level',
    val noPpBoosts: Boolean,
    // TODO: val gimmickType: Optional<GimmickMove>
    val critRatio: Double,
    val effectChances: Array<Double>,
    val actionEffect: ActionEffectTimeline?,
    val displayName: Component,
    val description: Component,
): RegistryElement<MoveTemplate>, ShowdownIdentifiable {

    val struct: MoStruct by lazy {
        QueryStruct(hashMapOf())
            .addFunction("id") { StringValue(resourceLocation().simplify()) }
            .addFunction("type") { StringValue(elementalType.resourceLocation().simplify()) }
            .addFunction("damage_category") { StringValue(damageCategory.name) }
            .addFunction("power") { DoubleValue(power) }
            .addFunction("target") { StringValue(target.name) }
            .addFunction("accuracy") { DoubleValue(accuracy) }
            .addFunction("pp") { DoubleValue(pp) }
            .addFunction("priority") { DoubleValue(priority) }
            .addFunction("crit_ratio") { DoubleValue(critRatio) }
    }
    val maxPp: Int
        get() = 8 * pp / 5

    /**
     * Creates the Move with full PP
     */
    fun create() = create(pp)

    /**
     * Creates the Move with given PP out of the normal maximum
     */
    fun create(currentPp: Int) = create(currentPp, 0)

    /**
     * Creates the Move with given current PP and the given raised PP stages.
     */
    fun create(currentPp: Int, raisedPpStages: Int): Move {
        return Move(
            currentPp = currentPp,
            raisedPpStages = raisedPpStages,
            template = this
        )
    }

    override fun registry(): Registry<MoveTemplate> = CobblemonRegistries.MOVE

    override fun resourceKey(): ResourceKey<MoveTemplate> = this.registry().getResourceKey(this)
        .orElseThrow { IllegalStateException("Unregistered MoveTemplate") }

    override fun isTaggedBy(tag: TagKey<MoveTemplate>): Boolean = this.registry()
        .getHolder(this.resourceKey())
        .orElseThrow { IllegalStateException("Unregistered MoveTemplate") }
        .`is`(tag)

    override fun showdownId(): String {
        return ShowdownIdentifiable.EXCLUSIVE_REGEX.replace(this.resourceLocation().simplify(), "")
    }
}
