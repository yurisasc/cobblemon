/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.dialogue

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.entity.Poseable
import java.util.UUID
import kotlin.math.atan
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.util.Identifier
import org.joml.Quaternionf

/**
 * Some time of face that can be rendered in a dialogue.
 *
 * @author Hiroku
 * @since January 1st, 2024
 */
sealed interface RenderableFace {
    fun render(drawContext: DrawContext, partialTicks: Float)
}

class PlayerRenderableFace(val playerId: UUID) : RenderableFace {
    override fun render(drawContext: DrawContext, partialTicks: Float) {
        val entity = MinecraftClient.getInstance().world?.getPlayerByUuid(playerId) ?: return
        val f = atan((-20 / 40.0f).toDouble()).toFloat()
        val g = atan((5 / 40.0f).toDouble()).toFloat()
        val quaternionf = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(g * 20.0f * (Math.PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)
        val h: Float = entity.bodyYaw
        val i: Float = entity.getYaw()
        val j: Float = entity.getPitch()
        val k: Float = entity.prevHeadYaw
        val l: Float = entity.headYaw
        drawContext.matrices.push()
        drawContext.matrices.translate(0.0, 0.0, 0.0)
        entity.bodyYaw = 180.0f + f * 20.0f
        entity.setYaw(180.0f + f * 40.0f)
        entity.setPitch(-g * 20.0f)
        entity.headYaw = entity.getYaw()
        entity.prevHeadYaw = entity.getYaw()
        InventoryScreen.drawEntity(drawContext, 0, 75, 37, quaternionf, quaternionf2, entity)
        entity.bodyYaw = h
        entity.setYaw(i)
        entity.setPitch(j)
        entity.prevHeadYaw = k
        entity.headYaw = l
        drawContext.matrices.pop()
    }
}

class ReferenceRenderableFace(entity: Poseable): RenderableFace {
    val state = entity.delegate as PoseableEntityState<*>
    override fun render(drawContext: DrawContext, partialTicks: Float) {
        val state = this.state
        if (state is PokemonClientDelegate) {
            drawPortraitPokemon(
                species = state.currentEntity.pokemon.species,
                aspects = state.currentEntity.pokemon.aspects,
                matrixStack = drawContext.matrices,
                state = state,
                partialTicks = 0F // It's already being rendered potentially so we don't need to tick the state.
            )
        }
    }
}

class ArtificialRenderableFace(
    modelType: String,
    val identifier: Identifier,
    val aspects: Set<String>
): RenderableFace {
    val species = PokemonSpecies.getByIdentifier(identifier) ?: run {
        Cobblemon.LOGGER.error("Unable to find species for $identifier for a dialogue face. Defaulting to first species.")
        PokemonSpecies.species.first()
    }
    val state: PoseableEntityState<*> = if (modelType == "pokemon") {
        PokemonFloatingState()
    } else {
        throw IllegalArgumentException("Unknown model type: $modelType")
    }

    override fun render(drawContext: DrawContext, partialTicks: Float) {
        val state = this.state
        if (state is PokemonFloatingState) {
            drawPortraitPokemon(
                species = species,
                aspects = aspects,
                matrixStack = drawContext.matrices,
                state = state,
                partialTicks = partialTicks
            )
        }
    }
}