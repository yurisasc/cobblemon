package com.cablemc.pokemoncobbled.common.pokemon.effects

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

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

        private const val SLOW_AFTER_PROPERTY = "slowAfter"
        private val observeMap = mutableMapOf<ServerPlayer, SlowFallEffect>()
        private val isFalling = mutableSetOf<ServerPlayer>()

        @SubscribeEvent
        fun onLivingUpdate(event: LivingEvent.LivingUpdateEvent) {
            if (event.entity !is ServerPlayer) return
            val player = event.entity as ServerPlayer
            if (!observeMap.containsKey(player)) return

            if (player.fallDistance > 0) observeMap[player]?.onFall(player)
            if (isFalling.contains(player) && player.fallDistance == 0F) observeMap[player]?.onFallEnd(player)
        }
    }

    /**
     * Amount of Blocks the [ServerPlayer] needs to fall to trigger the [SlowFallEffect]
     */
    private var slowAfter = 5

    override fun applyEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean) {
        observeMap[player] = this
    }

    override fun removeEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean) {
        observeMap.remove(player)
        isFalling.remove(player)
    }

    /**
     * Triggers if the [ServerPlayer] is falling
     */
    fun onFall(player: ServerPlayer) {
        if (player.fallDistance >= slowAfter) {
            isFalling.add(player)
            player.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 60))
        }
    }

    /**
     * Triggers when the [ServerPlayer] finished falling
     */
    fun onFallEnd(player: ServerPlayer) {
        isFalling.remove(player)
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