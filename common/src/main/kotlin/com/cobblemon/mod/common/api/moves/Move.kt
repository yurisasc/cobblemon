/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.moves.animations.ActionEffectTimeline
import com.cobblemon.mod.common.api.moves.categories.DamageCategory
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.registry.RegistryElement
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.battles.MoveTarget
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.DataKeys
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import kotlin.math.ceil
import kotlin.properties.Delegates

/**
 * Representing a Move based on some template and with current PP and the number of raised PP stages.
 */
open class Move(
    val template: MoveTemplate,
    currentPp: Int,
    raisedPpStages: Int = 0
): RegistryElement<MoveTemplate>, ShowdownIdentifiable {
    private var emit = true
    val observable = SimpleObservable<Move>()

    var currentPp: Int by Delegates.observable(currentPp) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            update()
        }
    }

    var raisedPpStages: Int by Delegates.observable(raisedPpStages) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            update()
        }
    }

    fun doThenUpdate(action: () -> Unit) {
        val oldEmit = emit
        emit = false
        action()
        emit = oldEmit
        update()
    }

    fun update() {
        if (emit) {
            observable.emit(this)
        }
    }

    val power: Int
        get() = this.template.power
    val accuracy: Float
        get() = this.template.accuracy
    val pp: Int
        get() = this.template.pp
    val damageCategory: DamageCategory
        get() = this.template.damageCategory
    val type: ElementalType
        get() = this.template.type
    val priority: Int
        get() = this.template.priority
    val target: MoveTarget
        get() = this.template.target
    val flags: Set<MoveFlag>
        get() = this.template.flags
    val noPpBoosts: Boolean
        get() = this.template.noPpBoosts
    val critRatio: Float
        get() = this.template.critRatio
    val effectChances: Array<Float>
        get() = this.template.effectChances
    val actionEffect: ActionEffectTimeline?
        get() = this.template.actionEffect
    val displayName: Component
        get() = this.template.displayName
    val description: Component
        get() = this.template.description
    val maxPp: Int
        get() {
            if (this.template.noPpBoosts) {
                return this.template.pp
            }
            return this.template.pp + this.raisedPpStages * this.template.pp / 5
        }

    /**
     * Raises the max PP and rescales the current PP value.
     *
     * @param amount
     * @return False if already maxed or [noPpBoosts] is true.
     */
    fun raiseMaxPP(amount: Int): Boolean {
        if (this.noPpBoosts) {
            return false
        }
        val oldPp = maxPp
        doThenUpdate {
            val ppRatio = currentPp / maxPp.toFloat()
            raisedPpStages += amount
            if (raisedPpStages > 3) {
                raisedPpStages = 3
            }
            currentPp = ceil(ppRatio * maxPp).toInt()
        }
        return oldPp != maxPp
    }

    override fun showdownId(): String = this.template.showdownId()

    override fun registry(): Registry<MoveTemplate> = this.template.registry()

    override fun resourceKey(): ResourceKey<MoveTemplate> = this.template.resourceKey()

    override fun resourceLocation(): ResourceLocation = this.template.resourceLocation()

    override fun isTaggedBy(tag: TagKey<MoveTemplate>): Boolean = this.template.isTaggedBy(tag)

    companion object {
        @JvmStatic
        val CODEC: Codec<Move> = RecordCodecBuilder.create { it.group(
            CobblemonRegistries.MOVE.byNameCodec().fieldOf(DataKeys.POKEMON_MOVESET_MOVENAME).forGetter(Move::template),
            Codec.intRange(0, Int.MAX_VALUE).fieldOf(DataKeys.POKEMON_MOVESET_MOVEPP).forGetter(Move::currentPp),
            Codec.intRange(0, 3).fieldOf(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES).forGetter(Move::raisedPpStages)
        ).apply(it, ::Move) }

        @JvmStatic
        val PACKET_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC)
    }
}