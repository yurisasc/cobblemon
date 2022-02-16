package com.cablemc.pokemoncobbled.forge.common.pokemon.effects

import com.cablemc.pokemoncobbled.forge.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.forge.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingFallEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.UUID

/**
 * Effect that allows for slow falling after [slowAfter] blocks.
 * The value for [slowAfter] can be set per Form in the Species JSON.
 *
 * @author Qu
 * @since 2022-01-29
 */
class SlowFallEffect: ShoulderEffect {
    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }
        private val SLOW_FALLING_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABB")
        private val SLOW_FALLING = AttributeModifier(
            SLOW_FALLING_ID,
            "Slow falling acceleration reduction",
            -0.07,
            AttributeModifier.Operation.ADDITION
        ) // Add -0.07 to 0.08 so we get the vanilla default of 0.01

        private const val SLOW_AFTER_PROPERTY = "slowAfter"
        private val observeMap = mutableMapOf<ServerPlayer, SlowFallEffect>()

        @SubscribeEvent
        fun onLivingUpdate(event: LivingEvent.LivingUpdateEvent) {
            if (event.entity !is ServerPlayer) return
            val player = event.entity as ServerPlayer
            if (player !in observeMap) return

            if (!player.gameMode.isSurvival) return

            if (player.fallDistance > 0) observeMap[player]?.onFall(player)
        }

        @SubscribeEvent
        fun onFallEnd(event: LivingFallEvent) {
            if (event.entity !is ServerPlayer) return
            val player = event.entity as ServerPlayer
            if (player !in observeMap) return

            event.damageMultiplier = 0.0F
            observeMap[player]?.onFallEnd(player)
        }
    }

    /**
     * Amount of Blocks the [ServerPlayer] needs to fall to trigger the [SlowFallEffect]
     */
    private var slowAfter = 5

    override fun applyEffect(pokemon: Pokemon, player: ServerPlayer, isLeft: Boolean) {
        observeMap[player] = this
    }

    override fun removeEffect(pokemon: Pokemon, player: ServerPlayer, isLeft: Boolean) {
        observeMap.remove(player)
        removeEffect(player)
    }

    /**
     * Triggers if the [ServerPlayer] is falling
     */
    fun onFall(player: ServerPlayer) {
        if (player.fallDistance >= slowAfter) {
            addEffect(player)
        }
    }

    /**
     * Triggers when the [ServerPlayer] finished falling
     */
    fun onFallEnd(player: ServerPlayer) {
        player.resetFallDistance()
        removeEffect(player)
    }

    private fun addEffect(player: ServerPlayer) {
        player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())?.let {
            if (!it.hasModifier(SLOW_FALLING)) it.addTransientModifier(SLOW_FALLING)
        }
    }

    private fun removeEffect(player: ServerPlayer) {
        player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())?.let {
            if (it.hasModifier(SLOW_FALLING)) it.removeModifier(SLOW_FALLING)
        }
    }

    override fun serialize(json: JsonObject): JsonObject {
        json.addProperty(SLOW_AFTER_PROPERTY, slowAfter)
        return json
    }

    override fun deserialize(json: JsonObject): ShoulderEffect {
        slowAfter = json.get(SLOW_AFTER_PROPERTY).asInt
        return this
    }
}