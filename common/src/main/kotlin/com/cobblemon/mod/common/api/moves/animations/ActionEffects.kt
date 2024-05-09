/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves.animations

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.moves.animations.keyframes.*
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.pokemon.adapters.CobblemonStatTypeAdapter
import com.cobblemon.mod.common.util.adapters.*
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.awt.Color
import net.minecraft.predicate.NumberRange
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

/**
 * Data registry containing all of the [ActionEffectTimeline]s that can be triggered from various actions.
 *
 * @author Hiroku
 * @since October 21st, 2023
 */
object ActionEffects : JsonDataRegistry<ActionEffectTimeline> {
    override val id: Identifier = cobblemonResource("action_effects")
    override val type: ResourceType = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<ActionEffects>()

    init {
        ActionEffectKeyframe.register<AnimationActionEffectKeyframe>("animation")
        ActionEffectKeyframe.register<EntityMoLangActionEffectKeyframe>("entity_molang")
        ActionEffectKeyframe.register<MoLangActionEffectKeyframe>("molang")
        ActionEffectKeyframe.register<ParallelActionEffectKeyframe>("parallel")
        ActionEffectKeyframe.register<CanInterruptActionEffectKeyframe>("can_interrupt")
        ActionEffectKeyframe.register<CannotInterruptActionEffectKeyframe>("cannot_interrupt")
        ActionEffectKeyframe.register<RemoveHoldsActionEffectKeyframe>("remove_holds")
        ActionEffectKeyframe.register<AddHoldsActionEffectKeyframe>("add_holds")
        ActionEffectKeyframe.register<MoveToTargetActionEffectKeyframe>("move_to_target")
        ActionEffectKeyframe.register<ReturnToPositionActionEffectKeyframe>("return_to_position")
        ActionEffectKeyframe.register<PauseActionEffectKeyframe>("pause")
        ActionEffectKeyframe.register<SavePositionActionEffectKeyframe>("save_position")
        ActionEffectKeyframe.register<ForkActionEffectKeyframe>("fork")
        ActionEffectKeyframe.register<SequenceActionEffectKeyframe>("sequence")
        ActionEffectKeyframe.register<RunActionEffectKeyframe>("run_action_effect")
        ActionEffectKeyframe.register<EntityParticlesActionEffectKeyframe>("entity_particles")
        ActionEffectKeyframe.register<EntitySoundActionEffectKeyframe>("entity_sound")
    }

    override val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .registerTypeAdapter(ActionEffectKeyframe::class.java, ActionEffectKeyframeAdapter)
        .registerTypeAdapter(NumberRange.DoubleRange::class.java, FloatNumberRangeAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(Collection::class.java, Box::class.java).type, BoxCollectionAdapter)
        .registerTypeAdapter(Box::class.java, BoxAdapter)
        .registerTypeAdapter(Vec3d::class.java, VerboseVec3dAdapter)
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(IntRange::class.java, VerboseIntRangeAdapter)
        .registerTypeAdapter(Color::class.java, LiteralHexColorAdapter)
        .registerTypeAdapter(Stat::class.java, CobblemonStatTypeAdapter)
        .registerTypeAdapter(Expression::class.java, ExpressionAdapter)
        .registerTypeAdapter(ExpressionLike::class.java, ExpressionLikeAdapter)
        .registerTypeAdapter(
            TypeToken.getParameterized(
                TypeToken.get(List::class.java).type,
                TypeToken.get(ActionEffectKeyframe::class.java).type
            ).type,
            SingleToPluralAdapter(ActionEffectKeyframe::class.java) { it }
        )
        .create()

    override val typeToken: TypeToken<ActionEffectTimeline> = TypeToken.get(ActionEffectTimeline::class.java)
    override val resourcePath = "action_effects"

    val actionEffects = mutableMapOf<Identifier, ActionEffectTimeline>()
    override fun reload(data: Map<Identifier, ActionEffectTimeline>) {
        actionEffects.clear()
        actionEffects.putAll(data)
        observable.emit(this)
    }

    override fun sync(player: ServerPlayerEntity) {}
}