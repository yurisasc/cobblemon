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

class SlowFallEffect: ShoulderEffect {
    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        private const val SLOW_AFTER_PROPERTY = "slowAfter"
        private val observeMap = mutableMapOf<ServerPlayer, SlowFallEffect>()

        @SubscribeEvent
        fun onLivingUpdate(event: LivingEvent.LivingUpdateEvent) {
            if (event.entity !is ServerPlayer) return
            val player = event.entity as ServerPlayer
            if (!observeMap.containsKey(player)) return

            if (player.fallDistance > 0) observeMap[player]?.onFall(player)
        }
    }

    private var slowAfter = 5

    override fun applyEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean) {
        observeMap[player] = this
    }

    override fun removeEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean) {
        observeMap.remove(player)
    }

    fun onFall(player: ServerPlayer) {
        if (player.fallDistance >= slowAfter) {
            player.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 60))
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